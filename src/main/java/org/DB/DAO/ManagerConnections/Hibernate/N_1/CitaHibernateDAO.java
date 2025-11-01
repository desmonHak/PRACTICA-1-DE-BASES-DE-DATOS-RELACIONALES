package org.DB.DAO.ManagerConnections.Hibernate.N_1;

import org.DB.domain.Paciente;
import org.hibernate.Session;
import org.DB.domain.Cita;
import org.DB.domain.DNI;
import org.DB.util.HibernateUtil;
import org.hibernate.Transaction;

import javax.persistence.PersistenceException;
import java.util.List;

public class CitaHibernateDAO implements HibernateDAOMethods<Cita, Integer> {

    @Override
    public void save(Cita object) throws PersistenceException {
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
    public Cita getById(Integer numeroCita) {
        Session session = null;
        Transaction transaction = null;

        Cita cita = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            cita = session.get(Cita.class, numeroCita);
            try {
                cita.getPaciente().syncAfterLoad();
                cita.syncAfterLoad();
            } catch (NullPointerException _){}

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

        return cita;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Cita> getAll() {
        Session session = null;
        Transaction transaction = null;

        List<Cita> citas = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            /**
             * Aunque la tabla se llama Citas, la entidad en mi codigo
             * se llama Cita
             */
            citas = session.createQuery("select c from Cita c").list();
            citas.forEach(cita -> {
                cita.getPaciente().syncAfterLoad();
                cita.syncAfterLoad();
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

        return citas;
    }

    @Override
    public void update(Cita object) {
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
    public void deleteById(Integer numeroCita) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // obtenemos la cita para luego eliminarla
            Cita cita = session.get(Cita.class, numeroCita);

            if (cita != null) {
                // es necesario eliminar la cita del paciente
                // tambien, antes de hacer el Delete
                Paciente paciente = cita.getPaciente();
                if (paciente != null) {
                    paciente.getCitas().remove(cita);  // quitar de la colección del paciente
                }
                session.delete(cita);
            } else {
                throw new Exception("No se encontró cita con ID " + numeroCita);
            }
            session.delete(cita);

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
