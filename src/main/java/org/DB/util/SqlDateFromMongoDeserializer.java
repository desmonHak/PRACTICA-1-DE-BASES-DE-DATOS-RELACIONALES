package org.DB.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;

public class SqlDateFromMongoDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String dateValue = jp.getCodec().readTree(jp).get("$date").asText();
        // Extraer solo la parte de fecha (YYYY-MM-DD)
        String dateOnly = dateValue.split("T")[0];
        return Date.valueOf(dateOnly);
    }
}
