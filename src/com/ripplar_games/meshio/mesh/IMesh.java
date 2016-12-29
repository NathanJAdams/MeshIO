package com.ripplar_games.meshio.mesh;

import java.nio.ByteBuffer;

import com.ripplar_games.meshio.IMeshInfo;

public interface IMesh extends IMeshInfo {
   void clear();

   boolean isValid();

   ByteBuffer getVertices();

   ByteBuffer getIndices();
}
