package meshio.mesh;

import meshio.IMeshBuilder;
import meshio.MeshVertexType;

public class NullMeshBuilder implements IMeshBuilder {
   private static final IMesh MESH = new NullMesh();

   @Override
   public MeshVertexType[] getVertexFormat() {
      return MESH.getVertexFormat();
   }

   @Override
   public int getVertexCount() {
      return MESH.getVertexCount();
   }

   @Override
   public int getFaceCount() {
      return MESH.getFaceCount();
   }

   @Override
   public void setVertexCount(int vertexCount) {
   }

   @Override
   public void setFaceCount(int faceCount) {
   }

   @Override
   public void setVertexData(int vertexIndex, float[] vertexData) {
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
   }

   @Override
   public IMesh build() {
      return MESH;
   }
}
