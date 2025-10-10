package org.practica1debasesdedatosrelacionales.DAO;

import org.junit.jupiter.api.Test;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConditionsDB;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConditionsDBOperators;
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
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CitaDAOTest {

    //Class<?> type_manager = ConnectionMongoDBSingleton.class;
    Class<?> type_manager = ConnectionMySQLDBSingleton.class;

    @Test
    public void getMaxNumeroCita() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;
        CitaDAO.modo_debug = true;

        CitaDAO citaDAO = new CitaDAO(type_manager);
        System.out.println(citaDAO.getMaxNumeroCita());
    }

    @Test
    void connect() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;
        CitaDAO.modo_debug = true;

        CitaDAO conn = new CitaDAO(type_manager);

    }

    @Test
    void insert() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;
        CitaDAO.modo_debug = true;

        CitaDAO conn = new CitaDAO(type_manager);

        DNI dni = DNI.generarDniAleatorio();

        try {
            conn.insert(new Cita(
                    dni,
                    Date.valueOf(LocalDate.now()),
                    new Especialidad("Radiografia"),
                    conn.getMaxNumeroCita() + 1
            ));
        } catch (SQLIntegrityConstraintViolationException _){}

        List<Cita> citas = conn.select(dni);
        if (citas.size() == 0) {
            System.out.println("No se inserto la cita");
        } else {
            System.out.println("Cita insertada con exito");
            for (Cita cita : citas) {
                System.out.println(cita);
            }
        }

    }

    @Test
    void delete() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;
        CitaDAO.modo_debug = true;

        CitaDAO conn = new CitaDAO(type_manager);

        DNI dni = new DNI("67984567V");
        Cita cita = null;
        // insertar la cita que vamos eliminar:
        try {
            cita = new Cita(
                    dni,
                    Date.valueOf(LocalDate.now()),
                    new Especialidad("Radiografia"),
                    conn.getMaxNumeroCita() + 1
            );
            conn.insert(cita);
        } catch (SQLIntegrityConstraintViolationException _){}

        List<Cita> citas = conn.select(dni);
        int n_citas = citas.size();
        System.out.println("Numero de citas antes de la eliminacion: " + n_citas);
        for (Cita e : citas) {
            System.out.println(e);
        }
        conn.delete(cita);

        try {
            citas = conn.select(dni);
            n_citas = citas.size();
            System.out.println("Numero de citas despues de la eliminacion: " + n_citas);
            for (Cita e : citas) {
                System.out.println(e);
            }
        } catch (SQLException e) {
            System.out.println("La cita fue eliminada correctamente");
        }

    }

    @Test
    void select() throws SQLException, IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CitaDAO conn = new CitaDAO(type_manager);
        CitaDAO.modo_debug = true;
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;

        List<Cita> citas = conn.select(new DNI("48901940F"));
        System.out.println("Numero de citas: " + citas.size());
        for (Cita cita : citas) {
            System.out.println(cita);
        }

    }

    @Test
    void update()
            throws SQLException,
            IOException,
            InvocationTargetException,
            NoSuchMethodException,
            IllegalAccessException, InstantiationException {
        CitaDAO conn = new CitaDAO(type_manager);
        CitaDAO.modo_debug = true;
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;

        DNI dni = DNI.generarDniAleatorio();

        Cita cita = new Cita(
                dni,
                Date.valueOf(LocalDate.now()),
                new Especialidad("Radiografia"),
                conn.getMaxNumeroCita() + 1
        );

        // insertar la cita a actualizar
        try { conn.insert(cita);
        } catch (SQLIntegrityConstraintViolationException _){}

        // buscar la cita insertada con los datos generados
        List<Cita> citas = conn.select(dni);
        System.out.println("Numero de citas: " + citas.size());
        for (Cita cita_ : citas) {
            System.out.println(cita_);
        }

        cita.setFecha_cita(Date.valueOf("2019-12-1"));
        cita.setEspecialidad(new Especialidad("Cardiologia"));
        conn.update(cita);

        // comprobar si los campos se actualizaron
        citas = conn.select(dni);
        System.out.println("Numero de citas: " + citas.size());
        for (Cita cita_ : citas) {
            System.out.println(cita_);
        }


    }
}