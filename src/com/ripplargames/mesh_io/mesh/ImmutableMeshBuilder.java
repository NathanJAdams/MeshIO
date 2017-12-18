package com.ripplargames.mesh_io.mesh;

import java.util.Set;

import com.ripplargames.mesh_io.IMeshBuilder;
import com.ripplargames.mesh_io.index.EditableIndices;
import com.ripplargames.mesh_io.index.IndicesDataType;
import com.ripplargames.mesh_io.vertex.LoadableVertices;
import com.ripplargames.mesh_io.vertex.VertexFormat;
import com.ripplargames.mesh_io.vertex.VertexType;

public class ImmutableMeshBuilder implements IMeshBuilder<ImmutableMesh> {
    private final EditableIndices<?> indices;
    private final LoadableVertices vertices;

    public <T> ImmutableMeshBuilder(MeshType meshType, IndicesDataType<T> indicesDataType, Set<VertexFormat> formats) {
        this.indices = new EditableIndices<T>(indicesDataType, meshType);
        this.vertices = new LoadableVertices(formats);
    }

    @Override
    public Set<VertexFormat> getVertexFormats() {
        return vertices.getFormatVertices().keySet();
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
        return new ImmutableMesh(getVertexCount(), getFaceCount(), vertices.getFormatVertices(), indices.getIndicesBuffer());
    }
}
