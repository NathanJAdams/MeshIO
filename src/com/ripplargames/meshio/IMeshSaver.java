package com.ripplargames.meshio;

import com.ripplargames.meshio.vertex.VertexType;

public interface IMeshSaver extends IMeshInfo {
    float getVertexDatum(int vertexIndex, VertexType vertexType);

    int[] getFaceIndices(int faceIndex);
}
