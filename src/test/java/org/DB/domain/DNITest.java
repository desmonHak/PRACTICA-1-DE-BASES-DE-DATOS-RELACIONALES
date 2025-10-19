package org.DB.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

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

    @Test
    void generarDniAleatorio() {
        // generar 25 DNI's aleatorios
        IntStream.range(0, 25).forEach(value -> {
            System.out.printf("%02d - DNI: %s%n", value, DNI.generarDniAleatorio());
        });
    }
}