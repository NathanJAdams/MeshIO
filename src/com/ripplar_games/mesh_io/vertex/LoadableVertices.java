package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;

public class LoadableVertices {
    private final VertexFormat format;
    private int vertexCount;
    private ByteBuffer vertices;

    public LoadableVertices(VertexFormat format) {
        this.format = format;
    }

    public void clear() {
        this.vertices = null;
    }

    public VertexFormat getFormat() {
        return format;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public ByteBuffer getVerticesBuffer() {
        return vertices;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
        int totalByteCount = format.getByteCount() * vertexCount;
        this.vertices = ByteBuffer.allocate(totalByteCount);
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
        VertexAlignedSubFormat alignedSubFormat = format.getAlignedSubFormat(vertexType);
        if (alignedSubFormat != null) {
            int offset = alignedSubFormat.getOffset();
            VertexDatumDataType dataType = alignedSubFormat.getDataType();
            int index = (vertexIndex * format.getByteCount()) + offset;
            dataType.appendDatum(vertices, index, vertexDatum);
        }
    }
}
