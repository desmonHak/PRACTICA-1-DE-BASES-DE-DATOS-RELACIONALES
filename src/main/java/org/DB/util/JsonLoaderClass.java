package org.DB.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonLoaderClass {

    private String name_and_path;
    private Class<?> class_for_load;

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public String getName() {
        return name_and_path;
    }

    public void setName(String name) {
        this.name_and_path = name;
    }

    public JsonLoaderClass(String name, Class<?> class_for_load) {
        this.name_and_path = name;
        this.class_for_load = class_for_load;
    }

    List<?> load() throws IOException {
        File file_json = new File(name_and_path);
        JsonNode rootNode = JSON_MAPPER.readTree(file_json);

        return JSON_MAPPER.readerForListOf(class_for_load).readValue(rootNode);
    }

    // Guarda la lista en JSON
    public void save(List<?> list) throws IOException {
        File file_json = new File(name_and_path);
        JSON_MAPPER.writeValue(file_json, list);
    }
}
