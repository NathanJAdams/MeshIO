package com.ripplar_games.mesh_io.mesh;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

public class NullMeshBuilder implements IMeshBuilder<NullMesh> {
    private static final NullMesh MESH = new NullMesh();

    @Override
    public VertexFormat getVertexFormat() {
        return MESH.getVertexFormat();
    }

    @Override
    public int getVertexCount() {
        return MESH.getVertexCount();
    }

    @Override
    public void setVertexCount(int vertexCount) {
    }

    @Override
    public int getFaceCount() {
        return MESH.getFaceCount();
    }

    @Override
    public void setFaceCount(int faceCount) {
    }

    @Override
    public void clear() {
    }

    @Override
    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
    }

    @Override
    public void setFaceIndices(int faceIndex, int[] faceIndices) {
    }

    @Override
    public NullMesh build() {
        return MESH;
    }
}
