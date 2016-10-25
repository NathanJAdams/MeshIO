package meshio;

public class NullMeshSaver implements IMeshSaver {
   @Override
   public MeshVertexType[] getVertexFormat() {
      return new MeshVertexType[0];
   }

   @Override
   public int getVertexCount() {
      return 0;
   }

   @Override
   public int getFaceCount() {
      return 0;
   }

   @Override
   public void getVertexData(int vertexIndex, float[] vertexData) {
   }

   @Override
   public void getFaceIndices(int faceIndex, int[] faceIndices) {
   }
}
