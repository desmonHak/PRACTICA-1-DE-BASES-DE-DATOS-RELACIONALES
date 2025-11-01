package org.DB.domain;

import org.DB.Exceptions.DniException;

import javax.persistence.Embeddable;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * La clase DNI debe ser serializable para poder trabajar con
 * ella usando Hibernate
 */
@Embeddable  // Esto le dice a Hibernate
             // que puede incrustar este tipo en otra entidad
public class DNI implements Serializable {

    private static final long serialVersionUID = 1L;

    @Transient // <--- Hibernate lo ignorara
    private Integer number;
    @Transient // <--- Hibernate lo ignorara
    private char letter;

    /**
     * Este es el unico campo que se almacenara en la DB, de esta
     * clase enbebida, por lo que debremos obtener los elementos
     * transitivos (Transient) por nuestra cuenta del elemento
     * obtenido de la DB
     */
    private String dni = null;

    private static final String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

    /**
     * Es necesario un constructor vacio para trabajar con Hibernate
     */
    public DNI() {
    }

    /**
     * reconstruir los campos transient al cargar desde la BD,
     * al cargar el DNI de la DB, se obtiene un String dni,
     * pero hace falta desconstruir este dato en letter y number
     * ya que todas las demas clases usan toString para obtener
     * la representacion de este DNI, y el metodo toString
     * usa letter y number para construir el DNI en lugar de
     * retornar el campo dni directamente, esto es asi por
     * que antiguamente, este campo no existia, ademas
     * de que permite separar la letra del numero.
     *
     * No funciona con SessionFactory !
     */
    @PostLoad
    public void rebuildAfterLoad() {
        if (dni != null) {
            DNI dni =  new DNI(this.dni);;
            this.letter = dni.letter;
            this.number = dni.number;
        }
    }

    /**
     * Calculamos la letra del DNI y validamos si es la que debe ser, en caso de no serlo,
     * indicamos cual deberia ser
     * @param dni a comprobar
     */
    public DNI(String dni) {
        if (dni.length() != 9) {
            throw new DniException("El DNI tiene no 9 caracteres, debe tener 8 numeros y una letra");
        }


        StringBuilder numbers   = new StringBuilder();
        char supuesta_letra     = 0;
        for (char c : dni.toCharArray()) {
            if (Character.isDigit(c) && numbers.length() <= 8) {
                numbers.append(c);
            } else {
                // convertimos la letra en mayuscula si es minuscula
                supuesta_letra = (Character.isLowerCase(c)) ? (char) (c - 32) : c;
                break;
            }

        }

        // calculamos la letra en base al valor introducido
        this.number = Integer.parseInt(numbers.toString());
        this.letter = letras.charAt(
                this.number % letras.length());

        if (letter != supuesta_letra) {
            throw new DniException(String.format("El DNI %s no es valido, la letra ingresada es %c, pero deberia ser %c", this.number, supuesta_letra, this.letter));
        }
        this.dni = toString();
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Genera un DNI aleatorio valido (8 digitos + letra)
     * @return un objeto DNI valido generado aleatoriamente
     */
    public static DNI generarDniAleatorio () {
        StringBuilder numero_str = new StringBuilder();
        // generar 8 numeros del 0 al 9 para el DNI
        IntStream.range(0, 8).forEach(value -> {
            numero_str.append(new Random().nextInt(0, 9));
        });
        int numero = Integer.parseInt(String.valueOf(numero_str));
        char letra = letras.charAt(numero % letras.length());
        String dniGenerado = String.format("%08d%c", numero, letra);
        return new DNI(dniGenerado);
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    String getDNI() {
        return String.format("%08d%c", this.number, this.letter);
    }

    @Override
    public String toString() {
        return getDNI();
    }

    /**
     * Los metodos equals y hashCode debo implementarlos para que
     * pueda trabajar bien con hibernate
     * @param o elemento con el que realizar la comparacion
     * @return indica si el objeto es o no igual a otro
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DNI dni = (DNI) o;
        return letter == dni.letter && Objects.equals(number, dni.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, letter);
    }
}
