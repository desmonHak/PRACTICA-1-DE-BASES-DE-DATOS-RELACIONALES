package org.practica1debasesdedatosrelacionales.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacienteTest {

    @Test
    void check_password() {
        DNI dni = new DNI("67984567V");
        Paciente paciente = new Paciente(
                dni,
                "Pedro",
                "hola@gmail.com",
                "1234",
                "456234123",
                "C/Fruteria"
        );

        // comprueba si la contraseña del Paciente fue 1234
        System.out.println(paciente.getHashClass().check_password("1234"));

    }
}