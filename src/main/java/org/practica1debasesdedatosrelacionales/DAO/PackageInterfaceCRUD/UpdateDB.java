package org.practica1debasesdedatosrelacionales.DAO.PackageInterfaceCRUD;

import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConditionsDB;
import org.practica1debasesdedatosrelacionales.DAO.ManagerConnections.ConnnectionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@FunctionalInterface
public interface UpdateDB<ClassConnection> {
    void invokeUpdate(
            ClassConnection connection,
            String table, HashMap<String, Object> data,
            List<ConditionsDB> condiciones) throws SQLException;
}
