package tests.mesh;

import org.junit.Assert;
import org.junit.Test;

import meshio.MeshVertexType;
import meshio.mesh.MeshVertices;

public class VerticesTest {
   private static final double DELTA = 1E-9;

   @Test
   public void testVertexCount() {
      MeshVertices vertices = new MeshVertices();
      Assert.assertEquals(0, vertices.getVertexCount());
      vertices.setVertexCount(1);
      Assert.assertEquals(1, vertices.getVertexCount());
   }

   @Test
   public void testVertexDataAfterFormatChange() {
      MeshVertices vertices = new MeshVertices(MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B, MeshVertexType.Color_A);
      vertices.setVertexCount(5);
      int index = 3;
      vertices.setVertexDatum(index, MeshVertexType.Color_A, 11);
      vertices.setVertexDatum(index, MeshVertexType.Color_B, 23);
      float[] preVertexData = new float[2];
      vertices.getVerticesAsFormat(MeshVertexType.Color_A, MeshVertexType.Color_B);
      float preA = preVertexData[0];
      float preB = preVertexData[1];
      float[] postVertexData = new float[4];
      vertices.getVerticesAsFormat(MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B, MeshVertexType.Color_A);
      float postA = postVertexData[3];
      float postB = postVertexData[2];
      Assert.assertEquals(preA, postA, DELTA);
      Assert.assertEquals(preB, postB, DELTA);
   }

   @Test
   public void testVertexDataAfterVertexCountChange() {
      MeshVertices vertices = new MeshVertices(MeshVertexType.Color_A);
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

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testArrayExceptionOver() {
      MeshVertices vertices = new MeshVertices(MeshVertexType.Color_A);
      vertices.setVertexCount(1);
      vertices.getVertexDatum(2, MeshVertexType.Color_A);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testArrayExceptionUnder() {
      MeshVertices vertices = new MeshVertices(MeshVertexType.Color_A);
      vertices.setVertexCount(1);
      vertices.getVertexDatum(-1, MeshVertexType.Color_A);
   }
}
