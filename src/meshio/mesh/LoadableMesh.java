package meshio.mesh;

import meshio.IMeshBuilder;
import meshio.MeshVertexType;
import meshio.mesh.indices.IndicesDataType;
import meshio.mesh.indices.IndicesDataTypes;
import meshio.mesh.indices.LoadableIndices;
import meshio.mesh.indices.MeshIndexType;
import meshio.mesh.vertices.LoadableVertices;

public class LoadableMesh implements IMeshData, IMeshBuilder<LoadableMesh> {
   private final LoadableVertices         vertices;
   private final LoadableIndices<byte[]>  byteIndices;
   private final LoadableIndices<short[]> shortIndices;
   private final LoadableIndices<int[]>   intIndices;
   private IndicesDataType<?>             indicesDataType;

   public LoadableMesh(MeshIndexType meshIndexType, MeshVertexType format) {
      this.vertices = new LoadableVertices(format);
      this.byteIndices = new LoadableIndices<>(IndicesDataTypes.Byte, meshIndexType);
      this.shortIndices = new LoadableIndices<>(IndicesDataTypes.Short, meshIndexType);
      this.intIndices = new LoadableIndices<>(IndicesDataTypes.Int, meshIndexType);
   }

   @Override
   public LoadableMesh build() {
      return this;
   }

   @Override
   public void setFaceCount(int faceCount) {
      if (indicesDataType == IndicesDataTypes.Byte)
         byteIndices.setFaceCount(faceCount);
      else if (indicesDataType == IndicesDataTypes.Short)
         shortIndices.setFaceCount(faceCount);
      else if (indicesDataType == IndicesDataTypes.Int)
         intIndices.setFaceCount(faceCount);
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      if (indicesDataType == IndicesDataTypes.Byte)
         byteIndices.setFaceIndices(faceIndex, faceIndices);
      else if (indicesDataType == IndicesDataTypes.Short)
         shortIndices.setFaceIndices(faceIndex, faceIndices);
      else if (indicesDataType == IndicesDataTypes.Int)
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
   public MeshVertexType[] getVertexFormat() {
      return vertices.getFormat();
   }
}
