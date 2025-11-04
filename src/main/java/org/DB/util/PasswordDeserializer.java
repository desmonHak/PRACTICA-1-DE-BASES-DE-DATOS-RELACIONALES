package org.DB.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class PasswordDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String passwordValue = p.getValueAsString();
        // Aquí puedes acceder al objeto contenedor Paciente para asignar el hash:
        // Pero como no tienes el objeto directamente,
        // la forma más simple es devolver el password y luego en el setter asociar hash.

        return passwordValue; // Devuelve la cadena que será asignada al campo password
    }
}