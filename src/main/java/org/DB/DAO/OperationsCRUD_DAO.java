package org.DB.DAO;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface OperationsCRUD_DAO {

    void insert(Object object) throws
            SQLException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, IOException;

    Object select(Object object) throws
            SQLException, IOException, IllegalAccessException;


    void delete(Object object) throws
            SQLException, IllegalAccessException, IOException;

    void update(Object object) throws
            IllegalAccessException, SQLException, IOException;

}
