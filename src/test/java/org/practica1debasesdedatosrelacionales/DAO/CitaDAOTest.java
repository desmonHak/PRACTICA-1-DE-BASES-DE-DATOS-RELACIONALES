package org.practica1debasesdedatosrelacionales.DAO;

import org.junit.jupiter.api.Test;
import org.practica1debasesdedatosrelacionales.domain.Cita;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CitaDAOTest {
/*
    @Test
    void connect() throws SQLException, IOException {
        CitaDAO conn = new CitaDAO();
        conn.connect();

        conn.desconnect();
    }

    @Test
    void insert() throws SQLException, IOException {
        CitaDAO conn = new CitaDAO();
        conn.connect();
        DNI dni = new DNI("67984567V");

        try {
            conn.insert(new Cita(
                    dni,
                    Date.valueOf(LocalDate.now()),
                    Especialidad.Radiografia,
                    2
            ));
        } catch (SQLIntegrityConstraintViolationException _){}

        List<Cita> citas = conn.select(dni);
        for (Cita cita : citas) {
            System.out.println(cita);
        }
        conn.desconnect();
    }

    @Test
    void delete() throws SQLException, IOException {
        CitaDAO conn = new CitaDAO();
        conn.connect();
        DNI dni = new DNI("67984567V");
        try {
            conn.insert(new Cita(
                    dni,
                    Date.valueOf(LocalDate.now()),
                    Especialidad.Radiografia,
                    2
            ));
        } catch (SQLIntegrityConstraintViolationException _){}

        List<Cita> citas = conn.select(dni);
        for (Cita cita : citas) {
            System.out.println(cita);
        }
        conn.delete(dni);

        try {
            citas = conn.select(dni);
            for (Cita cita : citas) {
                System.out.println(cita);
            }
        } catch (SQLException e) {
            System.out.println("La cita fue eliminada correctamente");
        }

        conn.desconnect();
    }

    @Test
    void select() throws SQLException, IOException {
        CitaDAO conn = new CitaDAO();
        conn.connect();
        List<Cita> citas = conn.select(new DNI("48901940F"));
        for (Cita cita : citas) {
            System.out.println(cita);
        }
        conn.desconnect();
    }*/

}