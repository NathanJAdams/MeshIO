package com.ripplar_games.mesh_io.vertex;

public class VertexAlignedSubFormat {
    private final int offset;
    private final VertexDataType dataType;

    public VertexAlignedSubFormat(int offset, VertexDataType dataType) {
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
        VertexAlignedSubFormat other = (VertexAlignedSubFormat) obj;
        return (offset == other.offset)
                && (dataType == other.dataType);
    }

    @Override
    public int hashCode() {
        return 31 * offset + dataType.hashCode();
    }
}
