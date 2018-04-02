package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.IMesh;
import com.ripplargames.meshio.IMeshBuilder;
import com.ripplargames.meshio.IMeshSaver;
import com.ripplargames.meshio.index.EditableIndices;
import com.ripplargames.meshio.index.IndicesDataType;
import com.ripplargames.meshio.index.IndicesDataTypes;
import com.ripplargames.meshio.vertex.EditableVertices;
import com.ripplargames.meshio.vertex.VertexFormat;
import com.ripplargames.meshio.vertex.VertexType;

public class EditableMesh implements IMesh, IMeshBuilder<EditableMesh>, IMeshSaver {
    private final Map<MeshType, Map<IndicesDataType<?>, EditableIndices<?>>> indices = new HashMap<MeshType, Map<IndicesDataType<?>, EditableIndices<?>>>();
    private final EditableVertices vertices = new EditableVertices();
    private final Set<VertexFormat> formats = new HashSet<VertexFormat>();
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

    public void addVertexFormat(VertexFormat format) {
        formats.add(format);
    }

    public void removeVertexFormat(VertexFormat format) {
        formats.remove(format);
    }

    @Override
    public Set<VertexFormat> getVertexFormats() {
        return formats;
    }

    @Override
    public int getVertexCount() {
        return vertices.getVertexCount();
    }

    @Override
    public void setVertexCount(int vertexCount) {
        vertices.setVertexCount(vertexCount);
    }

    @Override
    public int getFaceCount() {
        return getMeshIndices().getFaceCount();
    }

    @Override
    public void setFaceCount(int faceCount) {
        for (Map<IndicesDataType<?>, EditableIndices<?>> subMap : indices.values())
            for (EditableIndices<?> indices : subMap.values())
                indices.setFaceCount(faceCount);
    }

    @Override
    public boolean isValid() {
        return (getFaceCount() > 0) && indicesDataType.isValidVertexCount(getVertexCount());
    }

    @Override
    public ByteBuffer getVertices(VertexFormat format) {
        return vertices.getVerticesBuffer(format);
    }

    @Override
    public ByteBuffer getIndices() {
        return getMeshIndices().getIndicesBuffer();
    }

    @Override
    public void clear() {
        for (Map<IndicesDataType<?>, EditableIndices<?>> subMap : indices.values())
            for (EditableIndices<?> editableIndices : subMap.values())
                editableIndices.clear();
        vertices.clear();
        meshType = MeshType.Mesh;
        indicesDataType = IndicesDataTypes.Short;
    }

    @Override
    public void setVertexDatum(int vertexIndex, VertexType vertexType, float vertexDatum) {
        vertices.setVertexDatum(vertexIndex, vertexType, vertexDatum);
    }

    @Override
    public void setFaceIndices(int faceIndex, int[] faceIndices) {
        for (Map<IndicesDataType<?>, EditableIndices<?>> subMap : indices.values())
            for (EditableIndices<?> indices : subMap.values())
                indices.setFaceIndices(faceIndex, faceIndices);
    }

    @Override
    public EditableMesh build() {
        return this;
    }

    @Override
    public float getVertexDatum(int vertexIndex, VertexType vertexType) {
        return vertices.getVertexDatum(vertexIndex, vertexType);
    }

    @Override
    public int[] getFaceIndices(int faceIndex) {
        return indices.get(MeshType.Mesh).get(IndicesDataTypes.Int).getFaceIndices(faceIndex);
    }

    private EditableIndices<?> getMeshIndices() {
        return indices.get(meshType).get(indicesDataType);
    }
}
