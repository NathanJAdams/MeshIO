package com.ripplargames.meshio;

import org.junit.Assert;
import org.junit.Test;

public class DatumEnDecoderTest {
    private static final double DELTA = 0;

    @Test
    public void testEncodeAsByte() {
        testEncodedAsByte((byte) -Byte.MAX_VALUE, 0, false);
        testEncodedAsByte((byte) 0, 0.5f, false);
        testEncodedAsByte(Byte.MAX_VALUE, 1, false);
        testEncodedAsByte((byte) -Byte.MAX_VALUE, -1, true);
        testEncodedAsByte((byte) 0, 0, true);
        testEncodedAsByte(Byte.MAX_VALUE, 1, true);
    }

    private void testEncodedAsByte(byte expected, float original, boolean isSigned) {
        byte encoded = DatumEnDecoder.encodeAsByte(original, isSigned);
        Assert.assertEquals(expected, encoded);
    }

    @Test
    public void testEncodeAsShort() {
        testEncodedAsShort((short) -Short.MAX_VALUE, 0, false);
        testEncodedAsShort((short) 0, 0.5f, false);
        testEncodedAsShort(Short.MAX_VALUE, 1, false);
        testEncodedAsShort((short) -Short.MAX_VALUE, -1, true);
        testEncodedAsShort((short) 0, 0, true);
        testEncodedAsShort(Short.MAX_VALUE, 1, true);
    }

    private void testEncodedAsShort(short expected, float original, boolean isSigned) {
        short encoded = DatumEnDecoder.encodeAsShort(original, isSigned);
        Assert.assertEquals(expected, encoded);
    }

    @Test
    public void testEncodeAsInt() {
        testEncodedAsInt(-Integer.MAX_VALUE, 0, false);
        testEncodedAsInt(0, 0.5f, false);
        testEncodedAsInt(Integer.MAX_VALUE, 1, false);
        testEncodedAsInt(-Integer.MAX_VALUE, -1, true);
        testEncodedAsInt(0, 0, true);
        testEncodedAsInt(Integer.MAX_VALUE, 1, true);
    }

    private void testEncodedAsInt(int expected, float original, boolean isSigned) {
        int encoded = DatumEnDecoder.encodeAsInt(original, isSigned);
        Assert.assertEquals(expected, encoded);
    }

    @Test
    public void testDecodeByte() {
        testDecodeByte(0, (byte) -Byte.MAX_VALUE, false);
        testDecodeByte(0.5, (byte) 0, false);
        testDecodeByte(1, Byte.MAX_VALUE, false);
        testDecodeByte(-1, (byte) -Byte.MAX_VALUE, true);
        testDecodeByte(0, (byte) 0, true);
        testDecodeByte(1, Byte.MAX_VALUE, true);
    }

    private void testDecodeByte(double expected, byte encoded, boolean isSigned) {
        double decoded = DatumEnDecoder.decodeByte(encoded, isSigned);
        Assert.assertEquals(expected, decoded, DELTA);
    }

    @Test
    public void testDecodeShort() {
        testDecodeShort(0, (short) -Short.MAX_VALUE, false);
        testDecodeShort(0.5, (short) 0, false);
        testDecodeShort(1, Short.MAX_VALUE, false);
        testDecodeShort(-1, (short) -Short.MAX_VALUE, true);
        testDecodeShort(0, (short) 0, true);
        testDecodeShort(1, Short.MAX_VALUE, true);
    }

    private void testDecodeShort(double expected, short encoded, boolean isSigned) {
        double decoded = DatumEnDecoder.decodeShort(encoded, isSigned);
        Assert.assertEquals(expected, decoded, DELTA);
    }

    @Test
    public void testDecodeInt() {
        testDecodeInt(0, -Integer.MAX_VALUE, false);
        testDecodeInt(0.5, 0, false);
        testDecodeInt(1, Integer.MAX_VALUE, false);
        testDecodeInt(-1, -Integer.MAX_VALUE, true);
        testDecodeInt(0, 0, true);
        testDecodeInt(1, Integer.MAX_VALUE, true);
    }

    private void testDecodeInt(double expected, int encoded, boolean isSigned) {
        double decoded = DatumEnDecoder.decodeInt(encoded, isSigned);
        Assert.assertEquals(expected, decoded, DELTA);
    }
}
