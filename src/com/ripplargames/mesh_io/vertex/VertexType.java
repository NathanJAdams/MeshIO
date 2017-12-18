package com.ripplargames.mesh_io.vertex;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum VertexType {
    Position_X(true, 0),
    Position_Y(true, 0),
    Position_Z(true, 0),
    Normal_X(true, 0),
    Normal_Y(true, 1),
    Normal_Z(true, 0),
    Color_R(false, 1),
    Color_G(false, 1),
    Color_B(false, 1),
    ImageCoord_X(false, 0),
    ImageCoord_Y(false, 0);
    private static final List<VertexType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));

    private final boolean isSignedData;
    private final float defaultValue;

    public static List<VertexType> valuesList() {
        return VALUES;
    }

    VertexType(boolean isSignedData, float defaultValue) {
        this.isSignedData = isSignedData;
        this.defaultValue = defaultValue;
    }

    public boolean isSignedData() {
        return isSignedData;
    }

    public float defaultValue() {
        return defaultValue;
    }
}
