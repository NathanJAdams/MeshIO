package com.ripplargames.meshio.mesh;

import java.util.Set;

import com.ripplargames.meshio.IMeshBuilder;
import com.ripplargames.meshio.vertex.VertexFormat;
import com.ripplargames.meshio.vertex.VertexType;

public class NullMeshBuilder implements IMeshBuilder<NullMesh> {
    private static final NullMesh MESH = new NullMesh();

    @Override
    public Set<VertexFormat> getVertexFormats() {
        return MESH.getVertexFormats();
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
