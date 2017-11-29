package com.ripplar_games.mesh_io.vertex;

public class VertexAlignedSubFormat {
    private final int offset;
    private final VertexDataType dataType;

    public VertexAlignedSubFormat(int offset, VertexDataType dataType) {
        this.offset = offset;
        this.dataType = dataType;
    }

    public int getOffset() {
        return offset;
    }

    public VertexDataType getDataType() {
        return dataType;
    }
}
