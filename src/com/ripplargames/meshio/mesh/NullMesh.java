package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Set;

import com.ripplargames.meshio.IMesh;
import com.ripplargames.meshio.IMeshBuilder;
import com.ripplargames.meshio.MeshRawData;
import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.util.BufferUtil;

public class NullMesh implements IMesh, IMeshBuilder<NullMesh> {
    private static final int[] INDICES = new int[0];
    private static final float[] VERTICES = new float[0];
    private static final ByteBuffer INDICES_BUFFER = BufferUtil.with(INDICES);
    private static final ByteBuffer VERTICES_BUFFER = BufferUtil.with(VERTICES);
    private static final MeshRawData RAW_DATA = new MeshRawData();

    @Override
    public Set<BufferFormat> getBufferFormats() {
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
    public ByteBuffer getVertices(BufferFormat format) {
        return VERTICES_BUFFER;
    }

    @Override
    public ByteBuffer getIndices() {
        return INDICES_BUFFER;
    }

    @Override
    public MeshRawData toRawData() {
        return RAW_DATA;
    }

    @Override
    public NullMesh build(MeshRawData meshRawData) {
        return this;
    }
}
