package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;

import com.ripplar_games.mesh_io.DatumEnDecoder;

public enum VertexDataType {
    Float(4) {
        @Override
        public void appendDatum(ByteBuffer buffer, int index, float datum) {
            buffer.putFloat(index, datum);
        }
    },
    NormalisedSignedByte(1) {
        @Override
        public void appendDatum(ByteBuffer buffer, int index, float datum) {
            byte encoded = DatumEnDecoder.encodeAsByte(datum, true);
            buffer.put(index, encoded);
        }
    },
    NormalisedSignedShort(2) {
        @Override
        public void appendDatum(ByteBuffer buffer, int index, float datum) {
            short encoded = DatumEnDecoder.encodeAsShort(datum, true);
            buffer.putShort(index, encoded);
        }
    },
    NormalisedSignedInt(4) {
        @Override
        public void appendDatum(ByteBuffer buffer, int index, float datum) {
            int encoded = DatumEnDecoder.encodeAsInt(datum, true);
            buffer.putInt(index, encoded);
        }
    },
    NormalisedUnsignedByte(1) {
        @Override
        public void appendDatum(ByteBuffer buffer, int index, float datum) {
            byte encoded = DatumEnDecoder.encodeAsByte(datum, false);
            buffer.put(index, encoded);
        }
    },
    NormalisedUnsignedShort(2) {
        @Override
        public void appendDatum(ByteBuffer buffer, int index, float datum) {
            short encoded = DatumEnDecoder.encodeAsShort(datum, false);
            buffer.putShort(index, encoded);
        }
    },
    NormalisedUnsignedInt(4) {
        @Override
        public void appendDatum(ByteBuffer buffer, int index, float datum) {
            int encoded = DatumEnDecoder.encodeAsInt(datum, false);
            buffer.putInt(index, encoded);
        }
    };
    private final int byteCount;

    VertexDataType(int byteCount) {
        this.byteCount = byteCount;
    }

    public int getByteCount() {
        return byteCount;
    }

    public abstract void appendDatum(ByteBuffer buffer, int index, float datum);
}
