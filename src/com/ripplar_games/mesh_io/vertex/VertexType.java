package com.ripplar_games.mesh_io.vertex;

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
    private static final VertexType[] VALUES = values();

    private final boolean isSignedData;

    public static VertexType[] getValues() {
        return VALUES;
    }

    VertexType(boolean isSignedData) {
        this.isSignedData = isSignedData;
    }

    public boolean isSignedData() {
        return isSignedData;
    }
}
