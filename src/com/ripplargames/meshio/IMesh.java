package com.ripplargames.meshio;

import java.nio.ByteBuffer;

import com.ripplargames.meshio.bufferformats.BufferFormat;

public interface IMesh extends IMeshInfo {
    boolean isValid();

    ByteBuffer getVertices(BufferFormat format);

    ByteBuffer getIndices();
}
