package meshio;

public interface IMeshBuilder<T> {
   T build();

   void setVertexCount(int vertexCount);

   void setFaceCount(int faceCount);

   void setVertexDatum(int vertexIndex, MeshVertexType type, float datum);

   void setFaceIndices(int faceIndex, int[] indices);
}
