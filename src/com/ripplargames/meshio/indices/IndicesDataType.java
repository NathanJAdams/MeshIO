package com.ripplargames.meshio.indices;

import java.nio.ByteBuffer;
import java.util.List;

import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.util.ImmutableIntArray;

public interface IndicesDataType {
    int bytesPerDatum();

    void setValue(ByteBuffer buffer, int index, int value) throws MeshIOException;

    ByteBuffer flatten(List<ImmutableIntArray> elements, int elementLength) throws MeshIOException;

    boolean isValidVertexCount(int vertexCount);

    ByteBuffer toByteBuffer(int[] indices) throws MeshIOException;
}
