package com.ripplargames.meshio.vertex;

public class VertexFormatPart {
    private final VertexType vertexType;
    private final VertexDataType dataType;

    public VertexFormatPart(VertexType vertexType, VertexDataType dataType) {
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
