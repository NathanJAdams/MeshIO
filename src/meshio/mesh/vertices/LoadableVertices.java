package meshio.mesh.vertices;

import java.util.Arrays;

import meshio.MeshVertexType;

public class LoadableVertices implements IVerticesData, ISettableVertices {
   private final MeshVertexType[] format;
   private float[]                vertexData = new float[0];

   public LoadableVertices(MeshVertexType... format) {
      this.format = format;
   }

   @Override
   public float[] getVerticesData() {
      return vertexData;
   }

   @Override
   public MeshVertexType[] getFormat() {
      return format;
   }

   @Override
   public void setVertexCount(int vertexCount) {
      this.vertexData = Arrays.copyOf(vertexData, vertexCount * format.length);
   }

   @Override
   public void setVertexData(int vertexIndex, float[] verticesData) {
      int offset = vertexIndex * format.length;
      for (int i = 0; i < verticesData.length; i++)
         verticesData[offset + i] = verticesData[i];
   }
}
