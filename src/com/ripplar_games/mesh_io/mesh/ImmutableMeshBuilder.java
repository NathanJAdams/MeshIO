package com.ripplar_games.mesh_io.mesh;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.index.EditableIndices;
import com.ripplar_games.mesh_io.index.IndicesDataType;
import com.ripplar_games.mesh_io.vertex.LoadableVertices;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

public class ImmutableMeshBuilder implements IMeshBuilder<ImmutableMesh> {
    private final EditableIndices<?> indices;
    private final LoadableVertices vertices;

    public <T> ImmutableMeshBuilder(MeshType meshType, IndicesDataType<T> indicesDataType, VertexFormat format) {
        this.indices = new EditableIndices<T>(indicesDataType, meshType);
        this.vertices = new LoadableVertices(format);
    }

    @Override
    public VertexFormat getVertexFormat() {
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
    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
        vertices.setVertexDatum(vertexIndex, vertexType, vertexDatum);
    }

    @Override
    public void setFaceIndices(int faceIndex, int[] faceIndices) {
        indices.setFaceIndices(faceIndex, faceIndices);
    }

    @Override
    public ImmutableMesh build() {
        return new ImmutableMesh(getVertexFormat(), getVertexCount(), getFaceCount(), indices.getIndicesBuffer(), vertices.getVerticesBuffer());
    }
}
