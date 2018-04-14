package com.ripplargames.meshio.vertices;

public class AlignedBufferFormatPart {
    private final int offset;
    private final VertexDataType dataType;

    public AlignedBufferFormatPart(int offset, VertexDataType dataType) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be zero or greater");
        }
        this.offset = offset;
        if (dataType == null) {
            throw new NullPointerException("Data Type must not be null");
        }
        this.dataType = dataType;
    }

    public int offset() {
        return offset;
    }

    public VertexDataType dataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AlignedBufferFormatPart other = (AlignedBufferFormatPart) obj;
        return (offset == other.offset)
                && (dataType == other.dataType);
    }

    @Override
    public int hashCode() {
        return 31 * offset + dataType.hashCode();
    }
}
