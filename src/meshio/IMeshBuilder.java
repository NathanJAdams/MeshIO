package meshio;

import meshio.mesh.IMesh;

public interface IMeshBuilder extends IMeshInfo {
   void setVertexCount(int vertexCount);

   void setFaceCount(int faceCount);

   void setVertexData(int vertexIndex, float[] vertexData);

   void setFaceIndices(int faceIndex, int[] faceIndices);

   IMesh build();
}
