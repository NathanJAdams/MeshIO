package com.ripplar_games.mesh_io.vertex;

public class EditableVertex {
    private final float[] data = new float[VertexType.getValues().length];

    public void clear() {
        for (int i = 0; i < data.length; i++)
            data[i] = 0;
    }

    public float getDatum(VertexType vertexType) {
        return data[vertexType.ordinal()];
    }

    public void setDatum(VertexType vertexType, float value) {
        data[vertexType.ordinal()] = value;
    }
}
