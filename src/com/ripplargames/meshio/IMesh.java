package com.ripplargames.meshio;

import java.nio.ByteBuffer;
import java.util.Set;

import com.ripplargames.meshio.vertices.VertexFormat;

public interface IMesh {
    boolean isValid();

    Set<VertexFormat> getBufferFormats();

    int getVertexCount();

    int getFaceCount();

    ByteBuffer getVertices(VertexFormat format);

    ByteBuffer getIndices();

    Mesh toRawData();
}
