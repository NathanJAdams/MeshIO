package com.ripplar_games.mesh_io;

public class DatumEnDecoder {
    private static final long BYTE_RANGE = 0xFE;
    private static final long SHORT_RANGE = 0xFFFE;
    private static final long INT_RANGE = 0xFFFFFFFE;

    public static byte encodeAsByte(float decoded, boolean isSigned) {
        return isSigned
                ? (byte) (decoded * Byte.MAX_VALUE)
                : (byte) (Math.round((decoded * BYTE_RANGE) - Byte.MAX_VALUE));
    }

    public static short encodeAsShort(float decoded, boolean isSigned) {
        return isSigned
                ? (short) (decoded * Short.MAX_VALUE)
                : (short) (Math.round((decoded * SHORT_RANGE) - Short.MAX_VALUE));
    }

    public static int encodeAsInt(float decoded, boolean isSigned) {
        return isSigned
                ? (int) (decoded * Integer.MAX_VALUE)
                : (int) (Math.round((decoded * INT_RANGE) - Integer.MAX_VALUE));
    }

    public static float decodeByte(byte encoded, boolean isSigned) {
        return isSigned
                ? (float) ((double) encoded / Byte.MAX_VALUE)
                : (float) ((double) (encoded + Byte.MAX_VALUE) / BYTE_RANGE);
    }

    public static float decodeShort(short encoded, boolean isSigned) {
        return isSigned
                ? (float) ((double) encoded / Short.MAX_VALUE)
                : (float) ((double) (encoded + Short.MAX_VALUE) / SHORT_RANGE);
    }

    public static float decodeInt(int encoded, boolean isSigned) {
        return isSigned
                ? (float) ((double) encoded / Integer.MAX_VALUE)
                : (float) ((double) (encoded + Integer.MAX_VALUE) / INT_RANGE);
    }
}
