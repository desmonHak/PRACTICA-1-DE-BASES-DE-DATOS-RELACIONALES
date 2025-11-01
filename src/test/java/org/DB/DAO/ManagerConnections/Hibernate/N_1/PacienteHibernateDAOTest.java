package org.DB.DAO.ManagerConnections.Hibernate.N_1;

import org.DB.domain.DNI;
import org.DB.domain.Paciente;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PacienteHibernateDAOTest {

    private static PacienteHibernateDAO dao;
    private static DNI dniTest;
    private static Paciente pacienteTest;

    @BeforeAll
    static void setUp() {
        dao = new PacienteHibernateDAO();
        dniTest = DNI.generarDniAleatorio();
        pacienteTest = new Paciente(
                dniTest,
                "Carlos Gómez",
                "carlos@example.com",
                "password123",
                "612345678",
                "Calle Falsa 123"
        );
        dao.save(pacienteTest);
        System.out.println(pacienteTest);
    }

    @Test
    @Order(1)
    @DisplayName("Guardar paciente correctamente")
    void save() {
        DNI newDni = DNI.generarDniAleatorio();
        Paciente newPaciente = new Paciente(newDni, "Ana Ruiz", "ana@example.com", "pass", "600123456", "Calle Real 5");
        System.out.println(newPaciente);
        assertDoesNotThrow(() -> dao.save(newPaciente));
    }

    @Test
    @Order(2)
    @DisplayName("Obtener paciente por DNI existente")
    void getById() {
        Paciente paciente = dao.getById(dniTest);
        System.out.println(paciente);
        assertNotNull(paciente, "Paciente no debe ser nulo");
        assertEquals(pacienteTest.getNombre(), paciente.getNombre());
    }

    @Test
    @Order(3)
    @DisplayName("Obtener todos los pacientes")
    void getAll() {
        List<Paciente> pacientes = dao.getAll();
        pacientes.forEach(paciente ->{
                DNI dni = paciente.getDni();
                System.out.println(paciente + " -> " + dni.toString());
            System.out.println(dni.getDni());
            System.out.println(dni.getLetter());
            System.out.println(dni.getNumber());
        });
        System.out.println();
        assertNotNull(pacientes);
        assertFalse(pacientes.isEmpty());
        assertTrue(pacientes.stream().anyMatch(p -> p.getDni().equals(dniTest)));
    }

    @Test
    @Order(4)
    @DisplayName("Actualizar paciente existente")
    void update() {
        Paciente paciente = dao.getById(dniTest);
        System.out.println(paciente);
        assertNotNull(paciente);
        paciente.setDireccion("Nueva Dirección 456");
        assertDoesNotThrow(() -> dao.update(paciente));

        Paciente updated = dao.getById(dniTest);
        System.out.println(updated);
        assertEquals("Nueva Dirección 456", updated.getDireccion());
    }

    @Test
    @Order(5)
    @DisplayName("Eliminar paciente por DNI")
    void deleteById() {
        DNI tempDni = DNI.generarDniAleatorio();
        Paciente p = new Paciente(tempDni, "Temporal", "temp@example.com", "tempPass", "600000000", "Temp Street");
        dao.save(p);
        System.out.println(p);

        assertDoesNotThrow(() -> dao.deleteById(tempDni));
        Paciente deleted = dao.getById(tempDni);
        System.out.println(deleted);
        assertNull(deleted, "Paciente debería haberse eliminado");
    }
}
