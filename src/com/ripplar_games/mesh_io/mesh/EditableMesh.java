package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.index.EditableIndices;
import com.ripplar_games.mesh_io.index.IndicesDataType;
import com.ripplar_games.mesh_io.index.IndicesDataTypes;
import com.ripplar_games.mesh_io.vertex.EditableVertices;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

public class EditableMesh implements IMesh, IMeshBuilder<EditableMesh>, IMeshSaver {
    private final Map<MeshType, Map<IndicesDataType<?>, EditableIndices<?>>> indices = new HashMap<MeshType, Map<IndicesDataType<?>, EditableIndices<?>>>();
    private final EditableVertices vertices = new EditableVertices();
    private MeshType meshType = MeshType.Mesh;
    private IndicesDataType<?> indicesDataType = IndicesDataTypes.Short;

    public <T> EditableMesh() {
        for (MeshType meshType : MeshType.values()) {
            Map<IndicesDataType<?>, EditableIndices<?>> subMap = new HashMap<IndicesDataType<?>, EditableIndices<?>>();
            for (IndicesDataType<?> dataType : IndicesDataTypes.getAllTypes()) {
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

    @Override
    public VertexFormat getVertexFormat() {
        return vertices.getFormat();
    }

    public void setVertexFormat(VertexFormat format) {
        vertices.setFormat(format);
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
        return indicesDataType.isValidVertexCount(getVertexCount());
    }

    @Override
    public ByteBuffer getVertices() {
        return vertices.getVerticesBuffer();
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
