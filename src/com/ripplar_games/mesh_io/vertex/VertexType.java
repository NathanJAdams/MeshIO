package com.ripplar_games.mesh_io.vertex;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum VertexType {
    Position_X(true),
    Position_Y(true),
    Position_Z(true),
    Normal_X(true),
    Normal_Y(true),
    Normal_Z(true),
    Color_R(false),
    Color_G(false),
    Color_B(false),
    Color_A(false),
    ImageCoord_X(false),
    ImageCoord_Y(false);
    private static final List<VertexType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

    private final boolean isSignedData;

    public static List<VertexType> valuesList() {
        return VALUES;
    }

    VertexType(boolean isSignedData) {
        this.isSignedData = isSignedData;
    }

    public boolean isSignedData() {
        return isSignedData;
    }
}
