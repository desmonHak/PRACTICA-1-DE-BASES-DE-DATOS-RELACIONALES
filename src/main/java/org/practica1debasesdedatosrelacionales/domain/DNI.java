package org.practica1debasesdedatosrelacionales.domain;

import org.practica1debasesdedatosrelacionales.Exceptions.DniException;

public class DNI {

    private Integer number;
    private char letter;

    /**
     * Calculamos la letra del DNI y validamos si es la que debe ser, en caso de no serlo,
     * indicamos cual deberia ser
     * @param dni a comprobar
     */
    public DNI(String dni) {

        if (dni.length() != 9) {
            throw new DniException("El DNI tiene no 9 caracteres, debe tener 8 numeros y una letra");
        }

        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

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
            throw new DniException("El DNI no es valido, la letra ingresada es %c, pero deberia ser %c".formatted(
                    supuesta_letra, this.letter
            ));
        }
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
        return "%d%c".formatted(this.number, this.letter);
    }

    @Override
    public String toString() {
        return getDNI();
    }
}
