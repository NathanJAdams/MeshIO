package meshio;

public interface IMeshSaver extends IFormattableMesh {
   int getVertexCount();

   int getFaceCount();

   void getVertexData(int vertexIndex, float[] vertexData);

   void getFaceIndices(int faceIndex, int[] faceIndices);
}
