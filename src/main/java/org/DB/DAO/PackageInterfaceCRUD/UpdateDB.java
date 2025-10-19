package org.DB.DAO.PackageInterfaceCRUD;

import org.DB.DAO.ManagerConnections.ConditionsDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@FunctionalInterface
public interface UpdateDB<ClassConnection> {
    void invokeUpdate(
            ClassConnection connection,
            String table, HashMap<String, Object> data,
            List<ConditionsDB> condiciones) throws SQLException, IOException, InterruptedException;
}
