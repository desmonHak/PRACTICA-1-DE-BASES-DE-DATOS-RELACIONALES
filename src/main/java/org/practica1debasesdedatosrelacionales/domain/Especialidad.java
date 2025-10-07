package org.practica1debasesdedatosrelacionales.domain;

import java.util.ArrayList;

/**
 * Clase para almacenar todas las especialidades de la base de datos
 */
public class Especialidad {

    /**
     * los datos se guarda en forma de array list, la intencio es imitar
     * el comportamiento de los enums, creando un atributo "value"
     * que devuelva la enumeracion de especialidades, originalmente,
     * esta clase no existia y se uso un enum, posteriormente se elimino
     * este y se creo esta clase con este diseño.
     */
    private static final ArrayList<Especialidad> values = new ArrayList<>();;

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

    /**
     * El metodo add permite añadir a la enumeracion(array list) una especialidad
     * nueva, emulando un enum dinamico
     * @param especialidad
     */
    public static void add(Especialidad especialidad) {
        values.add(especialidad);
    }

    /**
     * Obtener los valores obtenidos de la DB.
     * @return retorna un array list estatico.
     */
    public static ArrayList<Especialidad> values() {
        return  values;
    }

    /**
     * Permite limpiar el array estatico
     */
    public static void clear() {
        values.clear();
    }

}
