package com.ripplar_games.mesh_io.vertex;

import com.ripplar_games.mesh_io.mesh.BufferUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditableVertices {
    private final List<EditableVertex> vertexList = new ArrayList<EditableVertex>();
    private final Map<VertexFormat, ByteBuffer> formatVertices = new HashMap<VertexFormat, ByteBuffer>();
    private int vertexCount;

    public void clear() {
        this.vertexList.clear();
        this.vertexCount = 0;
        this.formatVertices.clear();
    }

    public ByteBuffer getVerticesBuffer(VertexFormat format) {
        return formatVertices.get(format);
    }

    public Set<VertexFormat> getFormats() {
        return formatVertices.keySet();
    }

    public void addFormat(VertexFormat format) {
        if (!this.formatVertices.containsKey(format)) {
            int byteCount = vertexCount * format.getByteCount();
            ByteBuffer vertices = BufferUtil.createByteBuffer(byteCount);
            this.formatVertices.put(format, vertices);
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
                EditableVertex vertex = vertexList.get(vertexIndex);
                for (VertexType vertexType : format.getVertexTypes()) {
                    float datum = vertex.getDatum(vertexType);
                    VertexAlignedSubFormat alignedSubFormat = format.getAlignedSubFormat(vertexType);
                    if (alignedSubFormat != null) {
                        int offset = alignedSubFormat.getOffset();
                        int index = (vertexIndex * format.getByteCount()) + offset;
                        VertexDataType dataType = alignedSubFormat.getDataType();
                        dataType.setDatum(vertices, index, datum);
                    }
                }
            }
        }
    }

    public void removeFormat(VertexFormat format) {
        formatVertices.remove(format);
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
            for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
                VertexFormat format = entry.getKey();
                ByteBuffer vertices = entry.getValue();
                int newLength = vertexCount * format.getByteCount();
                if (vertices.capacity() >= newLength) {
                    vertices.limit(newLength);
                } else {
                    vertices = BufferUtil.copy(vertices, newLength);
                    entry.setValue(vertices);
                }
            }
        }
    }

    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return vertexList.get(vertexIndex).getDatum(vertexType);
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float datum) {
        vertexList.get(vertexIndex).setDatum(vertexType, datum);
        for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            VertexFormat format = entry.getKey();
            ByteBuffer vertices = entry.getValue();
            VertexAlignedSubFormat alignedSubFormat = format.getAlignedSubFormat(vertexType);
            if (alignedSubFormat != null) {
                int offset = alignedSubFormat.getOffset();
                int index = (vertexIndex * format.getByteCount()) + offset;
                VertexDataType dataType = alignedSubFormat.getDataType();
                dataType.setDatum(vertices, index, datum);
            }
        }
    }
}
