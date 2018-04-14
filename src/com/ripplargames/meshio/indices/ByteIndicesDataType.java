package com.ripplargames.meshio.indices;

import java.nio.ByteBuffer;

import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.util.BufferUtil;

public class ByteIndicesDataType extends AIndicesDataType {
    @Override
    public int bytesPerDatum() {
        return 1;
    }

    @Override
    public void setValue(ByteBuffer buffer, int index, int value) throws MeshIOException {
        byte byteValue = (byte) value;
        if (byteValue != value) {
            throwInvalidDataType(java.lang.Byte.TYPE, value);
        }
        buffer.put(index, (byte) value);
    }

    @Override
    public boolean isValidVertexCount(int vertexCount) {
        return (vertexCount >= 3) && (vertexCount <= 0x7f);
    }

    @Override
    public ByteBuffer toByteBuffer(int[] array) throws MeshIOException {
        ByteBuffer buffer = BufferUtil.createByteBuffer(array.length);
        for (int index : array) {
            byte indexByte = (byte) index;
            if (index != indexByte) {
                throw new MeshIOException("Index: " + index + " does not fit into a byte");
            }
            buffer.put(indexByte);
        }
        return buffer;
    }
}
