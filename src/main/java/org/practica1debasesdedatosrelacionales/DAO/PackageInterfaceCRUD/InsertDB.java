package org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD;

import java.sql.SQLException;
import java.util.HashMap;

@FunctionalInterface
public interface InsertDB<ClassConnection> {
    void invokeInsert(ClassConnection connection, String Table, HashMap<String, Object> data) throws SQLException;
}
