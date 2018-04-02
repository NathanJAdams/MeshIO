package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;

import com.ripplargames.meshio.IMesh;
import com.ripplargames.meshio.IMeshBuilder;
import com.ripplargames.meshio.IMeshSaver;
import com.ripplargames.meshio.util.BufferUtil;
import com.ripplargames.meshio.vertex.VertexFormat;
import com.ripplargames.meshio.vertex.VertexType;

public class NullMesh implements IMesh, IMeshBuilder<NullMesh>, IMeshSaver {
    private static final int[] INDICES = new int[0];
    private static final float[] VERTICES = new float[0];
    private static final ByteBuffer INDICES_BUFFER = BufferUtil.with(INDICES);
    private static final ByteBuffer VERTICES_BUFFER = BufferUtil.with(VERTICES);

    @Override
    public Set<VertexFormat> getVertexFormats() {
        return Collections.emptySet();
    }

    @Override
    public int getVertexCount() {
        return 0;
    }

    @Override
    public int getFaceCount() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public ByteBuffer getVertices(VertexFormat format) {
        return VERTICES_BUFFER;
    }

    @Override
    public ByteBuffer getIndices() {
        return INDICES_BUFFER;
    }

    @Override
    public void clear() {
    }

    @Override
    public void setVertexCount(int vertexCount) {
    }

    @Override
    public void setFaceCount(int faceCount) {
    }

    @Override
    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
    }

    @Override
    public void setFaceIndices(int faceIndex, int[] faceIndices) {
    }

    @Override
    public NullMesh build() {
        return this;
    }

    @Override
    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return 0;
    }

    @Override
    public int[] getFaceIndices(int faceIndex) {
        return INDICES;
    }
}
