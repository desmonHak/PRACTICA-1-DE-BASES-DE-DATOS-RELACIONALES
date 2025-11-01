package org.DB.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.DB.util.PasswordDeserializer;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "Paciente")
public class Paciente {

    //@EmbeddedId  // Esto indica que el ID está embebido
    //@Convert(converter = DNIConverter.class)  // conversión a Stringç
    @Id
    @Column(name = "dni", length = 9, nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "dni", column = @Column(name = "dni"))
    })
    private DNI dni;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "password", length = 64)
    private String password;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @JsonDeserialize(using = PasswordDeserializer.class)
    //@Transient // <--- Hibernate lo ignorara
    @Convert(converter = SHA256Converter.class) // indica como tratar el hash
    private SHA256 hash;

    //La anotación mappedBy apunta a la entidad propietaria de la relación.
    //en este caso hay que fijarse el nombre que tiene en la clase personaje el objeto arma
    //que veras esto: private Arma arma;


    //CascadeType.ALL
    //Propaga todas las operaciones de una entidad, a la entidad con la que se relaciona.
    //Es decir, que si insertamos, actualizamos o eliminamos una entidad,
    //también se aplican estas operaciones a la entidad que se relaciona.
    //es un simil a UPDATE CASCADE, DELETE CASCADE

    //se podrian poner de forma individual. Aqui teneis las opciones
    //https://www.baeldung.com/jpa-cascade-types

    /**
     * Una paciente puede tener 0, 1 o N citas, pero una cita solo puede tener un
     * paciente, N : 1,
     *
     * FetchType.EAGER: Esto hace que Hibernate cargue todas las citas
     * del paciente automáticamente cuando se carga un Paciente, evitando el error.
     * Es lo más práctico, por que sino debo mantener la sesion abierta si quiero
     * consultar cada paciente.
     *
     * Desventaja:  con muchas relaciones, puede causar sobrecarga (demasiadas consultas).
     */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Cita> citas;

    public Paciente() {
    }

    public Paciente(DNI dni, String nombre, String email, String password, String telefono, String direccion) {
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.hash = new SHA256(password, false);
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Paciente(DNI dni, String nombre, String email, SHA256 hash, String telefono, String direccion) {
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.password = null;
        this.hash = hash;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SHA256 getHashClass() {
        return hash;
    }

    // desserializar el campo hash usando este metodo
    @JsonSetter("hash")
    public void setHashClass(String hash) {
        this.hash = new SHA256(hash, true);
    }

    public void setHashClass(SHA256 hash) {
        this.hash = hash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DNI getDni() {
        return dni;
    }

    public void setDni(DNI dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Permite obtener un hash unico para cada usuario en base
     * a toda la informacion del paciente
     * @return este hash lo usare para generar una imagen unica para
     * cada paciente.
     */
    public SHA256 get_hash_user_data() {
        return new SHA256(
            dni + nombre + email +
            password + direccion + telefono +
            hash, false
        );
    }

    @PostLoad
    public void syncAfterLoad() {
        if (password != null && this.hash == null) {
            // tanto el campo password como el campo hash son
            // hash's al obtenerlos de la DB, pero solo se obtiene
            // atraves de password, por lo que hash sera null y se debe
            // sincronizar
            this.hash = new SHA256(this.password, true);
        }
    }


    public SHA256 getHash() {
        return hash;
    }

    public void setHash(SHA256 hash) {
        this.hash = hash;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }

    /**
     * Solo para usar con hibernate, permite sincronizar las citas del paciente
     * con el DNI actual
     */
    public void syncCitasDni() {
        if (citas != null) {
            for (Cita c : citas) {
                c.setDni(this.dni);
            }
        }
    }


    @Override
    public String toString() {
        String citasStr;
        try {
            citasStr = (citas != null)
                    ? citas.stream()
                    .map(c -> "\n\tCita#" + c.getNumero_cita() + " - " + c.getEspecialidad().getNombre())
                    .collect(Collectors.joining(", "))
                    : "[]";
        } catch (Exception e) {
            citasStr = "[Lazy loading disabled]";
        }

        return "Paciente{\n" +
                "\tdni=" + dni + "\n" +
                "\tnombre='" + nombre + '\'' + "\n" +
                "\temail='" + email + '\'' + "\n" +
                "\tpassword='" + password + '\'' + "\n" +
                "\tdireccion='" + direccion + '\'' + "\n" +
                "\ttelefono='" + telefono + '\'' + "\n" +
                "\thash=" + hash + "\n" +
                "\tcitas=" + citasStr  + "\n" +
                '}';
    }
}
