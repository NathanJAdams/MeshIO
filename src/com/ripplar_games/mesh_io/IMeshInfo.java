package com.ripplar_games.mesh_io;

import java.util.Set;

import com.ripplar_games.mesh_io.vertex.VertexFormat;

public interface IMeshInfo {
    Set<VertexFormat> getVertexFormats();

    int getVertexCount();

    int getFaceCount();
}
