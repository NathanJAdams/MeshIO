package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;

import com.ripplar_games.mesh_io.IMeshInfo;

public interface IMesh extends IMeshInfo {
   void clear();

   boolean isValid();

   ByteBuffer getVertices();

   ByteBuffer getIndices();
}