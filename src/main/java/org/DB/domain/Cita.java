package org.DB.domain;

import java.sql.Date;

public class Cita {

    private DNI dni;
    private Integer numero_cita;
    private Date fecha_cita;
    private Especialidad especialidad;

    public Cita(DNI dni, Date fecha_cita, Especialidad especialidad, Integer numero_cita) {
        this.dni = dni;
        this.fecha_cita = fecha_cita;
        this.especialidad = especialidad;
        this.numero_cita = numero_cita;
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

    @Override
    public String toString() {
        return "Citas{" +
                "dni=" + dni +
                ", numero_cita=" + numero_cita +
                ", fecha_cita=" + fecha_cita +
                ", especialidad=" + especialidad +
                '}';
    }
}
