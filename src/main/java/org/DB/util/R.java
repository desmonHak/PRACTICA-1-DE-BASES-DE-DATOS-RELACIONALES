package org.DB.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class R {

    public static InputStream getImage(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("images/" + name);
    }

    public static InputStream getProperties(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration/" + name);
    }

    public static URL getUI(String name) {
        Thread t = Thread.currentThread();
        ClassLoader c = t.getContextClassLoader();
        /** no usar File.separator, en Windows es \ y en Linux/Mac es /.
         * ClassLoader.getResource() siempre espera / como separador, independientemente del sistema operativo
         * Esto funciona siempre que no quiera hacer un Jar, pero
         * en el .jar, los recursos usan / como separador, no \. Esto devuelve null -> la url es null al mezclar
         * separadores
         * */

        URL url = c.getResource("ui/" + name);
        if (url != null) {
            return url;
        } else {
            System.out.println("No se encontro el archivo " + name);
            return null;
        }
    }
}
