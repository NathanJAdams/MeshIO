package com.ripplar_games.mesh_io.index;

import java.nio.ByteBuffer;

public interface IndicesDataType<T> {
    boolean isValidVertexCount(int vertexCount);

    T createEmptyArray();

    T createNewArray(T previousArray, int newLength);

    int getValue(T array, int index);

    void setValue(T array, int index, int value);

    ByteBuffer toByteBuffer(T array);
}
