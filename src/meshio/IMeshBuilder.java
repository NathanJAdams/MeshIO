package meshio;

public interface IMeshBuilder<T> extends IFormattableMesh {
   void setVertexCount(int vertexCount);

   void setFaceCount(int faceCount);

   void setVertexData(int vertexIndex, float[] vertexData);

   void setFaceIndices(int faceIndex, int[] faceIndices);

   T build();
}
