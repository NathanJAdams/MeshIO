package com.ripplar_games.meshio.mesh;

import com.ripplar_games.meshio.IMeshBuilder;
import com.ripplar_games.meshio.MeshVertexType;

public class NullMeshBuilder implements IMeshBuilder<NullMesh> {
   private static final NullMesh MESH = new NullMesh();

   @Override
   public MeshVertexType[] getVertexFormat() {
      return MESH.getVertexFormat();
   }

   @Override
   public int getVertexCount() {
      return MESH.getVertexCount();
   }

   @Override
   public int getFaceCount() {
      return MESH.getFaceCount();
   }

   @Override
   public void setVertexCount(int vertexCount) {
   }

   @Override
   public void setFaceCount(int faceCount) {
   }

   @Override
   public void setVertexData(int vertexIndex, float[] vertexData) {
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
   }

   @Override
   public NullMesh build() {
      return MESH;
   }
}
