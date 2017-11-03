package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import com.ripplar_games.mesh_io.MeshVertexType;

public class LoadableMeshVertices {
    private final List<MeshVertexType> format;
    private final EnumMap<MeshVertexType, Integer> meshVertexTypeIndexes;
    private float[] vertices;

    public LoadableMeshVertices(MeshVertexType... format) {
        this(Arrays.asList(format));
    }

    public LoadableMeshVertices(List<MeshVertexType> format) {
        this.format = Collections.unmodifiableList(MeshVertexType.createUniqueList(format));
        this.meshVertexTypeIndexes = MeshVertexType.createTypeIndexes(this.format);
    }

    public void clear() {
        this.vertices = new float[0];
    }

    public ByteBuffer toByteBuffer() {
        return BufferUtil.with(vertices);
    }

    public List<MeshVertexType> getFormat() {
        return format;
    }

    public int getVertexCount() {
        return (format.isEmpty())
                ? 0
                : vertices.length / format.size();
    }

    public void setVertexCount(int vertexCount) {
        this.vertices = new float[vertexCount * format.size()];
    }

    public void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float vertexDatum) {
        int offset = vertexIndex * format.size();
        Integer vertexTypeIndexObject = meshVertexTypeIndexes.get(meshVertexType);
        if (vertexTypeIndexObject != null) {
            vertices[offset + vertexTypeIndexObject] = vertexDatum;
        }
    }
}
