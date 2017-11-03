package com.ripplar_games.mesh_io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public enum MeshVertexType {
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
    private static final MeshVertexType[] VALUES = values();

    public static MeshVertexType[] getValues() {
        return VALUES;
    }

    public static List<MeshVertexType> createUniqueList(MeshVertexType... format) {
        return createUniqueList(Arrays.asList(format));
    }

    public static List<MeshVertexType> createUniqueList(List<MeshVertexType> format) {
        List<MeshVertexType> uniqueList = new ArrayList<MeshVertexType>();
        for (MeshVertexType meshVertexType : format)
            if (!uniqueList.contains(meshVertexType))
                uniqueList.add(meshVertexType);
        return uniqueList;
    }

    public static EnumMap<MeshVertexType, Integer> createTypeIndexes(MeshVertexType... format) {
        return createTypeIndexes(Arrays.asList(format));
    }

    public static EnumMap<MeshVertexType, Integer> createTypeIndexes(List<MeshVertexType> format) {
        EnumMap<MeshVertexType, Integer> typeIndexes = new EnumMap<MeshVertexType, Integer>(MeshVertexType.class);
        if (format != null)
            for (int i = 0; i < format.size(); i++)
                typeIndexes.put(format.get(i), i);
        return typeIndexes;
    }
}
