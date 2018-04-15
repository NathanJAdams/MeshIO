package com.ripplargames.meshio.util;

public class EnDecoder {
    private static final long BYTE_RANGE = 2 * (long) Byte.MAX_VALUE;
    private static final long SHORT_RANGE = 2 * (long) Short.MAX_VALUE;
    private static final long INT_RANGE = 2 * (long) Integer.MAX_VALUE;

    private final double min;
    private final double max;
    private final double range;
    private final double offset;

    public EnDecoder(double min, double max) {
        this.min = min;
        this.max = max;
        this.range = (max - min);
        this.offset = min;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public byte encodeAsByte(double decoded) {
        return (byte) encode(decoded, BYTE_RANGE, Byte.MAX_VALUE);
    }

    public short encodeAsShort(double decoded) {
        return (short) encode(decoded, SHORT_RANGE, Short.MAX_VALUE);
    }

    public int encodeAsInt(double decoded) {
        return (int) encode(decoded, INT_RANGE, Integer.MAX_VALUE);
    }

    private long encode(double datum, long multiply, long subtract) {
        if (range == 0) {
            return 0;
        }
        double zeroed = datum - offset;
        double normalised = zeroed / range;
        double expanded = normalised * multiply;
        double encoded = expanded - subtract;
        return Math.round(encoded);
    }

    public double decodeByte(byte encoded) {
        return decode(encoded, BYTE_RANGE, Byte.MAX_VALUE);
    }

    public double decodeShort(short encoded) {
        return decode(encoded, SHORT_RANGE, Short.MAX_VALUE);
    }

    public double decodeInt(int encoded) {
        return decode(encoded, INT_RANGE, Integer.MAX_VALUE);
    }

    private double decode(int encoded, long divisor, long add) {
        double expanded = encoded + add;
        double normalised = expanded / divisor;
        double zeroed = normalised * range;
        double decoded = zeroed + offset;
        return decoded;
    }
}
