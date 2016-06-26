package meshio;

public class NullMeshBuilder<T> implements IMeshBuilder<T> {
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
   public void setVertexDatum(int vertexIndex, MeshVertexType type, float datum) {
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] indices) {
   }
}
