package com.ripplargames.meshio.meshformats.ply;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;

public enum PlyDataType {
    Uchar(1, Byte.MAX_VALUE, "uchar", "uint8"),
    Char(1, 0xFF, "char", "int8"),
    Ushort(2, java.lang.Short.MAX_VALUE, "ushort", "uint16"),
    Short(2, 0xFFFF, "short", "int16"),
    Uint(4, Integer.MAX_VALUE, "uint", "uint32"),
    Int(4, 0xFFFFFFFF, "int", "int32"),
    Ulong(8, java.lang.Long.MAX_VALUE, "ulong", "uint64"),
    Long(8, 0xFFFFFFFFFFFFFFFFL, "long", "int64"),
    Float("float", "float32") {
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
    Double("double", "float64") {
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
            for (String representation : dataType.representations)
                BY_REPRESENTATION.put(representation, dataType);
    }

    private final String[] representations;
    private final int byteCount;
    private final long bitMask;

    PlyDataType(String... representations) {
        this(0, 0, representations);
    }

    PlyDataType(int byteCount, long bitMask, String... representations) {
        this.representations = representations;
        this.byteCount = byteCount;
        this.bitMask = bitMask;
    }

    public static PlyDataType getDataType(String representation) {
        return BY_REPRESENTATION.get(representation);
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
