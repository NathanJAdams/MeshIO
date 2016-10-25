package meshio.mesh.vertices;

public interface IGettableVertices extends IFormatableVertices {
   int getVertexCount();

   void getVertexData(int vertexIndex, float[] vertexData);
}