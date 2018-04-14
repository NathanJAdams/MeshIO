package com.ripplargames.meshio.util;

import java.util.Arrays;

public class ResizableIntArray {
    private static final double DEFAULT_RESIZE_FACTOR = 2;

    private final double resizeFactor;
    private int[] backing = new int[16];
    private int length;

    public ResizableIntArray() {
        this(DEFAULT_RESIZE_FACTOR);
    }

    public ResizableIntArray(double resizeFactor) {
        this.resizeFactor = Math.max(1.25, resizeFactor);
    }

    public int length() {
        return length;
    }

    public int getAt(int index) {
        return backing[index];
    }
    public int max() {
        int max = backing[0];
        for (int i = 1; i < backing.length; i++) {
            int datum = backing[i];
            if (datum > max) {
                max = datum;
            }
        }
        return max;
    }

    public int[] copyArray() {
        return Arrays.copyOf(backing, length);
    }

    public void append(int datum) {
        ensureCapacity(length);
        backing[length] = datum;
        length++;
    }

    public void setAt(int index, int datum) {
        ensureCapacity(index);
        backing[index] = datum;
        if (length == index) {
            length++;
        }
    }

    private void ensureCapacity(int index) {
        if (backing.length <= index) {
            int newLength = (int) (backing.length * resizeFactor);
            backing = Arrays.copyOf(backing, newLength);
        }
    }
}
