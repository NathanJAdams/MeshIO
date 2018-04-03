package com.ripplargames.meshio.bufferformats;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.vertex.VertexDataType;
import com.ripplargames.meshio.vertex.VertexType;

public class BufferFormat {
    private final Map<VertexType, AlignedBufferFormatPart> alignedFormatParts;
    private final int byteCount;

    public BufferFormat(BufferFormatPart... formatParts) {
        this(Arrays.asList(formatParts));
    }

    public BufferFormat(List<BufferFormatPart> formatParts) {
        this.alignedFormatParts = createAlignedFormatParts(formatParts);
        this.byteCount = calculateByteCount(formatParts);
    }

    private static Map<VertexType, AlignedBufferFormatPart> createAlignedFormatParts(List<BufferFormatPart> formatEntries) {
        Map<VertexType, AlignedBufferFormatPart> alignedFormatParts = new EnumMap<VertexType, AlignedBufferFormatPart>(VertexType.class);
        int offset = 0;
        for (int i = 0; i < formatEntries.size(); i++) {
            BufferFormatPart entry = formatEntries.get(i);
            VertexType vertexType = entry.getVertexType();
            VertexDataType dataType = entry.getDataType();
            AlignedBufferFormatPart alignedFormatPart = new AlignedBufferFormatPart(offset, dataType);
            alignedFormatParts.put(vertexType, alignedFormatPart);
            offset += dataType.getByteCount();
        }
        return Collections.unmodifiableMap(alignedFormatParts);
    }

    private static int calculateByteCount(List<BufferFormatPart> formatParts) {
        int byteCount = 0;
        for (BufferFormatPart formatPart : formatParts) {
            byteCount += formatPart.getDataType().getByteCount();
        }
        return byteCount;
    }

    public boolean isEmpty() {
        return (byteCount == 0);
    }

    public int getVertexTypeCount() {
        return alignedFormatParts.size();
    }

    public Set<VertexType> getVertexTypes() {
        return alignedFormatParts.keySet();
    }

    public boolean containsVertexType(VertexType vertexType) {
        return alignedFormatParts.containsKey(vertexType);
    }

    public int getVertexDatumIndex(int vertexIndex, VertexType vertexType) {
        AlignedBufferFormatPart alignedFormatPart = alignedFormatParts.get(vertexType);
        int offset = alignedFormatPart.getOffset();
        return vertexIndex * byteCount + offset;
    }

    public AlignedBufferFormatPart getAlignedFormatPart(VertexType vertexType) {
        return alignedFormatParts.get(vertexType);
    }

    public int getByteCount() {
        return byteCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BufferFormat other = (BufferFormat) obj;
        return alignedFormatParts.equals(other.alignedFormatParts);
    }

    @Override
    public int hashCode() {
        return alignedFormatParts.hashCode();
    }
}
