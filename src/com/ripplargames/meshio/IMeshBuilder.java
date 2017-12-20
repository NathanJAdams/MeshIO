package com.ripplargames.meshio;

import com.ripplargames.meshio.mesh.IMesh;
import com.ripplargames.meshio.vertex.VertexType;

public interface IMeshBuilder<T extends IMesh> extends IMeshInfo {
    void clear();

    void setVertexCount(int vertexCount);

    void setFaceCount(int faceCount);

    void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum);

    void setFaceIndices(int faceIndex, int[] faceIndices);

    T build();
}
