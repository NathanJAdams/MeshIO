package com.ripplargames.meshio.util;

import java.util.Arrays;

public class ResizableFloatArray {
    private static final double DEFAULT_RESIZE_FACTOR = 2;

    private final double resizeFactor;
    private float[] backing = new float[16];
    private int length;

    public ResizableFloatArray() {
        this(DEFAULT_RESIZE_FACTOR);
    }

    public ResizableFloatArray(double resizeFactor) {
        this.resizeFactor = Math.max(1.25, resizeFactor);
    }

    public int length() {
        return length;
    }

    public float getAt(int index) {
        return backing[index];
    }

    public float[] copyArray() {
        return Arrays.copyOf(backing, length);
    }

    public void append(float datum) {
        ensureCapacity(length);
        backing[length] = datum;
        length++;
    }

    public void setAt(int index, float datum) {
        ensureCapacity(index);
        backing[index] = datum;
        if (length <= index) {
            length = index + 1;
        }
    }

    private void ensureCapacity(int index) {
        if (backing.length <= index) {
            int newLength = (int) (backing.length * resizeFactor);
            backing = Arrays.copyOf(backing, newLength);
        }
    }
}
