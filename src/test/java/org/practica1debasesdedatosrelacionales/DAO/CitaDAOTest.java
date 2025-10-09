package org.practica1debasesdedatosrelacionales.DAO;

import org.junit.jupiter.api.Test;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMongoDBSingleton;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMySQLDBSingleton;
import org.practica1debasesdedatosrelacionales.domain.Cita;
import org.practica1debasesdedatosrelacionales.domain.DNI;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CitaDAOTest {

    @Test
    void connect() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CitaDAO conn = new CitaDAO();
        conn.connect(ConnectionMongoDBSingleton.class);
    }

    @Test
    void insert() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CitaDAO conn = new CitaDAO();
        conn.connect(ConnectionMongoDBSingleton.class);
        DNI dni = new DNI("67984567V");

        try {
            conn.insert(new Cita(
                    dni,
                    Date.valueOf(LocalDate.now()),
                    new Especialidad("Radiografia"),
                    2
            ));
        } catch (SQLIntegrityConstraintViolationException _){}

        List<Cita> citas = conn.select(dni);
        for (Cita cita : citas) {
            System.out.println(cita);
        }

    }

    @Test
    void delete() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CitaDAO conn = new CitaDAO();
        conn.connect(ConnectionMongoDBSingleton.class);
        DNI dni = new DNI("67984567V");
        try {
            conn.insert(new Cita(
                    dni,
                    Date.valueOf(LocalDate.now()),
                    new Especialidad("Radiografia"),
                    2
            ));
        } catch (SQLIntegrityConstraintViolationException _){}

        List<Cita> citas = conn.select(dni);
        for (Cita cita : citas) {
            System.out.println(cita);
        }
        //conn.delete(dni);

        try {
            citas = conn.select(dni);
            for (Cita cita : citas) {
                System.out.println(cita);
            }
        } catch (SQLException e) {
            System.out.println("La cita fue eliminada correctamente");
        }


    }

    @Test
    void select() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CitaDAO conn = new CitaDAO();
        CitaDAO.modo_debug = true;
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;

        conn.connect(ConnectionMongoDBSingleton.class);
        List<Cita> citas = conn.select(new DNI("48901940F"));
        System.out.println("Numero de citas: " + citas.size());
        for (Cita cita : citas) {
            System.out.println(cita);
        }

    }

}