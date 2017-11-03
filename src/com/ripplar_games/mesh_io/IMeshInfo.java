package com.ripplar_games.mesh_io;

import java.util.List;

public interface IMeshInfo {
    List<MeshVertexType> getVertexFormat();

    int getVertexCount();

    int getFaceCount();
}
