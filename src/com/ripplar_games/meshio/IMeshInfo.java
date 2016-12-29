package com.ripplar_games.meshio;

public interface IMeshInfo {
   MeshVertexType[] getVertexFormat();

   int getVertexCount();

   int getFaceCount();
}
