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
import meshio.mbwf.MbwfIO;
import meshio.util.PrimitiveInputStream;
import tests.Mesh;

public class MbwfIOWriteTest {
   private static final boolean IS_BIG_ENDIAN = true;
   private static final float   EQUALS_DELTA  = 1E-4f;

   @Test
   public void testWrite() throws IOException {
      testWriteMesh(null, null, null);
      testWriteMesh(format(), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(0, 0, 0), faces());
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(0, 0, 0), faces(0, 1, 2));
      testWriteMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y), vertices(-1, 1, -0.5f, 0.25f), faces(0, 1, 2));
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

   private void testWriteMesh(MeshVertexType[] vertexFormat, float[] vertexData, int[] faceIndices) throws IOException {
      Set<MeshVertexType> types = new HashSet<>();
      if (vertexFormat != null)
         types.addAll(Arrays.asList(vertexFormat));
      Mesh mesh = new Mesh(vertexFormat, vertexData, faceIndices);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
         MbwfIO.write(mesh, baos);
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
      if (types.contains(MeshVertexType.TextureCoordinate_U) || types.contains(MeshVertexType.TextureCoordinate_V))
         metadata += 1 << 13;
      if (types.contains(MeshVertexType.Color_R) || types.contains(MeshVertexType.Color_G) || types.contains(MeshVertexType.Color_B)
            || types.contains(MeshVertexType.Color_A))
         metadata += 1 << 12;
      if (types.contains(MeshVertexType.Color_A))
         metadata += 1 << 11;
      Assert.assertEquals(metadata, pis.readShort(IS_BIG_ENDIAN));
      Assert.assertEquals(vertexCount, pis.readInt(IS_BIG_ENDIAN));
      Assert.assertEquals(faceCount, pis.readInt(IS_BIG_ENDIAN));
      if (vertexFormat != null && vertexData != null) {
         for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            for (int datumIndex = 0; datumIndex < vertexFormat.length; datumIndex++) {
               float expected = vertexData[vertexIndex * vertexFormat.length + datumIndex];
               MeshVertexType type = vertexFormat[datumIndex];
               float datum = convertRaw(pis, type);
               Assert.assertEquals(expected, datum, EQUALS_DELTA);
            }
         }
      }
      int faceIndicesCount = faceCount * 3;
      for (int faceIndicesIndex = 0; faceIndicesIndex < faceIndicesCount; faceIndicesIndex++) {
         int actualFaceIndex = pis.readByte();
         Assert.assertEquals(actualFaceIndex, faceIndices[faceIndicesIndex]);
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
      case TextureCoordinate_U:
      case TextureCoordinate_V:
         return (float) ((double) pis.readByte() / Byte.MAX_VALUE);
      default:
         throw new IOException();
      }
   }
}
