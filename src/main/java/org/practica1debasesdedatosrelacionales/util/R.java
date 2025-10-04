package org.practica1debasesdedatosrelacionales.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class R {

    public static InputStream getImage(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("images" + File.separator + name);
    }

    public static InputStream getProperties(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration" + File.separator + name);
    }

    public static URL getUI(String name) {
        Thread t = Thread.currentThread();
        ClassLoader c = t.getContextClassLoader();
        URL url = c.getResource("ui" + File.separator + name);
        if (url != null) {
            return url;
        } else {
            System.out.println("No se encontro el archivo " + name);
            return null;
        }
    }
}
