package org.practica1debasesdedatosrelacionales.DAO;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionMySQLDBSingleton implements ConnnectionManager<Connection> {
    private Connection instance = null;

    @Override
    public Connection getNewConection() throws SQLException, IOException {
        Properties configuration = new Properties();

        configuration.load(R.getProperties("database.properties"));
        String host = configuration.getProperty("host");
        String port = configuration.getProperty("port");
        String name = configuration.getProperty("name");
        String username = configuration.getProperty("username");
        String password = configuration.getProperty("password");

        return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?serverTimezone=UTC",
                username, password);
    }

    @Override
    public void closeConection() throws SQLException {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    @Override
    public Connection connect() {
        if (instance == null) {
            throw new MongoException("No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        return instance;
    }

}
