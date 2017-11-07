package com.ripplar_games.mesh_io;

import com.ripplar_games.mesh_io.vertex.VertexFormat;

public interface IMeshInfo {
    VertexFormat getVertexFormat();

    int getVertexCount();

    int getFaceCount();
}
