package com.ripplar_games.mesh_io;

public interface IMeshInfo {
   MeshVertexType[] getVertexFormat();

   int getVertexCount();

   int getFaceCount();
}
