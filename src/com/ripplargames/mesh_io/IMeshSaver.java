package com.ripplargames.mesh_io;

import com.ripplargames.mesh_io.vertex.VertexType;

public interface IMeshSaver extends IMeshInfo {
    float getVertexDatum(int vertexIndex, VertexType vertexType);

    int[] getFaceIndices(int faceIndex);
}
