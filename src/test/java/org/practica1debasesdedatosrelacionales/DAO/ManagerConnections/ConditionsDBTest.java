package org.practica1debasesdedatosrelacionales.DAO.ManagerConnections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionsDBTest {

    @Test
    void testToString() {

        ConditionsDB condicion = new ConditionsDB(
                "dni", ConditionsDBOperators.EQUALS, "48901940F");
        System.out.println(condicion);

    }
}