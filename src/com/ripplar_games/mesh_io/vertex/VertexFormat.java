package com.ripplar_games.mesh_io.vertex;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VertexFormat {
    public static final VertexFormat EMPTY = new VertexFormat();

    private final Map<VertexType, VertexAlignedSubFormat> alignedSubFormats;
    private final int byteCount;

    public VertexFormat(VertexSubFormat... subFormats) {
        this(Arrays.asList(subFormats));
    }

    public VertexFormat(List<VertexSubFormat> subFormats) {
        this.alignedSubFormats = createAlignedSubFormats(subFormats);
        this.byteCount = calculateByteCount(subFormats);
    }

    private static Map<VertexType, VertexAlignedSubFormat> createAlignedSubFormats(List<VertexSubFormat> formatEntries) {
        Map<VertexType, VertexAlignedSubFormat> typeIndexes = new EnumMap<VertexType, VertexAlignedSubFormat>(VertexType.class);
        int offset = 0;
        for (int i = 0; i < formatEntries.size(); i++) {
            VertexSubFormat entry = formatEntries.get(i);
            VertexType vertexType = entry.getVertexType();
            VertexDataType dataType = entry.getDataType();
            VertexAlignedSubFormat alignedSubFormat = new VertexAlignedSubFormat(offset, dataType);
            typeIndexes.put(vertexType, alignedSubFormat);
            offset += dataType.getByteCount();
        }
        return Collections.unmodifiableMap(typeIndexes);
    }

    private static int calculateByteCount(List<VertexSubFormat> formatEntries) {
        int byteCount = 0;
        for (VertexSubFormat entry : formatEntries) {
            byteCount += entry.getDataType().getByteCount();
        }
        return byteCount;
    }

    public boolean isEmpty() {
        return (byteCount == 0);
    }

    public int getVertexTypeCount() {
        return alignedSubFormats.size();
    }

    public Set<VertexType> getVertexTypes() {
        return alignedSubFormats.keySet();
    }

    public boolean containsVertexType(VertexType vertexType) {
        return alignedSubFormats.containsKey(vertexType);
    }

    public int getVertexDatumIndex(int vertexIndex, VertexType vertexType) {
        VertexAlignedSubFormat subFormat = alignedSubFormats.get(vertexType);
        int offset = subFormat.getOffset();
        return vertexIndex * byteCount + offset;
    }

    public VertexAlignedSubFormat getAlignedSubFormat(VertexType vertexType) {
        return alignedSubFormats.get(vertexType);
    }

    public int getByteCount() {
        return byteCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VertexFormat other = (VertexFormat) obj;
        return alignedSubFormats.equals(other.alignedSubFormats);
    }

    @Override
    public int hashCode() {
        return alignedSubFormats.hashCode();
    }
}
