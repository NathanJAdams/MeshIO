package com.ripplargames.meshio;

import java.util.Arrays;

public class Face {
    private final int v0;
    private final int v1;
    private final int v2;
    private final int hash;

    public Face(int v0, int v1, int v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.hash = Arrays.hashCode(new int[]{v0, v1, v2});
    }

    public int getV0() {
        return v0;
    }

    public int getV1() {
        return v1;
    }

    public int getV2() {
        return v2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Face face = (Face) o;
        return (v0 == face.v0)
                && (v1 == face.v1)
                && (v2 == face.v2);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
