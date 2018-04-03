package com.ripplargames.meshio;

import java.util.Set;

import com.ripplargames.meshio.bufferformats.BufferFormat;

public interface IMeshInfo {
    Set<BufferFormat> getVertexFormats();

    int getVertexCount();

    int getFaceCount();
}
