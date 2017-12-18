package com.ripplargames.mesh_io.mesh;

import java.util.Set;

import com.ripplargames.mesh_io.IMeshSaver;
import com.ripplargames.mesh_io.vertex.VertexFormat;
import com.ripplargames.mesh_io.vertex.VertexType;

public class NullMeshSaver implements IMeshSaver {
    private static final IMesh MESH = new NullMesh();

    @Override
    public Set<VertexFormat> getVertexFormats() {
        return MESH.getVertexFormats();
    }

    @Override
    public int getVertexCount() {
        return MESH.getVertexCount();
    }

    @Override
    public int getFaceCount() {
        return MESH.getFaceCount();
    }

    @Override
    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return 0;
    }

    @Override
    public int[] getFaceIndices(int faceIndex) {
        return new int[]{0, 1, 2};
    }
}
