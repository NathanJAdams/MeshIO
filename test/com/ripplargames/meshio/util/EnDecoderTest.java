package com.ripplargames.meshio.util;

import org.junit.Assert;
import org.junit.Test;

public class EnDecoderTest {
    private static final double DELTA = 0;

    @Test
    public void testZeroRange() {
        EnDecoder endecoder = new EnDecoder(1, 1);
        Assert.assertEquals(0, endecoder.encodeAsByte(12), DELTA);
        Assert.assertEquals(1, endecoder.decodeByte((byte) 45), DELTA);
        Assert.assertEquals(0, endecoder.encodeAsShort(123), DELTA);
        Assert.assertEquals(1, endecoder.decodeShort((short) 456), DELTA);
        Assert.assertEquals(0, endecoder.encodeAsInt(123), DELTA);
        Assert.assertEquals(1, endecoder.decodeInt(456), DELTA);
    }

    @Test
    public void testRanges() {
        testLargeRange(-100, 100);
        testLargeRange(-100, 0);
        testLargeRange(0, 100);
        testLargeRange(100, 200);
    }

    private void testLargeRange(float min, float max) {
        EnDecoder endecoder = new EnDecoder(min, max);
        float half = (min + max) / 2;
        Assert.assertEquals(-Byte.MAX_VALUE, endecoder.encodeAsByte(min), DELTA);
        Assert.assertEquals(0, endecoder.encodeAsByte(half), DELTA);
        Assert.assertEquals(Byte.MAX_VALUE, endecoder.encodeAsByte(max), DELTA);
        Assert.assertEquals(-Short.MAX_VALUE, endecoder.encodeAsShort(min), DELTA);
        Assert.assertEquals(0, endecoder.encodeAsShort(half), DELTA);
        Assert.assertEquals(Short.MAX_VALUE, endecoder.encodeAsShort(max), DELTA);
        Assert.assertEquals(-Integer.MAX_VALUE, endecoder.encodeAsInt(min), DELTA);
        Assert.assertEquals(0, endecoder.encodeAsInt(half), DELTA);
        Assert.assertEquals(Integer.MAX_VALUE, endecoder.encodeAsInt(max), DELTA);
    }

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
        float min = isSigned ? -1 : 0;
        EnDecoder endecoder = new EnDecoder(min, 1);
        byte encoded = endecoder.encodeAsByte(original);
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
        float min = isSigned ? -1 : 0;
        EnDecoder endecoder = new EnDecoder(min, 1);
        short encoded = endecoder.encodeAsShort(original);
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
        float min = isSigned ? -1 : 0;
        EnDecoder endecoder = new EnDecoder(min, 1);
        int encoded = endecoder.encodeAsInt(original);
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
        float min = isSigned ? -1 : 0;
        EnDecoder endecoder = new EnDecoder(min, 1);
        double decoded = endecoder.decodeByte(encoded);
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
        float min = isSigned ? -1 : 0;
        EnDecoder endecoder = new EnDecoder(min, 1);
        double decoded = endecoder.decodeShort(encoded);
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
        float min = isSigned ? -1 : 0;
        EnDecoder endecoder = new EnDecoder(min, 1);
        double decoded = endecoder.decodeInt(encoded);
        Assert.assertEquals(expected, decoded, DELTA);
    }
}
