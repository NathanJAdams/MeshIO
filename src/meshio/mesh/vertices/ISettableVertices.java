package meshio.mesh.vertices;

public interface ISettableVertices extends IFormatableVertices {
   void setVertexCount(int vertexCount);

   void setVertexData(int vertexIndex, float[] verticesData);
}