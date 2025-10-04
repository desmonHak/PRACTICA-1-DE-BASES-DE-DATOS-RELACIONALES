package org.practica1debasesdedatosrelacionales.util;

/**
 * Clase que representa un valor de 32bits sin signo, al estilo C
 */
public class UInt32_t extends Number implements Comparable<UInt32_t>{
    private final int value;

    public UInt32_t(long value) {
        if (value < 0 || value > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("Valor fuera de rango de uint32");
        }
        this.value = (int) value;
    }

    // Suma con otro UInt32_t
    public UInt32_t add(UInt32_t other) {
        return new UInt32_t(Integer.toUnsignedLong(this.value) +
                Integer.toUnsignedLong(other.value));
    }

    // Suma con un número long
    public UInt32_t add(long other) {
        long result = this.longValue() + other;
        return new UInt32_t(result);
    }

    // Suma con un número int
    public UInt32_t add(int other) {
        long result = this.longValue() + Integer.toUnsignedLong(other);
        return new UInt32_t(result);
    }

    // Operaciones bit a bit
    public UInt32_t xor(UInt32_t other) {
        return new UInt32_t(Integer.toUnsignedLong(this.value ^ other.value));
    }

    public UInt32_t shiftLeft(int bits) {
        return new UInt32_t(Integer.toUnsignedLong(this.value << bits));
    }

    public UInt32_t shiftRight(int bits) {
        // Desplazamiento lógico
        return new UInt32_t(Integer.toUnsignedLong(this.value >>> bits));
    }

    public static UInt32_t valueOf(long number) {
        return new UInt32_t(number);
    }

    public static UInt32_t valueOf(String number) {
        return new UInt32_t(Long.parseLong(number));
    }

    @Override
    public String toString() {
        return Long.toString(longValue());
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return Integer.toUnsignedLong(value);
    }

    @Override
    public float floatValue() {
        return (float) longValue();
    }

    @Override
    public double doubleValue() {
        return (double) longValue();
    }

    @Override
    public int compareTo(UInt32_t o) {
        return Long.compare(this.longValue(), o.longValue());
    }

    public boolean gt(UInt32_t other) {
        return this.longValue() > other.longValue();
    }

    public boolean lt(UInt32_t other) {
        return this.longValue() < other.longValue();
    }

    public boolean eq(UInt32_t other) {
        return this.longValue() == other.longValue();
    }

}
