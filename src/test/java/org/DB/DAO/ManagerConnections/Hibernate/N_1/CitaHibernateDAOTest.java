package org.DB.DAO.ManagerConnections.Hibernate.N_1;

import org.DB.domain.*;
import org.DB.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración del DAO CitaHibernateDAO.
 * Usa una base de datos en memoria (H2) configurada en hibernate.cfg.xml.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CitaHibernateDAOTest {

    private static CitaHibernateDAO dao;
    private static DNI dniTest;
    private static Paciente pacienteTest;
    private static Especialidad especialidadTest;
    private static Cita citaGuardada;

    @BeforeAll
    static void setUp() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        assertNotNull(sessionFactory, "No se pudo crear SessionFactory. Verifica HibernateUtil.");

        dao = new CitaHibernateDAO();

        dniTest = DNI.generarDniAleatorio();

        // Crear entidades base y guardarlas
        pacienteTest = new Paciente(
                dniTest,
                "Juan Pérez",
                "juan@example.com",
                "password123",
                "123456789",
                "Calle Falsa 123"
        );

        especialidadTest = new Especialidad("cardiologia");

        // Guardar paciente y especialidad antes de crear la cita
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            //session.save(especialidadTest); // no es necesario guardarla por que ya existe
            session.save(pacienteTest);
            session.getTransaction().commit();
        }

        citaGuardada = new Cita(
                pacienteTest.getDni(),
                Date.valueOf(LocalDate.now()),
                especialidadTest
        );
        citaGuardada.setPaciente(pacienteTest); // esto sincroniza automáticamente el dni transitorio
    }

    @Test
    @Order(1)
    @DisplayName("Guardar una cita correctamente")
    void save() {
        assertDoesNotThrow(() -> dao.save(citaGuardada), "No se pudo guardar la cita");
        assertNotNull(citaGuardada.getNumero_cita(), "El número de cita debería haberse generado");
    }

    @Test
    @Order(2)
    @DisplayName("Obtener una cita por ID existente")
    void getById() {
        Cita cita = dao.getById(citaGuardada.getNumero_cita());
        System.out.println(cita.getPaciente());
        System.out.println(cita);
        assertNotNull(cita, "La cita obtenida no debe ser nula");
        assertEquals(pacienteTest.getDni(), cita.getPaciente().getDni(), "El paciente asociado no coincide");
    }

    @Test
    @Order(3)
    @DisplayName("Obtener todas las citas")
    void getAll() {
        List<Cita> citas = dao.getAll();
        citas.forEach(cita -> {
            if (cita.getPaciente() != null) {
                cita.getPaciente().syncCitasDni();
            }
            System.out.println(cita);

        });
        assertNotNull(citas, "La lista de citas no debe ser nula");
        assertFalse(citas.isEmpty(), "La lista de citas no debe estar vacía");
    }

    @Test
    @Order(4)
    @DisplayName("Actualizar una cita existente")
    void update() {
        Cita cita = dao.getById(citaGuardada.getNumero_cita());
        assertNotNull(cita, "La cita no existe para actualizar");

        cita.setEspecialidad(new Especialidad("dermatologia"));

        assertDoesNotThrow(() -> dao.update(cita), "No se pudo actualizar la cita");

        Cita citaActualizada = dao.getById(cita.getNumero_cita());
        System.out.println(citaGuardada);
        System.out.println(citaActualizada);
        assertEquals("dermatologia", citaActualizada.getEspecialidad().getNombre(), "La especialidad no se actualizó correctamente");
    }

    @Test
    @Order(5)
    @DisplayName("Eliminar una cita por ID")
    void deleteById() {
        assertDoesNotThrow(() -> dao.deleteById(citaGuardada.getNumero_cita()), "Error al eliminar la cita");

        System.out.println("Cita que se esta  eliminando: " + citaGuardada.getNumero_cita());
        Cita cita = dao.getById(citaGuardada.getNumero_cita());
        System.out.println("cita despues de la eliminacion: " + cita);
        assertNull(cita, "La cita debería haberse eliminado de la base de datos");
    }

    // al realizar los tests desde maven da error ya que la conexion es compartida
    // por todos los tests, pos lo que si se cierra, da error en el resto
    //@AfterAll
    //static void tearDown() {
    //    HibernateUtil.shutdown();
    //}
}
