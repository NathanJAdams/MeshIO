package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;

import com.ripplar_games.mesh_io.MeshVertexType;

public class LoadableMeshVertices {
   private final MeshVertexType[] format;
   private float[]                vertices;

   public LoadableMeshVertices(MeshVertexType... format) {
      this.format = format;
   }

   public void clear() {
      this.vertices = new float[0];
   }

   public ByteBuffer toByteBuffer() {
      return BufferUtil.with(vertices);
   }

   public MeshVertexType[] getFormat() {
      return format;
   }

   public int getVertexCount() {
      return (format.length == 0)
            ? 0
            : vertices.length / format.length;
   }

   public void setVertexCount(int vertexCount) {
      this.vertices = new float[vertexCount * format.length];
   }

   public void setVertexData(int vertexIndex, float[] vertexData) {
      int offset = vertexIndex * format.length;
      for (int i = 0; i < vertexData.length && i < format.length; i++)
         vertices[offset + i] = vertexData[i];
   }
}
