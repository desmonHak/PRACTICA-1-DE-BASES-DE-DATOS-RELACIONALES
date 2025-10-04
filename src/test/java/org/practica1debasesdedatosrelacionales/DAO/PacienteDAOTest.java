package org.practica1debasesdedatosrelacionales.DAO;

import org.junit.jupiter.api.Test;
import org.practica1debasesdedatosrelacionales.domain.Cita;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;
import org.practica1debasesdedatosrelacionales.domain.Paciente;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PacienteDAOTest {


    @Test
    void connect() throws SQLException, IOException {
        PacienteDAO conn = new PacienteDAO();
        conn.connect();

        conn.desconnect();
    }

    @Test
    void insert() throws SQLException, IOException {
        PacienteDAO conn = new PacienteDAO();
        conn.connect();
        DNI dni = new DNI("67984567V");

        try {
            conn.insert(new Paciente(
                    dni,
                    "Perez",
                    "hola@gmail.com",
                    "1234",
                    "456234123",
                    "C/Fruteria"
            ));
        } catch (SQLIntegrityConstraintViolationException _){}

        Paciente paciente = conn.select(dni);
        System.out.println(paciente);
        conn.desconnect();
    }

    @Test
    void delete() throws SQLException, IOException {
        PacienteDAO conn = new PacienteDAO();
        conn.connect();
        DNI dni = new DNI("77984567P");
        try {
            conn.insert(new Paciente(
                    dni,
                    "Ana",
                    "Ana@gmail.com",
                    "1234",
                    "111222333",
                    "C/China"
            ));
        } catch (SQLIntegrityConstraintViolationException _){}

        Paciente paciente = conn.select(dni);
        System.out.println(paciente);

        conn.delete(dni);

        try {
            paciente = conn.select(dni);
            System.out.println(paciente);
        } catch (SQLException e) {
            System.out.println("El paciente fue eliminada correctamente");
        }

        conn.desconnect();
    }

    @Test
    void select() throws SQLException, IOException {
        PacienteDAO conn = new PacienteDAO();
        conn.connect();
        Paciente paciente = conn.select(new DNI("67984567V"));
        System.out.println(paciente);

        ProcessResulSet process = (ResultSet rs)->{
            return new Paciente(
                    new DNI(rs.getString(1)),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6)
            );
        };

        paciente = conn.select("email", "hola@gmail.com", process);
        System.out.println(paciente);

        conn.desconnect();
    }
}