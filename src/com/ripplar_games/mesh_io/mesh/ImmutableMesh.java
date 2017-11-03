package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;
import java.util.List;

import com.ripplar_games.mesh_io.MeshVertexType;

public class ImmutableMesh<T> implements IMesh {
    private final MeshIndices<T> indices;
    private final LoadableMeshVertices vertices;

    public ImmutableMesh(MeshIndices<T> indices, LoadableMeshVertices vertices) {
        this.indices = indices;
        this.vertices = vertices;
    }

    @Override
    public boolean isValid() {
        return (indices != null) && (vertices != null);
    }

    @Override
    public ByteBuffer getVertices() {
        return vertices.toByteBuffer();
    }

    @Override
    public ByteBuffer getIndices() {
        return indices.getIndicesBuffer();
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
    public int getFaceCount() {
        return indices.getFaceCount();
    }
}
