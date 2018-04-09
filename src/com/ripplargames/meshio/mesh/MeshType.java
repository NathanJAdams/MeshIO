package com.ripplargames.meshio.mesh;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ripplargames.meshio.index.IndicesDataType;

public enum MeshType {
    Mesh(new int[]{0, 1, 2}),
    Outline(new int[]{0, 1, 1, 2, 2, 0});
    private static final List<MeshType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private final int[] offsets;

    public static List<MeshType> valuesList() {
        return VALUES;
    }

    MeshType(int[] offsets) {
        this.offsets = offsets;
    }

    public int getOffsetsLength() {
        return offsets.length;
    }

    public <T> void setFaceIndex(IndicesDataType<T> indicesDataType, T array, int faceIndex, int faceCornerIndex, int vertexIndex) {
        int offset = faceIndex * offsets.length;
        for (int offsetIndex = 0; offsetIndex < offsets.length; offsetIndex++) {
            if (faceCornerIndex == offsets[offsetIndex]) {
                indicesDataType.setValue(array, offset + offsetIndex, vertexIndex);
            }
        }
    }

    public <T> int[] getFaceIndices(IndicesDataType<T> indicesDataType, T array, int faceIndex) {
        int[] faceIndices = new int[offsets.length];
        int offset = faceIndex * offsets.length;
        for (int i = 0; i < offsets.length; i++)
            faceIndices[i] = indicesDataType.getValue(array, offset + i);
        return faceIndices;
    }

    public <T> void setFaceIndices(IndicesDataType<T> indicesDataType, T array, int faceIndex, int[] faceIndices) {
        int offset = faceIndex * offsets.length;
        for (int offsetIndex = 0; offsetIndex < offsets.length; offsetIndex++) {
            int faceCornerIndex = offsets[offsetIndex];
            int vertexIndex = faceIndices[faceCornerIndex];
            indicesDataType.setValue(array, offset + offsetIndex, vertexIndex);
        }
    }

    public int[] getIndices(int[] meshIndices) {
        int faceCount = meshIndices.length / 3;
        int length = faceCount * offsets.length;
        int[] indices = new int[length];
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            int indicesBaseIndex = faceIndex * offsets.length;
            int meshBaseIndex = faceIndex * 3;
            for (int offsetIndex = 0; offsetIndex < offsets.length; offsetIndex++) {
                int offset = offsets[offsetIndex];
                indices[indicesBaseIndex + offsetIndex] = meshIndices[meshBaseIndex + offset];
            }
        }
        return indices;
    }
}
