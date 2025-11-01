package org.DB.domain;

/**
 * Clase para almacenar todas las especialidades de la base de datos
 */
public class Especialidad {

    /**
     * Las especialidades contienen un nombre
     */
    String nombre;

    public Especialidad(String nombre) {
        this.nombre = nombre;
    }

    public Especialidad() {
    }

    @Override
    public String toString() {
        return nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
