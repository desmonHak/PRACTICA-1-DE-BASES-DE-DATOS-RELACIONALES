package org.DB.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.DB.util.PasswordDeserializer;

public class Paciente {

    private DNI dni;
    private String nombre;
    private String email;
    private String password;
    private String direccion;
    private String telefono;

    @JsonDeserialize(using = PasswordDeserializer.class)
    private SHA256 hash;


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

    @Override
    public String toString() {
        return "Paciente{" +
                "dni=" + dni +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", hash=" + hash +
                '}';
    }
}
