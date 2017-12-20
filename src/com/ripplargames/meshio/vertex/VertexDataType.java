package com.ripplargames.meshio.vertex;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ripplargames.meshio.DatumEnDecoder;

public enum VertexDataType {
    Float(4) {
        @Override
        public float getDatum(ByteBuffer buffer, int index) {
            return buffer.getFloat(index);
        }

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            buffer.putFloat(index, datum);
        }
    },
    NormalisedSignedByte(1) {
        @Override
        public float getDatum(ByteBuffer buffer, int index) {
            byte encoded = buffer.get(index);
            return (float) DatumEnDecoder.decodeByte(encoded, true);
        }

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            byte encoded = DatumEnDecoder.encodeAsByte(datum, true);
            buffer.put(index, encoded);
        }
    },
    NormalisedSignedShort(2) {
        @Override
        public float getDatum(ByteBuffer buffer, int index) {
            short encoded = buffer.getShort(index);
            return (float) DatumEnDecoder.decodeShort(encoded, true);
        }

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            short encoded = DatumEnDecoder.encodeAsShort(datum, true);
            buffer.putShort(index, encoded);
        }
    },
    NormalisedSignedInt(4) {
        @Override
        public float getDatum(ByteBuffer buffer, int index) {
            int encoded = buffer.getInt(index);
            return (float) DatumEnDecoder.decodeInt(encoded, true);
        }

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            int encoded = DatumEnDecoder.encodeAsInt(datum, true);
            buffer.putInt(index, encoded);
        }
    },
    NormalisedUnsignedByte(1) {
        @Override
        public float getDatum(ByteBuffer buffer, int index) {
            byte encoded = buffer.get(index);
            return (float) DatumEnDecoder.decodeByte(encoded, false);
        }

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            byte encoded = DatumEnDecoder.encodeAsByte(datum, false);
            buffer.put(index, encoded);
        }
    },
    NormalisedUnsignedShort(2) {
        @Override
        public float getDatum(ByteBuffer buffer, int index) {
            short encoded = buffer.getShort(index);
            return (float) DatumEnDecoder.decodeShort(encoded, false);
        }

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            short encoded = DatumEnDecoder.encodeAsShort(datum, false);
            buffer.putShort(index, encoded);
        }
    },
    NormalisedUnsignedInt(4) {
        @Override
        public float getDatum(ByteBuffer buffer, int index) {
            int encoded = buffer.getInt(index);
            return (float) DatumEnDecoder.decodeInt(encoded, false);
        }

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            int encoded = DatumEnDecoder.encodeAsInt(datum, false);
            buffer.putInt(index, encoded);
        }
    };
    private final int byteCount;
    private static final List<VertexDataType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

    public static List<VertexDataType> valuesList() {
        return VALUES;
    }

    VertexDataType(int byteCount) {
        this.byteCount = byteCount;
    }

    public int getByteCount() {
        return byteCount;
    }

    public abstract float getDatum(ByteBuffer buffer, int index);

    public abstract void setDatum(ByteBuffer buffer, int index, float datum);
}
