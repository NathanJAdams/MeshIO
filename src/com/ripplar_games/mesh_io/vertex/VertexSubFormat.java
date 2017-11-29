package com.ripplar_games.mesh_io.vertex;

public class VertexSubFormat {
    private final VertexType vertexType;
    private final VertexDataType dataType;

    public VertexSubFormat(VertexType vertexType, VertexDataType dataType) {
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
