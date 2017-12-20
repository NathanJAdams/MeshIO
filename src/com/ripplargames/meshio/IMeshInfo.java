package com.ripplargames.meshio;

import java.util.Set;

import com.ripplargames.meshio.vertex.VertexFormat;

public interface IMeshInfo {
    Set<VertexFormat> getVertexFormats();

    int getVertexCount();

    int getFaceCount();
}
