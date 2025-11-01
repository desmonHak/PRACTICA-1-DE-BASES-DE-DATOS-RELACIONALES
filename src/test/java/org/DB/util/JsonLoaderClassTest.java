package org.DB.util;

import org.DB.domain.Cita;
import org.DB.domain.Especialidad;
import org.DB.domain.Paciente;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JsonLoaderClassTest {

    @Test
    void load() throws IOException {

        JsonLoaderClass loadder = new JsonLoaderClass(
                "src/main/resources/DB/centro_medico.Paciente.json", Paciente.class);

        loadder.load().forEach(System.out::println);

        loadder = new JsonLoaderClass(
                "src/main/resources/DB/centro_medico.Citas.json", Cita.class);

        loadder.load().forEach(System.out::println);

        loadder = new JsonLoaderClass(
                "src/main/resources/DB/centro_medico.Especialidad.json", Especialidad.class);

        loadder.load().forEach(System.out::println);

    }
}