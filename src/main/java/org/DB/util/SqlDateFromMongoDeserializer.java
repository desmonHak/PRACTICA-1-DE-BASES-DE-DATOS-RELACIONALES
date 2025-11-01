package org.DB.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Date;

public class SqlDateFromMongoDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode dateNode = node.get("$date");
        if (dateNode == null || !dateNode.isTextual()) {
            throw new IOException("Formato de fecha inválido en JSON");
        }
        String dateValue = dateNode.textValue(); // textValue() = asText()
        String dateOnly = dateValue.split("T")[0];
        return Date.valueOf(dateOnly);
    }
}