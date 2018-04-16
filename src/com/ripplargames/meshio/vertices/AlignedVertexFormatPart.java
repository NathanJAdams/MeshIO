package com.ripplargames.meshio.vertices;

public class AlignedVertexFormatPart {
    private final int offset;
    private final VertexDataType dataType;

    public AlignedVertexFormatPart(int offset, VertexDataType dataType) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be zero or greater");
        }
        if (dataType == null) {
            throw new NullPointerException("Data Type must not be null");
        }
        this.offset = offset;
        this.dataType = dataType;
    }

    public int offset() {
        return offset;
    }

    public VertexDataType dataType() {
        return dataType;
    }
}
