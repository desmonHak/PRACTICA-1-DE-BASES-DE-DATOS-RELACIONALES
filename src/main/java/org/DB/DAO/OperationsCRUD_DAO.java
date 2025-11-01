package org.DB.DAO;

import org.DB.domain.Cita;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface OperationsCRUD {

    public void insert(Object object) throws
            SQLException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, IOException;

    public Object select(Object object) throws
            SQLException, IOException, IllegalAccessException;


    public void delete(Object object) throws SQLException, IllegalAccessException, IOException;

    public void update(Object object) throws IllegalAccessException, SQLException, IOException;

}
