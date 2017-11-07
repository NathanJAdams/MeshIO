package com.ripplar_games.mesh_io.vertex;

import java.util.EnumMap;
import java.util.List;

public enum VertexType {
    Position_X,
    Position_Y,
    Position_Z,
    Normal_X,
    Normal_Y,
    Normal_Z,
    Color_R,
    Color_G,
    Color_B,
    Color_A,
    ImageCoord_X,
    ImageCoord_Y;
    private static final VertexType[] VALUES = values();

    public static VertexType[] getValues() {
        return VALUES;
    }

    public static EnumMap<VertexType, Integer> createTypeIndexes(List<VertexType> format) {
        EnumMap<VertexType, Integer> typeIndexes = new EnumMap<VertexType, Integer>(VertexType.class);
        if (format != null)
            for (int i = 0; i < format.size(); i++)
                typeIndexes.put(format.get(i), i);
        return typeIndexes;
    }
}
