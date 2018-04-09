package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.IMesh;
import com.ripplargames.meshio.MeshRawData;
import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.index.EditableIndices;
import com.ripplargames.meshio.index.IndicesDataType;
import com.ripplargames.meshio.index.IndicesDataTypes;
import com.ripplargames.meshio.vertex.EditableVertices;
import com.ripplargames.meshio.vertex.VertexType;

public class EditableMesh implements IMesh {
    private final Map<MeshType, Map<IndicesDataType<?>, EditableIndices<?>>> indices = new HashMap<MeshType, Map<IndicesDataType<?>, EditableIndices<?>>>();
    private final EditableVertices vertices = new EditableVertices();
    private final Set<BufferFormat> formats = new HashSet<BufferFormat>();
    private MeshType meshType = MeshType.Mesh;
    private IndicesDataType<?> indicesDataType = IndicesDataTypes.Short;

    public <T> EditableMesh() {
        for (MeshType meshType : MeshType.values()) {
            Map<IndicesDataType<?>, EditableIndices<?>> subMap = new HashMap<IndicesDataType<?>, EditableIndices<?>>();
            for (IndicesDataType<?> dataType : IndicesDataTypes.valuesList()) {
                // hack to allow proper typing
                @SuppressWarnings("unchecked")
                IndicesDataType<T> typedDataType = (IndicesDataType<T>) dataType;
                subMap.put(dataType, new EditableIndices<T>(typedDataType, meshType));
            }
            indices.put(meshType, subMap);
        }
    }

    public void clear() {
        for (Map<IndicesDataType<?>, EditableIndices<?>> subMap : indices.values())
            for (EditableIndices<?> editableIndices : subMap.values())
                editableIndices.clear();
        vertices.clear();
        meshType = MeshType.Mesh;
        indicesDataType = IndicesDataTypes.Short;
    }

    @Override
    public boolean isValid() {
        return (getFaceCount() > 0) && indicesDataType.isValidVertexCount(getVertexCount());
    }

    @Override
    public Set<BufferFormat> getBufferFormats() {
        return formats;
    }

    @Override
    public int getVertexCount() {
        return vertices.getVertexCount();
    }

    public void setVertexCount(int vertexCount) {
        vertices.setVertexCount(vertexCount);
    }

    @Override
    public int getFaceCount() {
        return getMeshIndices().getFaceCount();
    }

    public void setFaceCount(int faceCount) {
        for (Map<IndicesDataType<?>, EditableIndices<?>> subMap : indices.values())
            for (EditableIndices<?> indices : subMap.values())
                indices.setFaceCount(faceCount);
    }

    @Override
    public ByteBuffer getVertices(BufferFormat format) {
        return vertices.getVerticesBuffer(format);
    }

    public void addBufferFormat(BufferFormat format) {
        formats.add(format);
    }

    public void removeBufferFormat(BufferFormat format) {
        formats.remove(format);
    }

    @Override
    public ByteBuffer getIndices() {
        return getMeshIndices().getIndicesBuffer();
    }

    @Override
    public MeshRawData toRawData() {
        MeshRawData meshRawData = new MeshRawData();
        EditableIndices<?> editableIndices = indices.get(MeshType.Mesh).get(IndicesDataTypes.Int);
        int faceCount = editableIndices.getFaceCount();
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            int[] faceIndices = editableIndices.getFaceIndices(faceIndex);
            meshRawData.setFace(faceIndex, faceIndices[0], faceIndices[1], faceIndices[2]);
        }
        int vertexCount = vertices.getVertexCount();
        Set<VertexType> vertexTypes = new HashSet<VertexType>();
        for (BufferFormat format : formats) {
            vertexTypes.addAll(format.getVertexTypes());
        }
        for (VertexType vertexType : vertexTypes) {
            for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
                float datum = vertices.getVertexDatum(vertexIndex, vertexType);
                meshRawData.setVertexTypeDatum(vertexType, vertexIndex, datum);
            }
        }
        return meshRawData;
    }

    public void setFaceIndicesIndex(int faceIndex, int indicesIndex, int vertexIndex) {
        for (Map<IndicesDataType<?>, EditableIndices<?>> subMap : indices.values())
            for (EditableIndices<?> indices : subMap.values())
                indices.setFaceIndex(faceIndex, indicesIndex, vertexIndex);
    }

    public void setIndicesDataType(IndicesDataType<?> indicesDataType) {
        if (indicesDataType != null)
            this.indicesDataType = indicesDataType;
    }

    public void setMeshType(MeshType meshType) {
        if (meshType != null)
            this.meshType = meshType;
    }


    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
        vertices.setVertexDatum(vertexIndex, vertexType, vertexDatum);
    }

    public void setFaceIndices(int faceIndex, int[] faceIndices) {
        for (Map<IndicesDataType<?>, EditableIndices<?>> subMap : indices.values())
            for (EditableIndices<?> indices : subMap.values())
                indices.setFaceIndices(faceIndex, faceIndices);
    }

    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return vertices.getVertexDatum(vertexIndex, vertexType);
    }

    public int[] getFaceIndices(int faceIndex) {
        return indices.get(MeshType.Mesh).get(IndicesDataTypes.Int).getFaceIndices(faceIndex);
    }

    private EditableIndices<?> getMeshIndices() {
        return indices.get(meshType).get(indicesDataType);
    }
}
