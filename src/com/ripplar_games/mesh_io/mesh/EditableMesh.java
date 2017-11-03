package com.ripplar_games.mesh_io.mesh;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.MeshVertexType;

public class EditableMesh implements IMesh, IMeshBuilder<EditableMesh>, IMeshSaver {
    private final Map<MeshIndexType, Map<IndicesDataType<?>, MeshIndices<?>>> indices = new HashMap<MeshIndexType, Map<IndicesDataType<?>, MeshIndices<?>>>();
    private final EditableMeshVertices vertices = new EditableMeshVertices();
    private MeshIndexType meshIndexType = MeshIndexType.Mesh;
    private IndicesDataType<?> indicesDataType = IndicesDataTypes.Short;

    public <T> EditableMesh() {
        for (MeshIndexType meshIndexType : MeshIndexType.values()) {
            Map<IndicesDataType<?>, MeshIndices<?>> subMap = new HashMap<IndicesDataType<?>, MeshIndices<?>>();
            for (IndicesDataType<?> dataType : IndicesDataTypes.getAllTypes()) {
                // hack to allow proper typing
                @SuppressWarnings("unchecked")
                IndicesDataType<T> typedDataType = (IndicesDataType<T>) dataType;
                subMap.put(dataType, new MeshIndices<T>(typedDataType, meshIndexType));
            }
            indices.put(meshIndexType, subMap);
        }
    }

    public float getVertexDatum(int vertexIndex, MeshVertexType meshVertexType) {
        return vertices.getVertexDatum(vertexIndex, meshVertexType);
    }

    public void setFaceIndicesIndex(int faceIndex, int indicesIndex, int vertexIndex) {
        for (Map<IndicesDataType<?>, MeshIndices<?>> subMap : indices.values())
            for (MeshIndices<?> indices : subMap.values())
                indices.setFaceIndex(faceIndex, indicesIndex, vertexIndex);
    }

    public void setIndicesDataType(IndicesDataType<?> indicesDataType) {
        if (indicesDataType != null)
            this.indicesDataType = indicesDataType;
    }

    public void setMeshIndexType(MeshIndexType meshIndexType) {
        if (meshIndexType != null)
            this.meshIndexType = meshIndexType;
    }

    @Override
    public List<MeshVertexType> getVertexFormat() {
        return vertices.getFormat();
    }

    public void setVertexFormat(MeshVertexType... format) {
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
        for (Map<IndicesDataType<?>, MeshIndices<?>> subMap : indices.values())
            for (MeshIndices<?> indices : subMap.values())
                indices.setFaceCount(faceCount);
    }

    @Override
    public boolean isValid() {
        return indicesDataType.isValidVertexCount(getVertexCount());
    }

    @Override
    public ByteBuffer getVertices() {
        return vertices.toByteBuffer();
    }

    @Override
    public ByteBuffer getIndices() {
        return getMeshIndices().getIndicesBuffer();
    }

    @Override
    public void clear() {
        for (Map<IndicesDataType<?>, MeshIndices<?>> subMap : indices.values())
            for (MeshIndices<?> meshIndices : subMap.values())
                meshIndices.clear();
        vertices.clear();
        meshIndexType = MeshIndexType.Mesh;
        indicesDataType = IndicesDataTypes.Short;
    }

    @Override
    public void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float vertexDatum) {
        vertices.setVertexDatum(vertexIndex, meshVertexType, vertexDatum);
    }

    @Override
    public void setFaceIndices(int faceIndex, int[] faceIndices) {
        for (Map<IndicesDataType<?>, MeshIndices<?>> subMap : indices.values())
            for (MeshIndices<?> indices : subMap.values())
                indices.setFaceIndices(faceIndex, faceIndices);
    }

    @Override
    public EditableMesh build() {
        return this;
    }

    @Override
    public void getVertexData(int vertexIndex, float[] vertexData) {
        vertices.getVertexData(vertexIndex, vertexData);
    }

    @Override
    public void getFaceIndices(int faceIndex, int[] faceIndices) {
        getMeshIndices().getFaceIndices(faceIndex, faceIndices);
    }

    private MeshIndices<?> getMeshIndices() {
        return indices.get(meshIndexType).get(indicesDataType);
    }
}
