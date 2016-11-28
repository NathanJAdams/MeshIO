package tests.meshio.formats;

import org.junit.Assert;
import org.junit.Ignore;

import meshio.MeshVertexType;
import meshio.mesh.EditableMesh;

@Ignore
public abstract class AReadWriteTest {
   public MeshVertexType[] format(MeshVertexType... types) {
      return types;
   }

   public float[][] vertices(float[]... vertices) {
      return vertices;
   }

   public int[][] faces(int[]... faces) {
      return faces;
   }

   public EditableMesh createMesh(MeshVertexType[] format, float[][] vertices, int[][] indices) {
      EditableMesh mesh = new EditableMesh();
      mesh.setVertexFormat(format);
      mesh.setVertexCount(vertices.length);
      mesh.setFaceCount(indices.length);
      for (int i = 0; i < vertices.length; i++) {
         float[] vertex = vertices[i];
         mesh.setVertexData(i, vertex);
      }
      for (int i = 0; i < indices.length; i++) {
         int[] face = indices[i];
         mesh.setFaceIndices(i, face);
      }
      return mesh;
   }

   public void checkMeshEquals(EditableMesh a, EditableMesh b) {
      Assert.assertEquals(a.getFaceCount(), b.getFaceCount());
      Assert.assertEquals(a.getIndices(), b.getIndices());
      Assert.assertEquals(a.isValid(), b.isValid());
      Assert.assertEquals(a.getVertexCount(), b.getVertexCount());
      Assert.assertArrayEquals(a.getVertexFormat(), b.getVertexFormat());
      Assert.assertEquals(a.getVertices(), b.getVertices());
   }
}
