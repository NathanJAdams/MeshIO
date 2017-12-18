package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ripplar_games.mesh_io.mesh.BufferUtil;

public class EditableVertices {
    private final List<EditableVertex> vertexList = new ArrayList<EditableVertex>();
    private int vertexCount;

    public void clear() {
        this.vertexList.clear();
        this.vertexCount = 0;
    }

    public ByteBuffer getVerticesBuffer(VertexFormat format) {
        int byteCount = vertexCount * format.getByteCount();
        ByteBuffer bb = BufferUtil.createByteBuffer(byteCount);
        for (int i = 0; i < vertexCount; i++) {
            EditableVertex vertex = vertexList.get(i);
            for (VertexType vertexType : format.getVertexTypes()) {
                AlignedVertexFormatPart alignedFormatPart = format.getAlignedFormatPart(vertexType);
                VertexDataType dataType = alignedFormatPart.getDataType();
                int index = format.getVertexDatumIndex(i, vertexType);
                float datum = vertex.getDatum(vertexType);
                dataType.setDatum(bb, index, datum);

            }
        }
        return bb;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        if (this.vertexCount > vertexCount) {
            for (int i = vertexCount; i < vertexList.size(); i++)
                vertexList.get(i).clear();
        } else if (this.vertexCount < vertexCount) {
            for (int i = vertexList.size(); i < vertexCount; i++)
                vertexList.add(new EditableVertex());
        }
        this.vertexCount = vertexCount;
    }

    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return vertexList.get(vertexIndex).getDatum(vertexType);
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float datum) {
        vertexList.get(vertexIndex).setDatum(vertexType, datum);
    }
}
