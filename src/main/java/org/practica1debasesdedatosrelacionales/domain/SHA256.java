package org.practica1debasesdedatosrelacionales.domain;

import javafx.scene.paint.Color;
import org.apache.commons.codec.digest.DigestUtils;
import org.practica1debasesdedatosrelacionales.Exceptions.SeedErrorRange;
import org.practica1debasesdedatosrelacionales.util.UInt32_t;

public class SHA256 {

    private String hash;
    private String data_org;

    /**
     * Indica si esta clase almaceno previamente un valor hash, o si almaceno un dato que luego
     * se hasheo
     */
    private boolean is_hash;

    /**
     * Permite comprobar si la contraseña pasada es la almacenada en SHA2.256
     * @param password password sin hashing a verificar
     * @return true si son iguales, false si no
     */
    public boolean check_password(String password) {
        return DigestUtils.sha256Hex(password).equals(this.hash);
    }

    public SHA256(String data_org, boolean is_hash) {
        if (is_hash) {
            this.hash = data_org;
            this.data_org = null;
        } else {
            this.data_org = data_org;
            this.hash = calc_hash();
        }
    }

    String calc_hash() {
        this.hash = DigestUtils.sha256Hex(this.data_org);
        return this.hash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getData_org() {
        return data_org;
    }

    public void setData_org(String data_org) {
        this.data_org = data_org;
    }

    public boolean isIs_hash() {
        return is_hash;
    }

    public void setIs_hash(boolean is_hash) {
        this.is_hash = is_hash;
    }

    @Override
    public String toString() {
        return "SHA256{" +
                "hash='" + hash + '\'' +
                ", data_org='" + data_org + '\'' +
                ", is_hash=" + is_hash +
                '}';
    }

    /**
     * Este algoritmo lo use en otras de mis librerias de colores para generar trios
     * de valores usando un unico valor base, junto a 6 valores pseudoaleatorios.
     * Codigo fuente original donde lo uso: https://github.com/desmonHak/colors-C-C-plus-plus/blob/main/src/colors.c
     *
     * @param value valor base que usar para generar el trio de valores dependientes del conjunto de datos
     * @param n1 n2 n3 n4 n5 n6, valores constantes o variantes de los datos que usar para generar los 3 valores unicos
     * @return se devuelve un valor de 1 byte pseudo aletorio que se puede usar como valor base de nuevo en esta
     * funcion
     */
    public static UInt32_t jenkins_hash(
            UInt32_t value,
            UInt32_t n1, UInt32_t n2, UInt32_t n3,
            UInt32_t n4, UInt32_t n5, UInt32_t n6
    ) {
        long v = value.longValue(); // trabajar en long para evitar overflow

        v = (v + 0x7ed55d16L) + ((v << n1.intValue()) & 0xFFFFFFFFL);
        v = (v ^ 0xc761c23cL) ^ ((v >>> n2.intValue()) & 0xFFFFFFFFL);
        v = (v + 0x165667b1L) + ((v << n3.intValue()) & 0xFFFFFFFFL);
        v = (v + 0xd3a2646cL) ^ ((v << n4.intValue()) & 0xFFFFFFFFL);
        v = (v + 0xfd7046c5L) + ((v << n5.intValue()) & 0xFFFFFFFFL);
        v = (v ^ 0xb55a4f09L) ^ ((v >>> n6.intValue()) & 0xFFFFFFFFL);

        v = v & 0xFFFFFFFFL;  // asegurar 32 bits sin signo
        return new UInt32_t(v % 256);       // resultado final
    }

    public static Color generate_color_RGB(
            UInt32_t seed,
            UInt32_t n1, UInt32_t n2, UInt32_t n3,
            UInt32_t n4, UInt32_t n5, UInt32_t n6,
            float opacity)
    {
        /* si mayor que 255 entonces error */
        if (seed.gt(UInt32_t.valueOf(255)))  { // seed > 255
            throw new SeedErrorRange("El numero debe estar en el rango de 0 a 255.\n");
        }


        UInt32_t value1;
        UInt32_t value2;
        UInt32_t value3;
        /* Aplicar la función de dispersión a los valores iniciales */
        value1 = jenkins_hash(   seed, n1, n2, n3, n4, n5, n6 );
        value2 = jenkins_hash( value1, n1, n2, n3, n4, n5, n6 );
        value3 = jenkins_hash( value2, n1, n2, n3, n4, n5, n6 );

        // Normalizar a rango 0.0–1.0
        double r = value1.longValue() / 255.0;
        double g = value2.longValue() / 255.0;
        double b = value3.longValue() / 255.0;

        return new Color(r, g, b, opacity);
    }

}
