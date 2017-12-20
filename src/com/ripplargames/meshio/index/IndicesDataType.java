package com.ripplargames.meshio.index;

import java.nio.ByteBuffer;

public interface IndicesDataType<T> {
    boolean isValidVertexCount(int vertexCount);

    T createEmptyArray();

    T createNewArray(T previousArray, int newLength);

    int getValue(T array, int index);

    void setValue(T array, int index, int value);

    ByteBuffer toByteBuffer(T array);

    int bytesPerDatum();
}
