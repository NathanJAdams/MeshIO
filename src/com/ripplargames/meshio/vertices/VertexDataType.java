package com.ripplargames.meshio.vertices;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ripplargames.meshio.util.EnDecoder;

public enum VertexDataType {
    FLOAT(4) {
        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            buffer.putFloat(index, datum);
        }
    },
    BYTE_SIGNED(1) {
        private final EnDecoder endecoder = new EnDecoder(-0x80, 0x7F);

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            byte encoded = endecoder.encodeAsByte(datum);
            buffer.put(index, encoded);
        }
    },
    BYTE_UNSIGNED(1) {
        private final EnDecoder endecoder = new EnDecoder(0, 0xFF);

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            byte encoded = endecoder.encodeAsByte(datum);
            buffer.put(index, encoded);
        }
    },
    SHORT_SIGNED(2) {
        private final EnDecoder endecoder = new EnDecoder(-0x8000, 0x7FFF);

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            short encoded = endecoder.encodeAsShort(datum);
            buffer.putShort(index, encoded);
        }
    },
    SHORT_UNSIGNED(2) {
        private final EnDecoder endecoder = new EnDecoder(0, 0xFFFF);

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            short encoded = endecoder.encodeAsShort(datum);
            buffer.putShort(index, encoded);
        }
    },
    INT_SIGNED(4) {
        private final EnDecoder endecoder = new EnDecoder(-0x80000000, 0x7FFFFFFF);

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            int encoded = endecoder.encodeAsInt(datum);
            buffer.putInt(index, encoded);
        }
    },
    INT_UNSIGNED(4) {
        private final EnDecoder endecoder = new EnDecoder(0, 0xFFFFFFFF);

        @Override
        public void setDatum(ByteBuffer buffer, int index, float datum) {
            int encoded = endecoder.encodeAsInt(datum);
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

    public int byteCount() {
        return byteCount;
    }

    public abstract void setDatum(ByteBuffer buffer, int index, float datum);
}
