package org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ProcessResulSet<T> {
    Object invokeProcessResultSet(T data) throws SQLException;
}
