package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.IMesh;
import com.ripplargames.meshio.MeshRawData;
import com.ripplargames.meshio.bufferformats.BufferFormat;

public class ImmutableMesh implements IMesh {
    private final int vertexCount;
    private final int faceCount;
    private final Map<BufferFormat, ByteBuffer> formatVertices;
    private final ByteBuffer indices;
    private final boolean isValid;

    public ImmutableMesh(int vertexCount, int faceCount, Map<BufferFormat, ByteBuffer> formatVertices, ByteBuffer indices) {
        this.vertexCount = vertexCount;
        this.faceCount = faceCount;
        this.formatVertices = formatVertices;
        this.indices = indices;
        this.isValid = (!formatVertices.isEmpty()) && (vertexCount >= 3) && (faceCount > 0) && (indices != null);
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public ByteBuffer getVertices(BufferFormat format) {
        return formatVertices.get(format);
    }

    @Override
    public ByteBuffer getIndices() {
        return indices;
    }

    @Override
    public Set<BufferFormat> getBufferFormats() {
        return formatVertices.keySet();
    }

    @Override
    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public int getFaceCount() {
        return faceCount;
    }

    @Override
    public MeshRawData toRawData() {
        MeshRawData meshRawData = new MeshRawData();
        // TODO
//        EditableIndices<?> editableIndices = MeshType.Mesh.getIndices(indices);
//        int faceCount = editableIndices.getFaceCount();
//        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
//            int[] faceIndices = editableIndices.getFaceIndices(faceIndex);
//            meshRawData.setFace(faceIndex, faceIndices[0], faceIndices[1], faceIndices[2]);
//        }
//        int vertexCount = vertices.getVertexCount();
//        Set<VertexType> vertexTypes = new HashSet<VertexType>();
//        for (BufferFormat format : formats) {
//            vertexTypes.addAll(format.getVertexTypes());
//        }
//        for (VertexType vertexType : vertexTypes) {
//            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
//                float datum = vertices.getVertexDatum(vertexIndex, vertexType);
//                meshRawData.setVertexTypeDatum(vertexType, vertexIndex, datum);
//            }
//        }
        return meshRawData;
    }
}
