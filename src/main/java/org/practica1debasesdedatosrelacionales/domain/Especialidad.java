package org.practica1debasesdedatosrelacionales.domain;

import java.util.ArrayList;

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

    @Override
    public String toString() {
        return nombre;
    }

}
