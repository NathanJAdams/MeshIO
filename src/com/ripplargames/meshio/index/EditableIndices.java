package com.ripplargames.meshio.index;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import com.ripplargames.meshio.mesh.MeshType;

public class EditableIndices<T> {
    private final IndicesDataType<T> indicesDataType;
    private final MeshType meshType;
    private T indices;

    public EditableIndices(IndicesDataType<T> indicesDataType, MeshType meshType) {
        this.indicesDataType = indicesDataType;
        this.meshType = meshType;
        this.indices = indicesDataType.createEmptyArray();
    }

    public void clear() {
        this.indices = indicesDataType.createEmptyArray();
    }

    public ByteBuffer getIndicesBuffer() {
        return indicesDataType.toByteBuffer(indices);
    }

    public boolean isValidVertexCount(int vertexCount) {
        return indicesDataType.isValidVertexCount(vertexCount);
    }

    public int getFaceCount() {
        return Array.getLength(indices) / meshType.getOffsetsLength();
    }

    public void setFaceCount(int faceCount) {
        this.indices = indicesDataType.createNewArray(indices, faceCount * meshType.getOffsetsLength());
    }

    public void setFaceIndex(int faceIndex, int faceCornerIndex, int vertexIndex) {
        meshType.setFaceIndex(indicesDataType, indices, faceIndex, faceCornerIndex, vertexIndex);
    }

    public int[] getFaceIndices(int faceIndex) {
        return meshType.getFaceIndices(indicesDataType, indices, faceIndex);
    }

    public void setFaceIndices(int faceIndex, int[] faceIndices) {
        meshType.setFaceIndices(indicesDataType, indices, faceIndex, faceIndices);
    }
}
