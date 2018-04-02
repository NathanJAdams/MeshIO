package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.IMesh;
import com.ripplargames.meshio.vertex.VertexFormat;

public class ImmutableMesh implements IMesh {
    private final int vertexCount;
    private final int faceCount;
    private final Map<VertexFormat, ByteBuffer> formatVertices;
    private final ByteBuffer indices;
    private final boolean isValid;

    public ImmutableMesh(int vertexCount, int faceCount, Map<VertexFormat, ByteBuffer> formatVertices, ByteBuffer indices) {
        this.vertexCount = vertexCount;
        this.faceCount = faceCount;
        this.formatVertices = formatVertices;
        this.indices = indices;
        this.isValid = (!formatVertices.isEmpty()) && (vertexCount >= 3) && (faceCount > 0) && (indices != null);
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public ByteBuffer getVertices(VertexFormat format) {
        return formatVertices.get(format);
    }

    @Override
    public ByteBuffer getIndices() {
        return indices;
    }

    @Override
    public Set<VertexFormat> getVertexFormats() {
        return formatVertices.keySet();
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
