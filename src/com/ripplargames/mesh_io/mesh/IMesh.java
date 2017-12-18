package com.ripplargames.mesh_io.mesh;

import java.nio.ByteBuffer;

import com.ripplargames.mesh_io.IMeshInfo;
import com.ripplargames.mesh_io.vertex.VertexFormat;

public interface IMesh extends IMeshInfo {
    boolean isValid();

    ByteBuffer getVertices(VertexFormat format);

    ByteBuffer getIndices();
}
