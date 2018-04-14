package com.ripplargames.meshio.indices;

import java.nio.ByteBuffer;

import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.util.BufferUtil;

public class IntIndicesDataType extends AIndicesDataType {
    @Override
    public int bytesPerDatum() {
        return 4;
    }

    @Override
    public void setValue(ByteBuffer buffer, int index, int value) throws MeshIOException {
        buffer.putInt(index, value);
    }

    @Override
    public boolean isValidVertexCount(int vertexCount) {
        return (vertexCount >= 3);// && (vertexCount <= 0x7fffffff); always true
    }

    @Override
    public ByteBuffer toByteBuffer(int[] array) throws MeshIOException {
        ByteBuffer buffer = BufferUtil.createByteBuffer(array.length);
        for (int index : array) {
            buffer.putInt(index);
        }
        return buffer;
    }
}
