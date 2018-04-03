package com.ripplargames.meshio.vertex;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.bufferformats.AlignedBufferFormatPart;
import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.util.BufferUtil;

public class LoadableVertices {
    private final Map<BufferFormat, ByteBuffer> formatVertices = new HashMap<BufferFormat, ByteBuffer>();
    private int vertexCount;

    public LoadableVertices(Set<BufferFormat> formats) {
        for (BufferFormat format : formats) {
            formatVertices.put(format, BufferUtil.createByteBuffer(0));
        }
    }

    public void clear() {
        this.vertexCount = 0;
        for (Map.Entry<BufferFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            ByteBuffer bb = entry.getValue();
            if ((bb.position() > 0) || (bb.capacity() > 0))
                entry.setValue(BufferUtil.createByteBuffer(0));
        }
    }

    public Map<BufferFormat, ByteBuffer> getFormatVertices() {
        return formatVertices;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        if (this.vertexCount != vertexCount) {
            for (Map.Entry<BufferFormat, ByteBuffer> entry : formatVertices.entrySet()) {
                BufferFormat format = entry.getKey();
                ByteBuffer buffer = entry.getValue();
                ByteBuffer resizedBuffer = resizeBuffer(format, buffer, this.vertexCount, vertexCount);
                entry.setValue(resizedBuffer);
            }
            this.vertexCount = vertexCount;
        }
    }

    private ByteBuffer resizeBuffer(BufferFormat format, ByteBuffer buffer, int previousVertexCount, int newVertexCount) {
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
                    AlignedBufferFormatPart alignedFormatPart = format.getAlignedFormatPart(vertexType);
                    setAlignedDatum(format, buffer, alignedFormatPart, i, vertexType.defaultValue());
                }
            }
        }
        return buffer;
    }

    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
        for (Map.Entry<BufferFormat, ByteBuffer> entry : formatVertices.entrySet()) {
            BufferFormat format = entry.getKey();
            ByteBuffer buffer = entry.getValue();
            AlignedBufferFormatPart alignedFormatPart = format.getAlignedFormatPart(vertexType);
            if (alignedFormatPart != null)
                setAlignedDatum(format, buffer, alignedFormatPart, vertexIndex, vertexDatum);
        }
    }

    private void setAlignedDatum(BufferFormat format, ByteBuffer buffer, AlignedBufferFormatPart alignedFormatPart, int vertexIndex, float datum) {
        int offset = alignedFormatPart.getOffset();
        VertexDataType dataType = alignedFormatPart.getDataType();
        int index = (vertexIndex * format.getByteCount()) + offset;
        dataType.setDatum(buffer, index, datum);
    }
}
