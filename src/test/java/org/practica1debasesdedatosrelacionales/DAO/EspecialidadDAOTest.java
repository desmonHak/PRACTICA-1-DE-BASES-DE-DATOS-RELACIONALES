package org.practica1debasesdedatosrelacionales.DAO;

import org.junit.jupiter.api.Test;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMongoDBSingleton;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnectionMySQLDBSingleton;
import org.practica1debasesdedatosrelacionales.domain.Cita;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EspecialidadDAOTest {

    @Test
    void connect() throws SQLException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;

        EspecialidadDAO espe =  new EspecialidadDAO();

        EspecialidadDAO.modo_debug = true;
        espe.connect(ConnectionMongoDBSingleton.class);
        System.out.println("Conexion para MongoDB: " + espe.getConn());
        for (String cita : (List<String>) espe.load_especialidades()) {
            System.out.println(cita);
        }

        espe.connect(ConnectionMySQLDBSingleton.class);
        System.out.println("Conexion para MySQL: " + espe.getConn());
        for (String cita : (List<String>) espe.load_especialidades()) {
            System.out.println(cita);
        }
    }

    @Test
    void load_especialidades() {
    }
}