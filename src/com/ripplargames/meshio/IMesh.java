package com.ripplargames.meshio;

import java.nio.ByteBuffer;
import java.util.Set;

import com.ripplargames.meshio.vertices.BufferFormat;

public interface IMesh {
    boolean isValid();

    Set<BufferFormat> getBufferFormats();

    int getVertexCount();

    int getFaceCount();

    ByteBuffer getVertices(BufferFormat format);

    ByteBuffer getIndices();

    Mesh toRawData();
}
