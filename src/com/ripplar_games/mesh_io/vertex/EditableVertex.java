package com.ripplar_games.mesh_io.vertex;

public class EditableVertex {
    private final float[] data = new float[VertexType.valuesList().size()];

    public EditableVertex() {
        clear();
    }

    public void clear() {
        for (VertexType vertexType : VertexType.valuesList())
            setDatum(vertexType, vertexType.defaultValue());
    }

    public float getDatum(VertexType vertexType) {
        return data[vertexType.ordinal()];
    }

    public void setDatum(VertexType vertexType, float value) {
        data[vertexType.ordinal()] = value;
    }
}
