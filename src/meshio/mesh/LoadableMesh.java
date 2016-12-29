package meshio.mesh;

import java.nio.ByteBuffer;

import meshio.IMeshBuilder;
import meshio.MeshVertexType;

public class LoadableMesh<T> implements IMesh, IMeshBuilder<LoadableMesh<T>> {
   private final MeshIndices<T>       indices;
   private final LoadableMeshVertices vertices;

   public LoadableMesh(MeshIndexType meshIndexType, IndicesDataType<T> indicesDataType, MeshVertexType... format) {
      this.indices = new MeshIndices<T>(indicesDataType, meshIndexType);
      this.vertices = new LoadableMeshVertices(format);
   }

   @Override
   public int getVertexCount() {
      return vertices.getVertexCount();
   }

   @Override
   public int getFaceCount() {
      return indices.getFaceCount();
   }

   @Override
   public void clear() {
      indices.clear();
      vertices.clear();
   }

   @Override
   public boolean isValid() {
      return indices.isValidVertexCount(getVertexCount());
   }

   @Override
   public ByteBuffer getVertices() {
      return vertices.toByteBuffer();
   }

   @Override
   public ByteBuffer getIndices() {
      return indices.getIndicesBuffer();
   }

   @Override
   public MeshVertexType[] getVertexFormat() {
      return vertices.getFormat();
   }

   @Override
   public LoadableMesh<T> build() {
      return this;
   }

   @Override
   public void setFaceCount(int faceCount) {
      indices.setFaceCount(faceCount);
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      indices.setFaceIndices(faceIndex, faceIndices);
   }

   @Override
   public void setVertexCount(int vertexCount) {
      vertices.setVertexCount(vertexCount);
   }

   @Override
   public void setVertexData(int vertexIndex, float[] vertexData) {
      vertices.setVertexData(vertexIndex, vertexData);
   }
}
