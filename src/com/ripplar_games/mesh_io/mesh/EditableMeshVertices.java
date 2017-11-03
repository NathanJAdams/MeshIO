package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import com.ripplar_games.mesh_io.MeshVertexType;

public class EditableMeshVertices {
    private final List<EditableVertex> vertexList = new ArrayList<EditableVertex>();
    private final EnumMap<MeshVertexType, Integer> meshVertexTypeIndexes = new EnumMap<MeshVertexType, Integer>(MeshVertexType.class);
    private final List<MeshVertexType> format = new ArrayList<MeshVertexType>();
    private float[] vertices = new float[0];
    private int vertexCount;

    public void clear() {
        this.vertexList.clear();
        this.meshVertexTypeIndexes.clear();
        this.format.clear();
        this.vertices = new float[0];
        this.vertexCount = 0;
    }

    public ByteBuffer toByteBuffer() {
        return BufferUtil.with(vertices, 0, vertexCount * format.size());
    }

    public List<MeshVertexType> getFormat() {
        return format;
    }

    public void setFormat(MeshVertexType... format) {
        setFormat(Arrays.asList(format));
    }

    public void setFormat(List<MeshVertexType> format) {
        if (!this.format.equals(format)) {
            this.format.clear();
            this.format.addAll(format);
            this.meshVertexTypeIndexes.clear();
            this.meshVertexTypeIndexes.putAll(MeshVertexType.createTypeIndexes(this.format));
            updateVertices();
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        int previousVertexCount = this.vertexCount;
        this.vertexCount = vertexCount;
        if (previousVertexCount > vertexCount) {
            for (int i = vertexCount; i < vertexList.size(); i++)
                vertexList.get(i).clear();
        } else if (previousVertexCount < vertexCount) {
            for (int i = vertexList.size(); i < vertexCount; i++)
                vertexList.add(new EditableVertex());
            int newLength = vertexCount * format.size();
            vertices = Arrays.copyOf(vertices, newLength);
        }
    }

    public float getVertexDatum(int vertexIndex, MeshVertexType meshVertexType) {
        return vertexList.get(vertexIndex).getDatum(meshVertexType);
    }

    public void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float datum) {
        vertexList.get(vertexIndex).setDatum(meshVertexType, datum);
        int offsetIndex = vertexIndex * format.size();
        Integer vertexTypeIndexObject = meshVertexTypeIndexes.get(meshVertexType);
        if (vertexTypeIndexObject != null) {
            vertices[offsetIndex + vertexTypeIndexObject] = datum;
        }
    }

    public void getVertexData(int vertexIndex, float[] vertexData) {
        EditableVertex vertex = vertexList.get(vertexIndex);
        for (int i = 0; i < format.size() && i < vertexData.length; i++)
            vertexData[i] = vertex.getDatum(format.get(i));
    }

    public void setVertexData(int vertexIndex, float[] vertexData) {
        EditableVertex vertex = vertexList.get(vertexIndex);
        for (int i = 0; i < format.size() && i < vertexData.length; i++)
            vertex.setDatum(format.get(i), vertexData[i]);
    }

    private void updateVertices() {
        this.vertices = new float[vertexCount * format.size()];
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            int offsetIndex = vertexIndex * format.size();
            EditableVertex vertex = vertexList.get(vertexIndex);
            for (int formatIndex = 0; formatIndex < format.size(); formatIndex++)
                vertices[offsetIndex + formatIndex] = vertex.getDatum(format.get(formatIndex));
        }
    }
}
