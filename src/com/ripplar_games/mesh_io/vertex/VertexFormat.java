package com.ripplar_games.mesh_io.vertex;

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
        for (int i = 0; i < formatEntries.size(); i++) {
            VertexFormatPart entry = formatEntries.get(i);
            VertexType vertexType = entry.getVertexType();
            VertexDataType dataType = entry.getDataType();
            AlignedVertexFormatPart alignedFormatPart = new AlignedVertexFormatPart(offset, dataType);
            alignedFormatParts.put(vertexType, alignedFormatPart);
            offset += dataType.getByteCount();
        }
        return Collections.unmodifiableMap(alignedFormatParts);
    }

    private static int calculateByteCount(List<VertexFormatPart> formatParts) {
        int byteCount = 0;
        for (VertexFormatPart formatPart : formatParts) {
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
        AlignedVertexFormatPart alignedFormatPart = alignedFormatParts.get(vertexType);
        int offset = alignedFormatPart.getOffset();
        return vertexIndex * byteCount + offset;
    }

    public AlignedVertexFormatPart getAlignedFormatPart(VertexType vertexType) {
        return alignedFormatParts.get(vertexType);
    }

    public int getByteCount() {
        return byteCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VertexFormat other = (VertexFormat) obj;
        return alignedFormatParts.equals(other.alignedFormatParts);
    }

    @Override
    public int hashCode() {
        return alignedFormatParts.hashCode();
    }
}
