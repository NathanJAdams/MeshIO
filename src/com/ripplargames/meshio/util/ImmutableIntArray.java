package com.ripplargames.meshio.util;

import java.util.Arrays;

public class ImmutableIntArray {
    private final int[] values;
    private final int hash;

    public ImmutableIntArray(int... values) {
        this.values = values;
        this.hash = Arrays.hashCode(values);
    }

    public int valueAt(int index) {
        return values[index];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableIntArray that = (ImmutableIntArray) o;
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
