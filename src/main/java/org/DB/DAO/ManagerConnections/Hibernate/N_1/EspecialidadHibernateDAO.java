package org.DB.DAO.ManagerConnections.Hibernate.N_1;

import org.DB.domain.Especialidad;
import org.DB.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.PersistenceException;
import java.util.List;

public class EspecialidadHibernateDAO implements HibernateDAOMethods<Especialidad, String> {

    @Override
    public void save(Especialidad object) throws PersistenceException {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            session.save(object);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // cerrar la sesion
            }
        }
    }

    @Override
    public Especialidad getById(String s) {
        Session session = null;
        Transaction transaction = null;

        Especialidad especialidad = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            especialidad = session.get(Especialidad.class, s);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // cerrar la sesion
            }
        }

        return especialidad;
    }

    @Override
    public List<Especialidad> getAll() {
        Session session = null;
        Transaction transaction = null;

        List<Especialidad> especialidades = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            especialidades = session.createQuery("from Especialidad").list();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // cerrar la sesion
            }
        }

        return especialidades;
    }

    @Override
    public void update(Especialidad object) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(object);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // cerrar la sesion
            }
        }
    }

    @Override
    public void deleteById(String s) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            Especialidad especialidad = session.get(Especialidad.class, s);
            session.delete(especialidad);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // cerrar la sesion
            }
        }
    }
}
