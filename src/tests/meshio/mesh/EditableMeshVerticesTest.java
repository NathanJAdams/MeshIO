package tests.meshio.mesh;

import org.junit.Assert;
import org.junit.Test;

import meshio.MeshVertexType;
import meshio.mesh.EditableMeshVertices;

public class EditableMeshVerticesTest {
   private static final double DELTA = 1E-12;

   @Test
   public void testVertexCount() {
      EditableMeshVertices vertices = new EditableMeshVertices();
      Assert.assertEquals(0, vertices.getVertexCount());
      vertices.setVertexCount(1);
      Assert.assertEquals(1, vertices.getVertexCount());
   }

   @Test
   public void testVertexDataAfterFormatChange() {
      EditableMeshVertices vertices = new EditableMeshVertices();
      vertices.setFormat(MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B, MeshVertexType.Color_A);
      vertices.setVertexCount(5);
      int index = 3;
      float preA = 11;
      float preB = 23;
      vertices.setVertexDatum(index, MeshVertexType.Color_A, preA);
      vertices.setVertexDatum(index, MeshVertexType.Color_B, preB);
      vertices.setFormat(MeshVertexType.Color_A, MeshVertexType.Color_B);
      vertices.getVertexDatum(index, MeshVertexType.Color_A);
      Assert.assertEquals(preA, vertices.getVertexDatum(index, MeshVertexType.Color_A), DELTA);
      Assert.assertEquals(preB, vertices.getVertexDatum(index, MeshVertexType.Color_B), DELTA);
      vertices.setFormat(MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B, MeshVertexType.Color_A);
      Assert.assertEquals(preA, vertices.getVertexDatum(index, MeshVertexType.Color_A), DELTA);
      Assert.assertEquals(preB, vertices.getVertexDatum(index, MeshVertexType.Color_B), DELTA);
   }

   @Test
   public void testVertexDataAfterVertexCountChange() {
      EditableMeshVertices vertices = new EditableMeshVertices();
      vertices.setFormat(MeshVertexType.Color_A);
      vertices.setVertexCount(2);
      int index = 1;
      float datum = 23;
      vertices.setVertexDatum(index, MeshVertexType.Color_A, datum);
      Assert.assertEquals(datum, vertices.getVertexDatum(index, MeshVertexType.Color_A), DELTA);
      vertices.setVertexCount(3);
      Assert.assertEquals(datum, vertices.getVertexDatum(index, MeshVertexType.Color_A), DELTA);
      vertices.setVertexCount(1);
      vertices.setVertexCount(2);
      Assert.assertEquals(0, vertices.getVertexDatum(index, MeshVertexType.Color_A), DELTA);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void testIndexExceptionOver() {
      EditableMeshVertices vertices = new EditableMeshVertices();
      vertices.setFormat(MeshVertexType.Color_A);
      vertices.setVertexCount(1);
      vertices.getVertexDatum(2, MeshVertexType.Color_A);
   }

   @Test(expected = IndexOutOfBoundsException.class)
   public void testIndexExceptionUnder() {
      EditableMeshVertices vertices = new EditableMeshVertices();
      vertices.setFormat(MeshVertexType.Color_A);
      vertices.setVertexCount(1);
      vertices.getVertexDatum(-1, MeshVertexType.Color_A);
   }
}
