package meshio.mesh;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshVertexType;
import meshio.mesh.indices.EditableIndices;
import meshio.mesh.indices.IndicesDataType;
import meshio.mesh.indices.IndicesDataTypes;
import meshio.mesh.indices.MeshIndexType;
import meshio.mesh.vertices.EditableVertices;

public class EditableMesh implements IMeshData, IMeshSaver, IMeshBuilder<EditableMesh> {
   private final EditableVertices         vertices;
   private final EditableIndices<byte[]>  byteIndices;
   private final EditableIndices<short[]> shortIndices;
   private final EditableIndices<int[]>   intIndices;
   private IndicesDataType<?>             indicesDataType;

   public EditableMesh(MeshIndexType meshIndexType, MeshVertexType format) {
      this.vertices = new EditableVertices(format);
      this.byteIndices = new EditableIndices<>(IndicesDataTypes.Byte, meshIndexType);
      this.shortIndices = new EditableIndices<>(IndicesDataTypes.Short, meshIndexType);
      this.intIndices = new EditableIndices<>(IndicesDataTypes.Int, meshIndexType);
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
   public void setFaceCount(int faceCount) {
      byteIndices.setFaceCount(faceCount);
      shortIndices.setFaceCount(faceCount);
      intIndices.setFaceCount(faceCount);
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      byteIndices.setFaceIndices(faceIndex, faceIndices);
      shortIndices.setFaceIndices(faceIndex, faceIndices);
      intIndices.setFaceIndices(faceIndex, faceIndices);
   }

   @Override
   public void setVertexCount(int vertexCount) {
      vertices.setVertexCount(vertexCount);
      if (vertexCount <= 0xFF)
         this.indicesDataType = IndicesDataTypes.Byte;
      else if (vertexCount <= 0xFFFF)
         this.indicesDataType = IndicesDataTypes.Short;
      else if (vertexCount <= 0xFFFFFFFF)
         this.indicesDataType = IndicesDataTypes.Int;
   }

   @Override
   public void setVertexData(int vertexIndex, float[] vertexData) {
      vertices.setVertexData(vertexIndex, vertexData);
   }

   @Override
   public float[] getVertexData() {
      return vertices.getVerticesData();
   }

   @Override
   public IndicesDataType<?> getIndicesDataType() {
      return indicesDataType;
   }

   @Override
   public byte[] getIndicesAsByteArray() {
      return byteIndices.getIndicesData();
   }

   @Override
   public short[] getIndicesAsShortArray() {
      return shortIndices.getIndicesData();
   }

   @Override
   public int[] getIndicesAsIntArray() {
      return intIndices.getIndicesData();
   }

   @Override
   public int getVertexCount() {
      return vertices.getVertexCount();
   }

   @Override
   public int getFaceCount() {
      return intIndices.getFaceCount();
   }

   @Override
   public void getVertexData(int vertexIndex, float[] vertexData) {
      vertices.getVertexData(vertexIndex, vertexData);
   }

   @Override
   public void getFaceIndices(int faceIndex, int[] faceIndices) {
      intIndices.getFaceIndices(faceIndex, faceIndices);
   }
}
