package com.ripplar_games.mesh_io;

import com.ripplar_games.mesh_io.mesh.IMesh;

public interface IMeshBuilder<T extends IMesh> extends IMeshInfo {
    void clear();

    void setVertexCount(int vertexCount);

    void setFaceCount(int faceCount);

    void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float vertexDatum);

    void setFaceIndices(int faceIndex, int[] faceIndices);

    T build();
}
