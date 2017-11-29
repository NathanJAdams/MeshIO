package com.ripplar_games.mesh_io;

import com.ripplar_games.mesh_io.vertex.VertexType;

public interface IMeshSaver extends IMeshInfo {
    float getVertexDatum(int vertexIndex, VertexType vertexType);

    int[] getFaceIndices(int faceIndex);
}
