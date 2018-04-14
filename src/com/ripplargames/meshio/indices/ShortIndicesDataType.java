package com.ripplargames.meshio.indices;

import java.nio.ByteBuffer;

import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.util.BufferUtil;

public class ShortIndicesDataType extends AIndicesDataType {
    @Override
    public int bytesPerDatum() {
        return 2;
    }

    @Override
    public void setValue(ByteBuffer buffer, int index, int value) throws MeshIOException {
        short shortValue = (short) value;
        if (shortValue != value) {
            throwInvalidDataType(java.lang.Byte.TYPE, value);
        }
        buffer.putShort(index, (short) value);
    }

    @Override
    public boolean isValidVertexCount(int vertexCount) {
        return (vertexCount >= 3) && (vertexCount <= 0x7fff);
    }

    @Override
    public ByteBuffer toByteBuffer(int[] array) throws MeshIOException {
        ByteBuffer buffer = BufferUtil.createByteBuffer(array.length);
        for (int index : array) {
            short indexShort = (short) index;
            if (index != indexShort) {
                throw new MeshIOException("Index: " + index + " does not fit into a short");
            }
            buffer.putShort(indexShort);
        }
        return buffer;
    }
}
