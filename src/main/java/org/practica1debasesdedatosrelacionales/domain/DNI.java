package org.practica1debasesdedatosrelacionales.domain;

import org.practica1debasesdedatosrelacionales.Exceptions.DniException;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class DNI {

    private Integer number;
    private char letter;

    private static final String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

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
}
