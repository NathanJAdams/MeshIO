package com.ripplargames.mesh_io;

import java.util.Set;

import com.ripplargames.mesh_io.vertex.VertexFormat;

public interface IMeshInfo {
    Set<VertexFormat> getVertexFormats();

    int getVertexCount();

    int getFaceCount();
}
