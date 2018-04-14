package com.ripplargames.meshio.vertices;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        for (BufferFormatPart entry : formatEntries) {
            VertexType vertexType = entry.getVertexType();
            VertexDataType dataType = entry.getDataType();
            AlignedBufferFormatPart alignedFormatPart = new AlignedBufferFormatPart(offset, dataType);
            alignedFormatParts.put(vertexType, alignedFormatPart);
            offset += dataType.byteCount();
        }
        return Collections.unmodifiableMap(alignedFormatParts);
    }

    private static int calculateByteCount(List<BufferFormatPart> formatParts) {
        int byteCount = 0;
        for (BufferFormatPart formatPart : formatParts) {
            byteCount += formatPart.getDataType().byteCount();
        }
        return byteCount;
    }

    public Iterable<Map.Entry<VertexType, AlignedBufferFormatPart>> alignedParts() {
        return alignedFormatParts.entrySet();
    }

    public Set<VertexType> vertexTypes() {
        return alignedFormatParts.keySet();
    }

    public int byteCount() {
        return byteCount;
    }
}
