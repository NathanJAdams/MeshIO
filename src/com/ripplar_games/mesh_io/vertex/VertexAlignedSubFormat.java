package com.ripplar_games.mesh_io.vertex;

public class VertexAlignedSubFormat {
    private final int offset;
    private final VertexDatumDataType dataType;

    public VertexAlignedSubFormat(int offset, VertexDatumDataType dataType) {
        this.offset = offset;
        this.dataType = dataType;
    }

    public int getOffset() {
        return offset;
    }

    public VertexDatumDataType getDataType() {
        return dataType;
    }
}
