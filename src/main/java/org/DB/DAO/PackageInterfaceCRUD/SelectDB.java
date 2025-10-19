package org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD;

import com.mongodb.MongoClient;
import org.bson.Document;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConditionsDB;

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
