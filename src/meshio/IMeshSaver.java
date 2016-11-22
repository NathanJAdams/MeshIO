package meshio;

public interface IMeshSaver extends IMeshInfo {
   void getVertexData(int vertexIndex, float[] vertexData);

   void getFaceIndices(int faceIndex, int[] faceIndices);
}
