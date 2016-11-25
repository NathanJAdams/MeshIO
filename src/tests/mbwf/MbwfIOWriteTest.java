package tests.mbwf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.formats.mbwf.MbwfIO;
import meshio.mesh.EditableMesh;
import tests.AReadWriteTest;
import util.PrimitiveInputStream;

public class MbwfIOWriteTest extends AReadWriteTest {
   private static final boolean IS_BIG_ENDIAN = true;
   private static final float   EQUALS_DELTA  = 1E-4f;

   @Test(expected = MeshIOException.class)
   public void testBadWrite() throws IOException, MeshIOException {
      testWriteMesh(format(), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X), vertices(), faces());
   }

   @Test
   public void testWrite() throws IOException, MeshIOException {
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(new float[] { 0, 0, 0 }),
            faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(new float[] { 0, 0, 0 }),
            faces(new int[] { 0, 1, 2 }));
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y), vertices(new float[] { -1, 1, -0.5f, 0.25f }),
            faces(new int[] { 0, 1, 2 }));
   }

   private void testWriteMesh(MeshVertexType[] vertexFormat, float[][] vertexData, int[][] faceIndices) throws IOException, MeshIOException {
      Set<MeshVertexType> types = new HashSet<>();
      if (vertexFormat != null)
         types.addAll(Arrays.asList(vertexFormat));
      EditableMesh mesh = createMesh(vertexFormat, vertexData, faceIndices);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      MbwfIO.write(mesh, baos);
      byte[] buffer = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      PrimitiveInputStream pis = new PrimitiveInputStream(bais);
      int vertexCount = (vertexFormat == null || vertexData == null || vertexFormat.length == 0)
            ? 0
            : vertexData.length;
      int faceCount = (faceIndices == null)
            ? 0
            : faceIndices.length;
      byte[] magicBytes = new byte[4];
      pis.read(magicBytes);
      short version = pis.readShort(IS_BIG_ENDIAN);
      Assert.assertEquals(1, version);
      Assert.assertArrayEquals(new byte[] { 'M', 'B', 'W', 'F' }, magicBytes);
      short metadata = 0;
      if (types.contains(MeshVertexType.Position_Z))
         metadata += 1 << 15;
      if (types.contains(MeshVertexType.Normal_X) || types.contains(MeshVertexType.Normal_Y) || types.contains(MeshVertexType.Normal_Z))
         metadata += 1 << 14;
      if (types.contains(MeshVertexType.ImageCoord_X) || types.contains(MeshVertexType.ImageCoord_Y))
         metadata += 1 << 13;
      if (types.contains(MeshVertexType.Color_R) || types.contains(MeshVertexType.Color_G) || types.contains(MeshVertexType.Color_B)
            || types.contains(MeshVertexType.Color_A))
         metadata += 1 << 12;
      if (types.contains(MeshVertexType.Color_A))
         metadata += 1 << 11;
      Assert.assertEquals(metadata, pis.readShort(IS_BIG_ENDIAN));
      Assert.assertEquals(vertexCount, pis.readInt(IS_BIG_ENDIAN));
      if (vertexFormat != null && vertexData != null) {
         for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            for (int datumIndex = 0; datumIndex < vertexFormat.length; datumIndex++) {
               float expected = vertexData[vertexIndex][datumIndex];
               MeshVertexType type = vertexFormat[datumIndex];
               float datum = convertRaw(pis, type);
               Assert.assertEquals(expected, datum, EQUALS_DELTA);
            }
         }
      }
      Assert.assertEquals(faceCount, pis.readInt(IS_BIG_ENDIAN));
      for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
         for (int faceIndicesIndex = 0; faceIndicesIndex < 3; faceIndicesIndex++) {
            int actualFaceIndex = pis.readByte();
            Assert.assertEquals(actualFaceIndex, faceIndices[faceIndex][faceIndicesIndex]);
         }
      }
   }

   private float convertRaw(PrimitiveInputStream pis, MeshVertexType type) throws IOException {
      switch (type) {
      case Position_X:
      case Position_Y:
      case Position_Z:
      case Normal_X:
      case Normal_Y:
      case Normal_Z:
         return (float) ((double) pis.readShort(IS_BIG_ENDIAN) / Short.MAX_VALUE);
      case Color_R:
      case Color_G:
      case Color_B:
      case Color_A:
      case ImageCoord_X:
      case ImageCoord_Y:
         return (float) ((double) pis.readByte() / Byte.MAX_VALUE);
      default:
         throw new IOException();
      }
   }
}
