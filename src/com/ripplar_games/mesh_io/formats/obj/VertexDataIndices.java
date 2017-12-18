package com.ripplar_games.mesh_io.formats.obj;

public class VertexDataIndices {
    private final int positionIndex;
    private final int imageCoordIndex;
    private final int normalIndex;

    public VertexDataIndices(int positionIndex, int imageCoordIndex, int normalIndex) {
        this.positionIndex = positionIndex;
        this.imageCoordIndex = imageCoordIndex;
        this.normalIndex = normalIndex;
    }

    public int positionIndex() {
        return positionIndex;
    }

    public int imageCoordIndex() {
        return imageCoordIndex;
    }

    public int normalIndex() {
        return normalIndex;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        VertexDataIndices other = (VertexDataIndices) object;
        return (positionIndex == other.positionIndex) &&
                (imageCoordIndex == other.imageCoordIndex) &&
                (normalIndex == other.normalIndex);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash *= 31;
        hash += positionIndex;
        hash *= 31;
        hash += imageCoordIndex;
        hash *= 31;
        hash += normalIndex;
        return hash;
    }
}
