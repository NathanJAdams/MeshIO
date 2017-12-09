package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ripplar_games.mesh_io.mesh.BufferUtil;

public class LoadableVertices {
    private final Map<VertexFormat, ByteBuffer> formatVertices = new HashMap<VertexFormat, ByteBuffer>();
    private int vertexCount;

    public LoadableVertices(Set<VertexFormat> formats) {
        for (VertexFormat format : formats) {
            formatVertices.put(format, BufferUtil.createByteBuffer(0));
        }
    }

    public void clear() {
        for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            entry.setValue(BufferUtil.createByteBuffer(0));
        }
    }

    public Map<VertexFormat, ByteBuffer> getFormatVertices() {
        return formatVertices;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
        for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            int totalByteCount = entry.getKey().getByteCount() * vertexCount;
            ByteBuffer buffer = entry.getValue();
            if (buffer.capacity() >= totalByteCount) {
                buffer.limit(totalByteCount);
            } else {
                buffer = BufferUtil.createByteBuffer(totalByteCount);
                entry.setValue(buffer);
            }
        }
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
        for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            VertexFormat format = entry.getKey();
            ByteBuffer vertices = entry.getValue();
            VertexAlignedSubFormat alignedSubFormat = format.getAlignedSubFormat(vertexType);
            if (alignedSubFormat != null) {
                int offset = alignedSubFormat.getOffset();
                VertexDataType dataType = alignedSubFormat.getDataType();
                int index = (vertexIndex * format.getByteCount()) + offset;
                dataType.setDatum(vertices, index, vertexDatum);
            }
        }
    }
}
