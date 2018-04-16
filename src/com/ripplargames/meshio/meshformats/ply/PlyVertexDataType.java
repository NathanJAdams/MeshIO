package com.ripplargames.meshio.meshformats.ply;

import com.ripplargames.meshio.vertices.VertexType;

public class PlyVertexDataType {
    private final VertexType vertexType;
    private final PlyDataType plyDataType;

    public PlyVertexDataType(VertexType vertexType, PlyDataType plyDataType) {
        this.vertexType = vertexType;
        this.plyDataType = plyDataType;
    }

    public VertexType vertexType() {
        return vertexType;
    }

    public PlyDataType plyDataType() {
        return plyDataType;
    }
}
