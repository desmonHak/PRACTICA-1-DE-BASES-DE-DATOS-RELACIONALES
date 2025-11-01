package org.DB.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Clase para almacenar todas las especialidades de la base de datos
 */
@Entity
@Table(name = "Especialidad")
public class Especialidad {

    /**
     * Las especialidades contienen un nombre
     */
    @Id
    @Column(name = "nombre")
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
