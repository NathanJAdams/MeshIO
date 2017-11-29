package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;

import com.ripplar_games.mesh_io.IMeshInfo;
import com.ripplar_games.mesh_io.vertex.VertexFormat;

public interface IMesh extends IMeshInfo {
    boolean isValid();

    ByteBuffer getVertices(VertexFormat format);

    ByteBuffer getIndices();
}
