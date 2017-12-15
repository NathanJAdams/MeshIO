package com.ripplar_games.mesh_io.vertex;

public class AlignedVertexFormatPart {
    private final int offset;
    private final VertexDataType dataType;

    public AlignedVertexFormatPart(int offset, VertexDataType dataType) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be zero or greater");
        }
        this.offset = offset;
        if (dataType == null) {
            throw new NullPointerException("Data Type must not be null");
        }
        this.dataType = dataType;
    }

    public int getOffset() {
        return offset;
    }

    public VertexDataType getDataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AlignedVertexFormatPart other = (AlignedVertexFormatPart) obj;
        return (offset == other.offset)
                && (dataType == other.dataType);
    }

    @Override
    public int hashCode() {
        return 31 * offset + dataType.hashCode();
    }
}
