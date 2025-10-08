package org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD;

import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConditionsDB;

import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface DeleteDB<ClassConnection> {
    void invokeRemove(ClassConnection connection, String table, List<ConditionsDB> condiciones) throws SQLException;
}
