package org.DB.DAO.PackageInterfaceCRUD;

import org.DB.DAO.ManagerConnections.ConditionsDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface SelectDB<ClassConnection, TypeDataSelect> {
    Object invokeSelect(
            ClassConnection connection,
            List<String> select_fields,
            String table,
            List<ConditionsDB> condiciones,
            ProcessSelectData<TypeDataSelect> process) throws SQLException, IOException, InterruptedException;
}
