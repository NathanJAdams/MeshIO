package com.ripplargames.meshio.vertices;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VertexFormat {
    private final Map<VertexType, AlignedVertexFormatPart> alignedFormatParts;
    private final int byteCount;

    public VertexFormat(VertexFormatPart... formatParts) {
        this(Arrays.asList(formatParts));
    }

    public VertexFormat(List<VertexFormatPart> formatParts) {
        this.alignedFormatParts = createAlignedFormatParts(formatParts);
        this.byteCount = calculateByteCount(formatParts);
    }

    private static Map<VertexType, AlignedVertexFormatPart> createAlignedFormatParts(List<VertexFormatPart> formatEntries) {
        Map<VertexType, AlignedVertexFormatPart> alignedFormatParts = new EnumMap<VertexType, AlignedVertexFormatPart>(VertexType.class);
        int offset = 0;
        for (VertexFormatPart entry : formatEntries) {
            VertexType vertexType = entry.getVertexType();
            VertexDataType dataType = entry.getDataType();
            AlignedVertexFormatPart alignedFormatPart = new AlignedVertexFormatPart(offset, dataType);
            alignedFormatParts.put(vertexType, alignedFormatPart);
            offset += dataType.byteCount();
        }
        return Collections.unmodifiableMap(alignedFormatParts);
    }

    private static int calculateByteCount(List<VertexFormatPart> formatParts) {
        int byteCount = 0;
        for (VertexFormatPart formatPart : formatParts) {
            byteCount += formatPart.getDataType().byteCount();
        }
        return byteCount;
    }

    public Iterable<Map.Entry<VertexType, AlignedVertexFormatPart>> alignedParts() {
        return alignedFormatParts.entrySet();
    }

    public Set<VertexType> vertexTypes() {
        return alignedFormatParts.keySet();
    }

    public int byteCount() {
        return byteCount;
    }
}
