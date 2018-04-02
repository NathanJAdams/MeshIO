package com.ripplargames.meshio.vertex;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.util.BufferUtil;

public class LoadableVertices {
    private final Map<VertexFormat, ByteBuffer> formatVertices = new HashMap<VertexFormat, ByteBuffer>();
    private int vertexCount;

    public LoadableVertices(Set<VertexFormat> formats) {
        for (VertexFormat format : formats) {
            formatVertices.put(format, BufferUtil.createByteBuffer(0));
        }
    }

    public void clear() {
        this.vertexCount = 0;
        for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            ByteBuffer bb = entry.getValue();
            if ((bb.position() > 0) || (bb.capacity() > 0))
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
        if (this.vertexCount != vertexCount) {
            for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
                VertexFormat format = entry.getKey();
                ByteBuffer buffer = entry.getValue();
                ByteBuffer resizedBuffer = resizeBuffer(format, buffer, this.vertexCount, vertexCount);
                entry.setValue(resizedBuffer);
            }
            this.vertexCount = vertexCount;
        }
    }

    private ByteBuffer resizeBuffer(VertexFormat format, ByteBuffer buffer, int previousVertexCount, int newVertexCount) {
        int formatByteCount = format.getByteCount();
        int newTotalByteCount = formatByteCount * newVertexCount;
        if (buffer.capacity() >= newTotalByteCount) {
            buffer.limit(newTotalByteCount);
        } else {
            ByteBuffer newBuffer = BufferUtil.createByteBuffer(newTotalByteCount);
            newBuffer.put(buffer);
            newBuffer.position(0);
            buffer = newBuffer;
            for (int i = previousVertexCount; i < newVertexCount; i++) {
                for (VertexType vertexType : format.getVertexTypes()) {
                    AlignedVertexFormatPart alignedFormatPart = format.getAlignedFormatPart(vertexType);
                    setAlignedDatum(format, buffer, alignedFormatPart, i, vertexType.defaultValue());
                }
            }
        }
        return buffer;
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
        for (Map.Entry<VertexFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            VertexFormat format = entry.getKey();
            ByteBuffer buffer = entry.getValue();
            AlignedVertexFormatPart alignedFormatPart = format.getAlignedFormatPart(vertexType);
            if (alignedFormatPart != null)
                setAlignedDatum(format, buffer, alignedFormatPart, vertexIndex, vertexDatum);
        }
    }

    private void setAlignedDatum(VertexFormat format, ByteBuffer buffer, AlignedVertexFormatPart alignedFormatPart, int vertexIndex, float datum) {
        int offset = alignedFormatPart.getOffset();
        VertexDataType dataType = alignedFormatPart.getDataType();
        int index = (vertexIndex * format.getByteCount()) + offset;
        dataType.setDatum(buffer, index, datum);
    }
}
