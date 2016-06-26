package meshio;

public interface IMeshSaver {
   MeshVertexType[] getVertexFormat();

   int getVertexCount();

   int getFaceCount();

   void fillVertexData(int vertexIndex, float[] vertexData);

   void fillFaceIndices(int faceIndex, int[] faceIndices);
}
