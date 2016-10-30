package tests.mesh;

import org.junit.Assert;
import org.junit.Test;

import meshio.mesh.Mesh;

public class MeshTest {
   @Test
   public void testVertexCount() {
      Mesh mesh = new Mesh();
      mesh.setVertexCount(3);
      Assert.assertEquals(3, mesh.getVertexCount());
   }

   @Test
   public void testFaceCount() {
      Mesh mesh = new Mesh();
      mesh.setFaceCount(3);
      Assert.assertEquals(3, mesh.getFaceCount());
   }
}
