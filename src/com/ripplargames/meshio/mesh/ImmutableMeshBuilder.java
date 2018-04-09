package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.IMeshBuilder;
import com.ripplargames.meshio.MeshRawData;
import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.index.IndicesDataType;

public class ImmutableMeshBuilder<T> implements IMeshBuilder<ImmutableMesh> {
    private final MeshType meshType;
    private final IndicesDataType<T> indicesDataType;
    private final Set<BufferFormat> formats;

    public ImmutableMeshBuilder(MeshType meshType, IndicesDataType<T> indicesDataType, Set<BufferFormat> formats) {
        this.meshType = meshType;
        this.indicesDataType = indicesDataType;
        this.formats = formats;
    }

    @Override
    public ImmutableMesh build(MeshRawData meshRawData) {
        int vertexCount = meshRawData.getVertexCount();
        int faceCount = meshRawData.getFaceCount();
        Map<BufferFormat, ByteBuffer> formatVertices = new HashMap<BufferFormat, ByteBuffer>();
        for (BufferFormat format : formats) {
            ByteBuffer buffer = format.createBuffer(meshRawData);
            formatVertices.put(format, buffer);
        }
        int[] meshIndices = meshRawData.getIndices();
        int[] indices = meshType.getIndices(meshIndices);
        ByteBuffer indicesBuffer = indicesDataType.indicesToByteBuffer(indices);
        return new ImmutableMesh(vertexCount, faceCount, formatVertices, indicesBuffer);
    }
}
