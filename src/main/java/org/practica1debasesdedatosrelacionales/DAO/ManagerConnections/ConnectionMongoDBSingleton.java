package org.practica1debasesdedatosrelacionales.DAO;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import org.practica1debasesdedatosrelacionales.util.R;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Usaremos el patron Singleton para tener una unica instancia de
 * conexion
 */
public class ConnectionMongoDBSingleton implements ConnnectionManager<MongoClient> {

    /**
     * variable estatica que almacenara la unica existencia que habra
     */
    private static MongoClient instance = null;


    public static MongoClient getInstance(String value) {
        if (instance == null) {
            instance = new ConnectionMongoDBSingleton().getNewConection();
        }
        return instance;
    }


    @Override
    public MongoClient getNewConection() {
        try {
            Properties properties = new Properties();
            properties.load(R.getProperties("database.properties"));
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String host = properties.getProperty("host");
            int port = Integer.parseInt(properties.getProperty("port"));

            return new MongoClient(
                    new MongoClientURI("mongodb://%s:%s@%s:%d/?authSource=admin".formatted(
                            username, password, host, port
                    ))
            );
        } catch (Exception e) {
            System.out.println("Conexion Fallida");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void closeConection() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    @Override
    public MongoClient connect() {
        if (instance == null) {
            throw new MongoException("No se inicializo la clase o se cerro la conexion, debe obtener una instancia con getInstance()");
        }
        return instance;
    }
}
