package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;

import com.ripplargames.meshio.IMeshInfo;
import com.ripplargames.meshio.vertex.VertexFormat;

public interface IMesh extends IMeshInfo {
    boolean isValid();

    ByteBuffer getVertices(VertexFormat format);

    ByteBuffer getIndices();
}
