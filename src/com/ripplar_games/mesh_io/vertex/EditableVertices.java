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
        int formatByteCount = format.getByteCount();
        int byteCount = vertexCount * formatByteCount;
        ByteBuffer bb = BufferUtil.createByteBuffer(byteCount);
        for (int i = 0; i < vertexCount; i++) {
            EditableVertex vertex = vertexList.get(i);
            int vertexBaseIndex = i * formatByteCount;
            for (VertexType vertexType : format.getVertexTypes()) {
                float datum = vertex.getDatum(vertexType);
                VertexAlignedSubFormat subFormat = format.getAlignedSubFormat(vertexType);
                int offset = subFormat.getOffset();
                int index = vertexBaseIndex + offset;
                VertexDataType dataType = subFormat.getDataType();
                System.out.println("VertexType: " + vertexType + ", DataType: " + dataType + ", Index: " + index + ", Datum: " + datum);
                dataType.setDatum(bb, index, datum);
            }
        }
        bb.position(0); // TODO is this needed?
        return bb;
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
    }

    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return vertexList.get(vertexIndex).getDatum(vertexType);
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float datum) {
        vertexList.get(vertexIndex).setDatum(vertexType, datum);
    }
}
