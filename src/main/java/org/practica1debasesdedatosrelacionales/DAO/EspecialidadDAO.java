package org.practica1debasesdedatosrelacionales.DAO;

import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLDataNotFound;
import org.practica1debasesdedatosrelacionales.Exceptions.ExceptionsDB.SQLUnknownException;
import org.practica1debasesdedatosrelacionales.domain.Especialidad;
import org.practica1debasesdedatosrelacionales.domain.Paciente;
import org.practica1debasesdedatosrelacionales.domain.SHA256;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class EspecialidadDAO {
    private Connection conn;

    public Connection getConn() {
        return conn;
    }

    public void connect() throws SQLException, IOException {
        Properties configuration = new Properties();

        configuration.load(R.getProperties("databaseMongoDB.properties"));
        String host = configuration.getProperty("host");
        String port = configuration.getProperty("port");
        String name = configuration.getProperty("name");
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");

        conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?serverTimezone=UTC",
                username, password);
    }
    public void desconnect() throws SQLException {
        conn.close();
    }

    // inicializa las especialidades obteniendolas de la DB
    public static void load_especialidades() throws SQLException, IOException {
        // limpiamos todas las especialidades antiguas para cargar las nuevas
        Especialidad.clear();

        EspecialidadDAO espe = new EspecialidadDAO();
        espe.connect();
        Connection my_connection = espe.getConn();

        PreparedStatement ps = my_connection.prepareStatement(
                "select nombre direccion from Especialidad;");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Especialidad.add(new Especialidad(rs.getString(1)));
        }

    }

}
