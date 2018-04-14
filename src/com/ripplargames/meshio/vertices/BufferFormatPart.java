package com.ripplargames.meshio.vertices;

public class BufferFormatPart {
    private final VertexType vertexType;
    private final VertexDataType dataType;

    public BufferFormatPart(VertexType vertexType, VertexDataType dataType) {
        this.vertexType = vertexType;
        this.dataType = dataType;
    }

    public VertexType getVertexType() {
        return vertexType;
    }

    public VertexDataType getDataType() {
        return dataType;
    }
}
