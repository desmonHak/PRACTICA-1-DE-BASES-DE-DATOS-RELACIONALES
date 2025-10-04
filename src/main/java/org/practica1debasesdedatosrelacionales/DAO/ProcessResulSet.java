package org.practica1debasesdedatosrelacionales.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ProcessResulSet {

    Object invokeProcessResultSet(ResultSet data) throws SQLException;

}
