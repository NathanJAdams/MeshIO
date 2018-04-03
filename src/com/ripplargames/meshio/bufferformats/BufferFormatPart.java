package com.ripplargames.meshio.bufferformats;

import com.ripplargames.meshio.vertex.VertexDataType;
import com.ripplargames.meshio.vertex.VertexType;

public class BufferFormatPart {
    private final VertexType vertexType;
    private final VertexDataType dataType;

    public BufferFormatPart(VertexType vertexType, VertexDataType dataType) {
        this.vertexType = vertexType;
        this.dataType = dataType;
    }

    public VertexType getVertexType() {
        return vertexType;
    }

    public VertexDataType getDataType() {
        return dataType;
    }
}
