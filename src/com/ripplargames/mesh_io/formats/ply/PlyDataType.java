package com.ripplargames.mesh_io.formats.ply;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ripplargames.mesh_io.io.PrimitiveInputStream;
import com.ripplargames.mesh_io.io.PrimitiveOutputStream;

public enum PlyDataType {
    Uchar("uchar", 1, Byte.MAX_VALUE),
    Char("char", 1, 0xFF),
    Ushort("ushort", 2, java.lang.Short.MAX_VALUE),
    Short("short", 2, 0xFFFF),
    Uint("uint", 4, Integer.MAX_VALUE),
    Int("int", 4, 0xFFFFFFFF),
    Ulong("ulong", 8, java.lang.Long.MAX_VALUE),
    Long("long", 8, 0xFFFFFFFFFFFFFFFFL),
    Float("float") {
        @Override
        public long readInteger(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
            return (long) readReal(pis, isBigEndian);
        }

        @Override
        public double readReal(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
            return pis.readFloat(isBigEndian);
        }

        @Override
        public void writeInteger(PrimitiveOutputStream pos, boolean isBigEndian, long integer) throws IOException {
            writeReal(pos, isBigEndian, integer);
        }

        @Override
        public void writeReal(PrimitiveOutputStream pos, boolean isBigEndian, double real) throws IOException {
            pos.writeFloat((float) real, isBigEndian);
        }
    },
    Double("double") {
        @Override
        public long readInteger(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
            return (long) readReal(pis, isBigEndian);
        }

        @Override
        public double readReal(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
            return pis.readDouble(isBigEndian);
        }

        @Override
        public void writeInteger(PrimitiveOutputStream pos, boolean isBigEndian, long integer) throws IOException {
            writeReal(pos, isBigEndian, integer);
        }

        @Override
        public void writeReal(PrimitiveOutputStream pos, boolean isBigEndian, double real) throws IOException {
            pos.writeDouble(real, isBigEndian);
        }
    };
    private static final Map<String, PlyDataType> BY_REPRESENTATION = new HashMap<String, PlyDataType>();

    static {
        for (PlyDataType dataType : values())
            BY_REPRESENTATION.put(dataType.representation, dataType);
    }

    private final String representation;
    private final int byteCount;
    private final long bitMask;

    private PlyDataType(String representation) {
        this(representation, 0, 0);
    }

    private PlyDataType(String representation, int byteCount, long bitMask) {
        this.representation = representation;
        this.byteCount = byteCount;
        this.bitMask = bitMask;
    }

    public static PlyDataType getDataType(String representation) {
        return BY_REPRESENTATION.get(representation);
    }

    public String getRepresentation() {
        return representation;
    }

    public long readInteger(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
        return pis.readLong(isBigEndian, byteCount) & bitMask;
    }

    public double readReal(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
        return readInteger(pis, isBigEndian);
    }

    public void writeInteger(PrimitiveOutputStream pos, boolean isBigEndian, long integer) throws IOException {
        pos.writeLong(integer, isBigEndian, byteCount);
    }

    public void writeReal(PrimitiveOutputStream pos, boolean isBigEndian, double real) throws IOException {
        writeInteger(pos, isBigEndian, (long) real);
    }
}
