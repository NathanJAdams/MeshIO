package com.ripplar_games.meshio.mesh;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

public class MeshIndices<T> {
   private final IndicesDataType<T> indicesDataType;
   private final MeshIndexType      meshIndexType;
   private T                        indices;

   public MeshIndices(IndicesDataType<T> indicesDataType, MeshIndexType meshIndexType) {
      this.indicesDataType = indicesDataType;
      this.meshIndexType = meshIndexType;
      this.indices = indicesDataType.createEmptyArray();
   }

   public void clear() {
      this.indices = indicesDataType.createEmptyArray();
   }

   public ByteBuffer getIndicesBuffer() {
      return indicesDataType.toByteBuffer(indices);
   }

   public boolean isValidVertexCount(int vertexCount) {
      return indicesDataType.isValidVertexCount(vertexCount);
   }

   public int getFaceCount() {
      return Array.getLength(indices) / meshIndexType.getOffsetsLength();
   }

   public void setFaceCount(int faceCount) {
      this.indices = indicesDataType.createNewArray(indices, faceCount * meshIndexType.getOffsetsLength());
   }

   public void setFaceIndex(int faceIndex, int faceCornerIndex, int vertexIndex) {
      meshIndexType.setFaceIndex(indicesDataType, indices, faceIndex, faceCornerIndex, vertexIndex);
   }

   public void getFaceIndices(int faceIndex, int[] faceIndices) {
      meshIndexType.getFaceIndices(indicesDataType, indices, faceIndex, faceIndices);
   }

   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      meshIndexType.setFaceIndices(indicesDataType, indices, faceIndex, faceIndices);
   }
}
