package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ripplar_games.mesh_io.mesh.BufferUtil;

public class EditableVertices {
    private final List<EditableVertex> vertexList = new ArrayList<EditableVertex>();
    private VertexFormat format = VertexFormat.EMPTY;
    private int vertexCount;
    private ByteBuffer vertices;

    public void clear() {
        this.vertexList.clear();
        this.format = VertexFormat.EMPTY;
        this.vertexCount = 0;
        this.vertices = BufferUtil.createByteBuffer(0);
    }

    public ByteBuffer getVerticesBuffer() {
        return vertices;
    }

    public VertexFormat getFormat() {
        return format;
    }

    public void setFormat(VertexFormat format) {
        if (!this.format.equals(format)) {
            this.format = format;
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
                EditableVertex vertex = vertexList.get(vertexIndex);
                for (VertexType vertexType : format.getVertexTypes()) {
                    float datum = vertex.getDatum(vertexType);
                    setDatumOnBuffer(vertexIndex, vertexType, datum);
                }
            }
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
        }
        if (previousVertexCount != vertexCount) {
            int newLength = vertexCount * format.getByteCount();
            if (vertices.capacity() >= newLength) {
                vertices.limit(newLength);
            } else {
                vertices = BufferUtil.copy(vertices, newLength);
            }
        }
    }

    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return vertexList.get(vertexIndex).getDatum(vertexType);
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float datum) {
        vertexList.get(vertexIndex).setDatum(vertexType, datum);
        setDatumOnBuffer(vertexIndex, vertexType, datum);
    }

    private void setDatumOnBuffer(int vertexIndex, VertexType vertexType, float datum) {
        VertexAlignedSubFormat alignedSubFormat = format.getAlignedSubFormat(vertexType);
        if (alignedSubFormat != null) {
            int offset = alignedSubFormat.getOffset();
            int index = (vertexIndex * format.getByteCount()) + offset;
            VertexDatumDataType dataType = alignedSubFormat.getDataType();
            dataType.appendDatum(vertices, index, datum);
        }
    }
}
