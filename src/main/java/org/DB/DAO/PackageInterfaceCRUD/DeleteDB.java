package org.DB.DAO.PackageInterfaceCRUD;

import org.DB.DAO.ManagerConnections.ConditionsDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface DeleteDB<ClassConnection> {
    void invokeRemove(ClassConnection connection, String table, List<ConditionsDB> condiciones) throws SQLException, IOException, InterruptedException;
}
