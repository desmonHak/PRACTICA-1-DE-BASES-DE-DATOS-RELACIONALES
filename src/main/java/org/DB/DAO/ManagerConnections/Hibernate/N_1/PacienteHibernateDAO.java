package org.DB.DAO.ManagerConnections.Hibernate.N_1;

import org.DB.domain.Cita;
import org.DB.domain.DNI;
import org.DB.domain.Paciente;
import org.DB.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PacienteHibernateDAO implements HibernateDAOMethods<Paciente, DNI> {

    @Override
    public void save(Paciente object) {
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
    public Paciente getById(DNI dni) {
        Session session = null;
        Transaction transaction = null;

        Paciente paciente = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            paciente = session.get(Paciente.class, dni);
            paciente.getDni().rebuildAfterLoad();

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

        return paciente;
    }

    @Override
    public List<Paciente> getAll() {
        Session session = null;
        Transaction transaction = null;

        List<Paciente> pacientes = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            pacientes = session.createQuery("from Paciente").list();
            pacientes.forEach(paciente -> {
                paciente.getDni().rebuildAfterLoad();
            });

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

        return pacientes;
    }

    @Override
    public void update(Paciente object) {
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
    public void deleteById(DNI dni) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // obtenemos la cita para luego eliminarla
            Paciente paciente = session.get(Paciente.class, dni);
            session.delete(paciente);

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
