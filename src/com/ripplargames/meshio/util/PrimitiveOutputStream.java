package com.ripplargames.meshio.util;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PrimitiveOutputStream extends FilterOutputStream {
    private static final boolean DEFAULT_IS_BIG_ENDIAN = true;

    public PrimitiveOutputStream(OutputStream os) {
        super(new BufferedOutputStream(os));
    }

    public void writeFloat(float f) throws IOException {
        writeFloat(f, DEFAULT_IS_BIG_ENDIAN);
    }

    public void writeFloat(float f, boolean isBigEndian) throws IOException {
        int i = Float.floatToIntBits(f);
        writeLong(i, isBigEndian, 4);
    }

    public void writeDouble(double d) throws IOException {
        writeDouble(d, DEFAULT_IS_BIG_ENDIAN);
    }

    public void writeDouble(double d, boolean isBigEndian) throws IOException {
        long l = Double.doubleToLongBits(d);
        writeLong(l, isBigEndian, 8);
    }

    public void writeByte(byte b) throws IOException {
        writeLong(b, true, 1);
    }

    public void writeShort(short s) throws IOException {
        writeShort(s, DEFAULT_IS_BIG_ENDIAN);
    }

    public void writeShort(short s, boolean isBigEndian) throws IOException {
        writeLong(s, isBigEndian, 2);
    }

    public void writeInt(int i) throws IOException {
        writeInt(i, DEFAULT_IS_BIG_ENDIAN);
    }

    public void writeInt(int i, boolean isBigEndian) throws IOException {
        writeLong(i, isBigEndian, 4);
    }

    public void writeLong(long l) throws IOException {
        writeLong(l, DEFAULT_IS_BIG_ENDIAN);
    }

    public void writeLong(long l, boolean isBigEndian) throws IOException {
        writeLong(l, isBigEndian, 8);
    }

    public void writeLong(long l, boolean isBigEndian, int numBytes) throws IOException {
        if (numBytes <= 0)
            throw new IllegalArgumentException("Cannot write a non positive {" + numBytes + "} number of bytes");
        else if (numBytes > 8)
            throw new IllegalArgumentException("Cannot write {" + numBytes + "} bytes, maximum is 8");
        byte[] bytes = new byte[numBytes];
        if (isBigEndian)
            for (int i = 0; i < numBytes; i++)
                bytes[i] = (byte) (l >>> (8 * (numBytes - 1 - i)));
        else
            for (int i = numBytes - 1; i >= 0; i--)
                bytes[i] = (byte) (l >>> (8 * i));
        write(bytes);
    }

    public void writeLine(String line) throws IOException {
        int length = line.length();
        byte[] bytes = new byte[length + 1];
        for (int i = 0; i < line.length(); i++)
            bytes[i] = (byte) line.charAt(i);
        bytes[length] = '\n';
        write(bytes);
    }
}
