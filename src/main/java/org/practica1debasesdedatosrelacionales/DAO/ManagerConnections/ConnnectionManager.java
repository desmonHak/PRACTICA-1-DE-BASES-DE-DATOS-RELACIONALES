package org.practica1debasesdedatosrelacionales.DAO;

import com.mongodb.MongoClient;

import java.io.IOException;
import java.sql.SQLException;

public interface ConnnectionManager<T> {

    T getNewConection() throws SQLException, IOException;
    void closeConection() throws SQLException;
    T connect();
}
