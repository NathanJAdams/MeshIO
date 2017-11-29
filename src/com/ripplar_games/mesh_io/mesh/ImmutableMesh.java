package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;

import com.ripplar_games.mesh_io.vertex.VertexFormat;

public class ImmutableMesh implements IMesh {
    private final VertexFormat format;
    private final int vertexCount;
    private final int faceCount;
    private final ByteBuffer vertices;
    private final ByteBuffer indices;
    private final boolean isValid;

    public ImmutableMesh(VertexFormat format, int vertexCount, int faceCount, ByteBuffer vertices, ByteBuffer indices) {
        this.format = format;
        this.vertexCount = vertexCount;
        this.faceCount = faceCount;
        this.vertices = vertices;
        this.indices = indices;
        this.isValid = (!format.isEmpty()) && (vertexCount >= 3) && (faceCount > 0) && (indices != null) && (vertices != null);
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public ByteBuffer getVertices() {
        return vertices;
    }

    @Override
    public ByteBuffer getIndices() {
        return indices;
    }

    @Override
    public VertexFormat getVertexFormat() {
        return format;
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public int getFaceCount() {
        return faceCount;
    }
}
