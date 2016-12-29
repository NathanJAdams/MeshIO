package com.ripplar_games.meshio.mesh;

import java.nio.ByteBuffer;

public interface IndicesDataType<T> {
   boolean isValidVertexCount(int vertexCount);

   T createEmptyArray();

   T createNewArray(T previousArray, int newLength);

   void setValue(T array, int index, int value);

   ByteBuffer toByteBuffer(T array);
}
