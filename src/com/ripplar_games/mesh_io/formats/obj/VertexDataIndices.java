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
}
