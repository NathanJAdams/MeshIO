package meshio.mesh;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshVertexType;

public class EditableMesh implements IMesh, IMeshBuilder<EditableMesh>, IMeshSaver {
   private final Map<MeshIndexType, Map<IndicesDataType<?>, MeshIndices<?>>> indices         = new HashMap<>();
   private final EditableMeshVertices                                        vertices        = new EditableMeshVertices();
   private MeshIndexType                                                     meshIndexType   = MeshIndexType.Mesh;
   private IndicesDataType<?>                                                indicesDataType = IndicesDataTypes.Short;

   public EditableMesh() {
      for (MeshIndexType meshIndexType : MeshIndexType.values()) {
         Map<IndicesDataType<?>, MeshIndices<?>> subMap = new HashMap<>();
         for (IndicesDataType<?> dataType : IndicesDataTypes.getAllTypes())
            subMap.put(dataType, new MeshIndices<>(dataType, meshIndexType));
         indices.put(meshIndexType, subMap);
      }
   }

   public float getVertexDatum(int vertexIndex, MeshVertexType meshVertexType) {
      return vertices.getVertexDatum(vertexIndex, meshVertexType);
   }

   public void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float datum) {
      vertices.setVertexDatum(vertexIndex, meshVertexType, datum);
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

   public void setVertexFormat(MeshVertexType... format) {
      vertices.setFormat(format);
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
   public EditableMesh build() {
      return this;
   }

   @Override
   public MeshVertexType[] getVertexFormat() {
      return vertices.getFormat();
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
   public void setVertexData(int vertexIndex, float[] vertexData) {
      vertices.setVertexData(vertexIndex, vertexData);
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      for (Map<IndicesDataType<?>, MeshIndices<?>> subMap : indices.values())
         for (MeshIndices<?> indices : subMap.values())
            indices.setFaceIndices(faceIndex, faceIndices);
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
