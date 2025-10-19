package org.DB.Exceptions.ExceptionsDB;

public class SingletonException extends RuntimeException {
    public SingletonException(Class<?> class_exception, String message) {
        super("Error en la clase %s: ".formatted(class_exception.getName()) + message);
    }
}
