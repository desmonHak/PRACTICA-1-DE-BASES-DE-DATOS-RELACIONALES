package org.DB.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.DB.util.SqlDateFromMongoDeserializer;

import javax.persistence.*;
import java.sql.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "Cita")
@Table(name = "Citas")
public class Cita {

    /**
     * Al usar
     *     @ManyToOne
     *     @JoinColumn(name = "dni") // campo primario con el que realizar el join
     *     private Paciente paciente;
     *
     * El atributo DNI esta siendo duplicado, pero necesito mantenerlo para
     * trabajar con lo ya existente, para eso indicare que no se insertara
     * ni se actualizara este campo
     */
    //@Column(name="dni", insertable = false, updatable = false)  // Solo para lectura en hibernate
    /**
     * Campo auxiliar solo de lectura (opcional)
     * para exponer el DNI sin tocar la relación.
     */
    //@Column(name = "dni", length = 9, nullable = false)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "dni", column = @Column(name = "dni"))
    })
    private DNI dni;


    @Id
    @Column(name="numero_cita")
    @GeneratedValue(strategy = IDENTITY) // auto generar ID en hibernate
    private Integer numero_cita;

    /**
     * Deserializar las fechas usando la clase SqlDateFromMongoDeserializer
     */
    @JsonDeserialize(using = SqlDateFromMongoDeserializer.class)
    @Column(name="fecha_cita")
    private Date fecha_cita;

    //@Column(name = "especialidad") // no se puede aplicar con ManyToOne
    @JoinColumn(name = "especialidad") // columna FK en la tabla Citas
    @ManyToOne
    private Especialidad especialidad;


    // Relación con Paciente, sin volver a insertar/actualizar el DNI.
    @ManyToOne//(fetch = FetchType.EAGER)
    @JoinColumn(name = "dni", referencedColumnName = "dni", insertable = false, updatable = false)    private Paciente paciente;

    public Cita() {
        this.paciente = null; // se puede setear después
    }



    @PostLoad
    public void syncAfterLoad() {
        if (paciente != null) {
            // el DNI es mapeado en paciente, y no en la Cita, asi
            // se evita el duplicado de campos, este metodo sincroniza los
            // datos, y se auto ejecuta una vez se obtienen de la DB
            this.dni = paciente.getDni();
        }
    }



    public Cita(DNI dni, Date fecha_cita, Especialidad especialidad, Integer numero_cita) {
        this.dni = dni;
        this.fecha_cita = fecha_cita;
        this.especialidad = especialidad;
        this.numero_cita = numero_cita;
        this.paciente = null; // se puede setear después
    }

    public Cita(DNI dni, Date fecha_cita, Especialidad especialidad) {
        this.dni = dni;
        this.fecha_cita = fecha_cita;
        this.especialidad = especialidad;
        this.paciente = null; // se puede setear después
    }

    public DNI getDni() {
        return dni;
    }

    public void setDni(DNI dni) {
        this.dni = dni;
    }

    public Integer getNumero_cita() {
        return numero_cita;
    }

    public void setNumero_cita(Integer numero_cita) {
        this.numero_cita = numero_cita;
    }

    public Date getFecha_cita() {
        return fecha_cita;
    }

    public void setFecha_cita(Date fecha_cita) {
        this.fecha_cita = fecha_cita;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) {
            this.dni = paciente.getDni(); // sincronizamos el DNI transitorio
        }
    }

    @Override
    public String toString() {
        return "Cita{" + "\n" +
                "\tdni=" + dni + "\n" +
                "\tnumero_cita=" + numero_cita + "\n" +
                "\tfecha_cita=" + fecha_cita + "\n" +
                "\tespecialidad=" + especialidad + "\n" +
                "\tpaciente=" + paciente + "\n" +
                '}';
    }
}
