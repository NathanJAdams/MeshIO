package tests;

import meshio.IMeshBuilder;
import meshio.MeshVertexType;

public class MeshBuilder implements IMeshBuilder<Mesh> {
   private final MeshVertexType[] vertexFormat;
   private float[]                vertices;
   private int[]                  indices;

   public MeshBuilder(MeshVertexType[] vertexFormat) {
      this.vertexFormat = vertexFormat;
   }

   @Override
   public Mesh build() {
      return new Mesh(vertexFormat, vertices, indices);
   }

   @Override
   public void setVertexCount(int vertexCount) {
      int verticesLength = (vertexFormat == null)
            ? 0
            : vertexCount * vertexFormat.length;
      this.vertices = new float[verticesLength];
   }

   @Override
   public void setFaceCount(int faceCount) {
      this.indices = new int[faceCount * 3];
   }

   @Override
   public void setVertexDatum(int vertexIndex, MeshVertexType type, float datum) {
      for (int vertexFormatIndex = 0; vertexFormatIndex < vertexFormat.length; vertexFormatIndex++)
         if (type == vertexFormat[vertexFormatIndex])
            vertices[vertexIndex * vertexFormat.length + vertexFormatIndex] = datum;
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] indices) {
      for (int i = 0; i < 3; i++)
         this.indices[i] = indices[i];
   }
}
