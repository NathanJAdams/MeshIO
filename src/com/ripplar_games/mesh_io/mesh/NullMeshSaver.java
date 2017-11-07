package com.ripplar_games.mesh_io.mesh;

import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

public class NullMeshSaver implements IMeshSaver {
    private static final IMesh MESH = new NullMesh();

    @Override
    public VertexFormat getVertexFormat() {
        return MESH.getVertexFormat();
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
    public void fillFaceIndices(int faceIndex, int[] faceIndices) {
    }
}
