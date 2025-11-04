package org.DB.DAO.ManagerConnections.Hibernate.N_1;

import javax.persistence.PersistenceException;
import java.util.List;

public interface HibernateDAOMethods<_Class, ID> {

    void save(_Class object) throws PersistenceException;
    _Class getById(ID id);
    List<_Class> getAll();
    void update(_Class object);
    void deleteById(ID id);

}
