package org.practica1debasesdedatosrelacionales.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DNITest {

    DNI dni_pruebas;

    @BeforeEach
    void setUp() {
        dni_pruebas = new DNI("74504461D");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getDNI() {
        System.out.println(dni_pruebas.getNumber());
        System.out.println(dni_pruebas.getLetter());
        System.out.println(dni_pruebas.getDNI());
    }
}