package meshio.mesh;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshVertexType;

public class Mesh implements IMeshSaver, IMeshBuilder<Mesh> {
   private final MeshVertices         vertices     = new MeshVertices();
   private final MeshIndices<byte[]>  byteIndices  = new MeshIndices<>(IndicesDataTypes.Byte);
   private final MeshIndices<short[]> shortIndices = new MeshIndices<>(IndicesDataTypes.Short);
   private final MeshIndices<int[]>   intIndices   = new MeshIndices<>(IndicesDataTypes.Int);
   private IndicesDataType<?>         indicesDataType;

   public void setFormat(MeshVertexType... format) {
      vertices.setFormat(format);
   }

   public float[] getVertexData() {
      return vertices.getVerticesData();
   }

   public IndicesDataType<?> getIndicesDataType() {
      return indicesDataType;
   }

   public byte[] getIndicesAsByteArray(MeshIndexType meshIndexType) {
      return byteIndices.getIndicesData(meshIndexType);
   }

   public short[] getIndicesAsShortArray(MeshIndexType meshIndexType) {
      return shortIndices.getIndicesData(meshIndexType);
   }

   public int[] getIndicesAsIntArray(MeshIndexType meshIndexType) {
      return intIndices.getIndicesData(meshIndexType);
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

   @Override
   public Mesh build() {
      return this;
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
}
