package meshio;

public class NullMeshBuilder<T> implements IMeshBuilder<T> {
   @Override
   public MeshVertexType[] getVertexFormat() {
      return null;
   }

   @Override
   public T build() {
      return null;
   }

   @Override
   public void setVertexCount(int vertexCount) {
   }

   @Override
   public void setFaceCount(int faceCount) {
   }

   @Override
   public void setVertexData(int vertexIndex, float[] vertexData) {
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
   }
}
