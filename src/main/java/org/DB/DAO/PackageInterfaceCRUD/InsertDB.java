package org.DB.DAO.PackageInterfaceCRUD;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

@FunctionalInterface
public interface InsertDB<ClassConnection> {
    void invokeInsert(ClassConnection connection, String Table, HashMap<String, Object> data) throws SQLException, IOException, InterruptedException;
}
