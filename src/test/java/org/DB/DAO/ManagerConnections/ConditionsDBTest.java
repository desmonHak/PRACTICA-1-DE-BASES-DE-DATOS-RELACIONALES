package org.DB.DAO.ManagerConnections;

import org.junit.jupiter.api.Test;

class ConditionsDBTest {

    @Test
    void testToString() {

        ConditionsDB condicion = new ConditionsDB(
                "dni", ConditionsDBOperators.EQUALS, "48901940F");
        System.out.println(condicion);

    }
}