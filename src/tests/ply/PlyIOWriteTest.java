package tests.ply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.ply.PlyDataType;
import meshio.ply.PlyFormat;
import meshio.ply.PlyIO;
import meshio.util.PrimitiveInputStream;
import tests.TestMesh;

public class PlyIOWriteTest {
   @Test
   public void testWrite() throws IOException {
      testWriteMesh(null, null, null);
      testWriteMesh(format(), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(0, 0, 0), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(0, 0, 0), faces(0, 1, 2));
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y), vertices(1, 2, 3, 4), faces(0, 1, 2));
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Color_R), vertices(1, 2, 3, 4), faces(1, 2, 3));
   }

   private MeshVertexType[] format(MeshVertexType... types) {
      return types;
   }

   private float[] vertices(float... vertices) {
      return vertices;
   }

   private int[] faces(int... faces) {
      return faces;
   }

   private void testWriteMesh(MeshVertexType[] format, float[] vertexData, int[] faceIndices) throws IOException {
      testWriteMesh(format, vertexData, faceIndices, PlyFormat.ASCII_1_0);
      testWriteMesh(format, vertexData, faceIndices, PlyFormat.BINARY_BIG_ENDIAN_1_0);
      testWriteMesh(format, vertexData, faceIndices, PlyFormat.BINARY_LITTLE_ENDIAN_1_0);
   }

   private void testWriteMesh(MeshVertexType[] vertexFormat, float[] vertexData, int[] faceIndices, PlyFormat plyFormat) throws IOException {
      TestMesh mesh = new TestMesh(vertexFormat, vertexData, faceIndices);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
         PlyIO.write(mesh, baos, plyFormat);
      } catch (MeshIOException e) {
         Assert.fail();
      }
      byte[] buffer = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      PrimitiveInputStream pis = new PrimitiveInputStream(bais);
      int vertexCount = (vertexFormat == null || vertexData == null || vertexFormat.length == 0)
            ? 0
            : vertexData.length / vertexFormat.length;
      int faceCount = (faceIndices == null)
            ? 0
            : faceIndices.length / 3;
      Assert.assertEquals("ply", pis.readLine());
      Assert.assertEquals("format " + plyFormat.getEncoding() + ' ' + plyFormat.getVersion(), pis.readLine());
      Assert.assertEquals("element vertex " + vertexCount, pis.readLine());
      if (vertexFormat != null)
         for (int i = 0; i < vertexFormat.length; i++)
            Assert.assertEquals("property float " + PlyIO.getPropertyName(vertexFormat[i]), pis.readLine());
      Assert.assertEquals("element face " + faceCount, pis.readLine());
      Assert.assertEquals("property list uchar int vertex_index", pis.readLine());
      Assert.assertEquals("end_header", pis.readLine());
      if (vertexFormat != null) {
         float[] expectedVertexData = new float[vertexFormat.length];
         float[] actualVertexData = new float[vertexFormat.length];
         for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            for (int i = 0; i < vertexFormat.length; i++) {
               int startIndex = vertexIndex * vertexFormat.length;
               expectedVertexData[i] = vertexData[startIndex + i];
            }
            plyFormat.fillVertexData(pis, actualVertexData, PlyDataType.Float);
            Assert.assertArrayEquals(expectedVertexData, actualVertexData, (float) 1E-6);
         }
      }
      int[] expectedFaceIndices = new int[3];
      for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
         for (int i = 0; i < 3; i++) {
            int startIndex = faceIndex * 3;
            expectedFaceIndices[i] = faceIndices[startIndex + i];
         }
         mesh.getFaceIndices(faceIndex, expectedFaceIndices);
         int[] actualFaceIndices = plyFormat.readFaceIndices(pis, PlyDataType.Uchar, PlyDataType.Int);
         Assert.assertArrayEquals(expectedFaceIndices, actualFaceIndices);
      }
   }
}
