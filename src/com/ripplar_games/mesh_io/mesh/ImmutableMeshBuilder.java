package com.ripplar_games.mesh_io.mesh;

import java.util.Arrays;
import java.util.List;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.MeshVertexType;

public class ImmutableMeshBuilder<T> implements IMeshBuilder<ImmutableMesh<T>> {
    private final MeshIndices<T> indices;
    private final LoadableMeshVertices vertices;

    public ImmutableMeshBuilder(MeshIndexType meshIndexType, IndicesDataType<T> indicesDataType, MeshVertexType... format) {
        this(meshIndexType, indicesDataType, Arrays.asList(format));
    }

    public ImmutableMeshBuilder(MeshIndexType meshIndexType, IndicesDataType<T> indicesDataType, List<MeshVertexType> format) {
        this.indices = new MeshIndices<T>(indicesDataType, meshIndexType);
        this.vertices = new LoadableMeshVertices(format);
    }

    @Override
    public List<MeshVertexType> getVertexFormat() {
        return vertices.getFormat();
    }

    @Override
    public int getVertexCount() {
        return vertices.getVertexCount();
    }

    @Override
    public void setVertexCount(int vertexCount) {
        vertices.setVertexCount(vertexCount);
    }

    @Override
    public int getFaceCount() {
        return indices.getFaceCount();
    }

    @Override
    public void setFaceCount(int faceCount) {
        indices.setFaceCount(faceCount);
    }

    @Override
    public void clear() {
        indices.clear();
        vertices.clear();
    }

    @Override
    public void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float vertexDatum) {
        vertices.setVertexDatum(vertexIndex, meshVertexType, vertexDatum);
    }

    @Override
    public void setFaceIndices(int faceIndex, int[] faceIndices) {
        indices.setFaceIndices(faceIndex, faceIndices);
    }

    @Override
    public ImmutableMesh<T> build() {
        return new ImmutableMesh<T>(indices, vertices);
    }
}