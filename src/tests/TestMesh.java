package tests;

import java.util.Arrays;

import meshio.IMeshSaver;
import meshio.MeshVertexType;

public class TestMesh implements IMeshSaver {
   private static final int       VERTICES_PER_FACE = 3;
   private final MeshVertexType[] format;
   private final float[]          vertices;
   private final int[]            indices;
   private final int              hash;

   public TestMesh(MeshVertexType[] format, float[] vertices, int[] indices) {
      this.format = (format == null)
            ? new MeshVertexType[0]
            : format;
      this.vertices = (vertices == null)
            ? new float[0]
            : vertices;
      this.indices = (indices == null)
            ? new int[0]
            : indices;
      this.hash = createHash();
   }

   private int createHash() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(indices);
      result = prime * result + Arrays.hashCode(vertices);
      return result;
   }

   @Override
   public MeshVertexType[] getVertexFormat() {
      return format;
   }

   @Override
   public int getVertexCount() {
      return (format.length == 0)
            ? 0
            : vertices.length / format.length;
   }

   @Override
   public int getFaceCount() {
      return indices.length / 3;
   }

   @Override
   public void getVertexData(int vertexIndex, float[] vertexData) {
      int startIndex = vertexIndex * format.length;
      for (int i = 0; i < format.length; i++)
         vertexData[i] = vertices[startIndex + i];
   }

   @Override
   public void getFaceIndices(int faceIndex, int[] faceIndices) {
      int startIndex = faceIndex * VERTICES_PER_FACE;
      for (int i = 0; i < VERTICES_PER_FACE; i++)
         faceIndices[i] = indices[startIndex + i];
   }

   @Override
   public int hashCode() {
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TestMesh other = (TestMesh) obj;
      boolean isFormatEqual = Arrays.equals(format, other.format);
      boolean isVerticesEqual = Arrays.equals(vertices, other.vertices);
      boolean isIndicesEqual = Arrays.equals(indices, other.indices);
      return isFormatEqual && isVerticesEqual && isIndicesEqual;
   }
}
