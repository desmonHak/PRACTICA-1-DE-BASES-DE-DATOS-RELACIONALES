package org.DB.DAO.ManagerConnections.Hibernate.N_1;

import org.DB.domain.Especialidad;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.*;

import javax.persistence.PersistenceException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EspecialidadHibernateDAOTest {
    private static EspecialidadHibernateDAO dao;
    private static Especialidad especialidadTest;

    @BeforeAll
    static void setUp() {
        dao = new EspecialidadHibernateDAO();
        especialidadTest = new Especialidad("TestEspecialidad");
    }

    @Test
    @Order(1)
    @DisplayName("Guardar una especialidad correctamente")
    void save() {
        System.out.println(especialidadTest);
        try {
            dao.save(especialidadTest);
        } catch (PersistenceException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                System.out.println("El dato ya existe: " + cve.getConstraintName());
            } else {
                System.out.println("AAAAAAAAAAAAA");
                // Otros errores
                throw e;
            }
        }
    }

    @Test
    @Order(2)
    @DisplayName("Obtener una especialidad por ID existente")
    void getById() {
        Especialidad especialidad = dao.getById(especialidadTest.getNombre());
        System.out.println(especialidad);
        assertNotNull(especialidad, "La especialidad obtenida no debe ser nula");
        assertEquals(especialidadTest.getNombre(), especialidad.getNombre(), "Los nombres deben coincidir");
    }

    @Test
    @Order(3)
    @DisplayName("Obtener todas las especialidades")
    void getAll() {
        List<Especialidad> especialidades = dao.getAll();
        especialidades.forEach(especialidad ->
                System.out.println(especialidad));
        assertNotNull(especialidades, "La lista de especialidades no debe ser nula");
        assertFalse(especialidades.isEmpty(), "La lista de especialidades no debe estar vacía");
        assertTrue(especialidades.stream().anyMatch(e -> e.getNombre().equals(especialidadTest.getNombre())),
                "La especialidad de prueba debe estar en la lista");
    }

    @Test
    @Order(4)
    @DisplayName("Actualizar una especialidad existente")
    void update() {
        Especialidad especialidad = dao.getById(especialidadTest.getNombre());
        assertNotNull(especialidad, "No existe la especialidad para actualizar");
        especialidad.setNombre("EspecialidadActualizada");
        assertDoesNotThrow(() -> dao.update(especialidad), "No se pudo actualizar la especialidad");
        Especialidad actualizada = dao.getById("EspecialidadActualizada");
        assertNotNull(actualizada, "La especialidad actualizada no se encontró");
        assertEquals("EspecialidadActualizada", actualizada.getNombre(), "El nombre no se actualizó correctamente");
        // Restaurar estado original para no afectar otras pruebas
        actualizada.setNombre(especialidadTest.getNombre());
        dao.update(actualizada);
    }

    @Test
    @Order(5)
    @DisplayName("Eliminar una especialidad por ID")
    void deleteById() {
        Especialidad nuevaEspecialidad = new Especialidad("EspecialidadEliminar");
        dao.save(nuevaEspecialidad);
        assertDoesNotThrow(() -> dao.deleteById("EspecialidadEliminar"), "Error al eliminar la especialidad");
        Especialidad eliminada = dao.getById("EspecialidadEliminar");
        assertNull(eliminada, "La especialidad debería haberse eliminado de la base de datos");
    }
}