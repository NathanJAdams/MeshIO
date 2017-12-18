package com.ripplargames.mesh_io;

public class DatumEnDecoder {
    private static final long BYTE_RANGE = 2 * (long) Byte.MAX_VALUE;
    private static final long SHORT_RANGE = 2 * (long) Short.MAX_VALUE;
    private static final long INT_RANGE = 2 * (long) Integer.MAX_VALUE;

    public static byte encodeAsByte(double decoded, boolean isSigned) {
        return isSigned
                ? (byte) (decoded * Byte.MAX_VALUE)
                : (byte) (Math.round((decoded * BYTE_RANGE) - Byte.MAX_VALUE));
    }

    public static short encodeAsShort(double decoded, boolean isSigned) {
        return isSigned
                ? (short) (decoded * Short.MAX_VALUE)
                : (short) (Math.round((decoded * SHORT_RANGE) - Short.MAX_VALUE));
    }

    public static int encodeAsInt(double decoded, boolean isSigned) {
        return isSigned
                ? (int) (decoded * Integer.MAX_VALUE)
                : (int) (Math.round((decoded * INT_RANGE) - Integer.MAX_VALUE));
    }

    public static double decodeByte(byte encoded, boolean isSigned) {
        return isSigned
                ? ((double) encoded / Byte.MAX_VALUE)
                : (((double) encoded + Byte.MAX_VALUE) / BYTE_RANGE);
    }

    public static double decodeShort(short encoded, boolean isSigned) {
        return isSigned
                ? ((double) encoded / Short.MAX_VALUE)
                : (((double) encoded + Short.MAX_VALUE) / SHORT_RANGE);
    }

    public static double decodeInt(int encoded, boolean isSigned) {
        return isSigned
                ? ((double) encoded / Integer.MAX_VALUE)
                : (((double) encoded + Integer.MAX_VALUE) / INT_RANGE);
    }
}
