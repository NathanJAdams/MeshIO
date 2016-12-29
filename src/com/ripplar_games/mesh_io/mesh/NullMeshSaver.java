package com.ripplar_games.mesh_io.mesh;

import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.MeshVertexType;

public class NullMeshSaver implements IMeshSaver {
   private static final IMesh MESH = new NullMesh();

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
   public void getVertexData(int vertexIndex, float[] vertexData) {
   }

   @Override
   public void getFaceIndices(int faceIndex, int[] faceIndices) {
   }
}
