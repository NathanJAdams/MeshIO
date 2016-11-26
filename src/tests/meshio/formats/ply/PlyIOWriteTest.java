package tests.meshio.formats.ply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.formats.ply.PlyDataType;
import meshio.formats.ply.PlyFormat;
import meshio.formats.ply.PlyFormatAscii_1_0;
import meshio.formats.ply.PlyFormatBinaryBigEndian_1_0;
import meshio.formats.ply.PlyFormatBinaryLittleEndian_1_0;
import meshio.mesh.EditableMesh;
import tests.AReadWriteTest;
import util.PrimitiveInputStream;

public class PlyIOWriteTest extends AReadWriteTest {
   @Test
   public void testWrite() throws IOException {
      testWriteMesh(format(), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(new float[] { 0, 0, 0 }),
            faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(new float[] { 0, 0, 0 }),
            faces(new int[] { 0, 1, 2 }));
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y), vertices(new float[] { 1, 2, 3, 4 }), faces(new int[] { 0, 1, 2 }));
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Color_R), vertices(new float[] { 1, 2, 3, 4 }), faces(new int[] { 1, 2, 3 }));
   }

   private void testWriteMesh(MeshVertexType[] format, float[][] vertexData, int[][] faceIndices) throws IOException {
      testWriteMesh(format, vertexData, faceIndices, new PlyFormatAscii_1_0());
      testWriteMesh(format, vertexData, faceIndices, new PlyFormatBinaryBigEndian_1_0());
      testWriteMesh(format, vertexData, faceIndices, new PlyFormatBinaryLittleEndian_1_0());
   }

   private void testWriteMesh(MeshVertexType[] vertexFormat, float[][] vertexData, int[][] faceIndices, PlyFormat plyFormat) throws IOException {
      EditableMesh mesh = createMesh(vertexFormat, vertexData, faceIndices);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
         plyFormat.write(mesh, baos);
      } catch (MeshIOException e) {
         Assert.fail();
      }
      byte[] buffer = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      PrimitiveInputStream pis = new PrimitiveInputStream(bais);
      int vertexCount = (vertexFormat == null || vertexData == null || vertexFormat.length == 0)
            ? 0
            : vertexData.length;
      int faceCount = (faceIndices == null)
            ? 0
            : faceIndices.length;
      Assert.assertEquals("ply", pis.readLine());
      Assert.assertEquals("format " + plyFormat.getEncoding() + ' ' + plyFormat.getVersion(), pis.readLine());
      Assert.assertEquals("element vertex " + vertexCount, pis.readLine());
      if (vertexFormat != null)
         for (int i = 0; i < vertexFormat.length; i++)
            Assert.assertEquals("property float " + PlyFormat.getPropertyName(vertexFormat[i]), pis.readLine());
      Assert.assertEquals("element face " + faceCount, pis.readLine());
      Assert.assertEquals("property list uchar int vertex_index", pis.readLine());
      Assert.assertEquals("end_header", pis.readLine());
      if (vertexFormat != null) {
         float[] expectedVertexData = new float[vertexFormat.length];
         float[] actualVertexData = new float[vertexFormat.length];
         for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            for (int i = 0; i < vertexFormat.length; i++) {
               expectedVertexData[i] = vertexData[vertexIndex][i];
            }
            plyFormat.fillVertexData(pis, actualVertexData, PlyDataType.Float);
            Assert.assertArrayEquals(expectedVertexData, actualVertexData, (float) 1E-6);
         }
      }
      int[] expectedFaceIndices = new int[3];
      for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
         for (int i = 0; i < 3; i++) {
            expectedFaceIndices[i] = faceIndices[faceIndex][i];
         }
         mesh.getFaceIndices(faceIndex, expectedFaceIndices);
         int[] actualFaceIndices = plyFormat.readFaceIndices(pis, PlyDataType.Uchar, PlyDataType.Int);
         Assert.assertArrayEquals(expectedFaceIndices, actualFaceIndices);
      }
   }
}
