package com.ripplar_games.mesh_io;

import com.ripplar_games.mesh_io.mesh.IMesh;

public interface IMeshBuilder<T extends IMesh> extends IMeshInfo {
   void setVertexCount(int vertexCount);

   void setFaceCount(int faceCount);

   void setVertexData(int vertexIndex, float[] vertexData);

   void setFaceIndices(int faceIndex, int[] faceIndices);

   T build();
}
