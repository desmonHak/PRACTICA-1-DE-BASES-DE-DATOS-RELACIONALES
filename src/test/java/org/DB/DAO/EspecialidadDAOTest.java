package org.DB.DAO;

import org.junit.jupiter.api.Test;
import org.DB.DAO.ManagerConnections.ConnectionMongoDBSingleton;
import org.DB.DAO.ManagerConnections.ConnectionMySQLDBSingleton;
import org.DB.InitWindows;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

class EspecialidadDAOTest {

    @Test
    void connect() throws SQLException, IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ConnectionMongoDBSingleton.modo_debug = true;
        ConnectionMySQLDBSingleton.modo_debug = true;

        EspecialidadDAO espe =  new EspecialidadDAO(InitWindows.managerDBClass);

        System.out.println("Conexion para MongoDB: " + espe.getConn());
        for (String cita : (List<String>) espe.load_especialidades()) {
            System.out.println(cita);
        }


        System.out.println("Conexion para MySQL: " + espe.getConn());
        for (String cita : (List<String>) espe.load_especialidades()) {
            System.out.println(cita);
        }
    }

    @Test
    void load_especialidades() {
    }
}