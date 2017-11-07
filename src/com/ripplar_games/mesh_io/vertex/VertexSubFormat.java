package com.ripplar_games.mesh_io.vertex;

public class VertexSubFormat {
    private final VertexType vertexType;
    private final VertexDatumDataType dataType;

    public VertexSubFormat(VertexType vertexType, VertexDatumDataType dataType) {
        this.vertexType = vertexType;
        this.dataType = dataType;
    }

    public VertexType getVertexType() {
        return vertexType;
    }

    public VertexDatumDataType getDataType() {
        return dataType;
    }
}
