package org.DB.util;

import org.DB.domain.Cita;
import org.DB.domain.Especialidad;
import org.DB.domain.Paciente;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil implements Closeable {

    static SessionFactory factory = null;

    static {

        try {
            Configuration cfg = new Configuration();

            cfg.configure("hibernate.cfg.xml");
            cfg.addAnnotatedClass(Cita.class);
            cfg.addAnnotatedClass(Especialidad.class);
            cfg.addAnnotatedClass(Paciente.class);
            factory = cfg.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error al crear SessionFactory: " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }

    public static Session getSession() {
        return factory.openSession();
    }

    public static void shutdown() {
        factory.close();
    }

    @Override
    public void close() {
        shutdown();
    }
}
