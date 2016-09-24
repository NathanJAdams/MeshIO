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
import meshio.util.PrimitiveOutputStream;
import tests.Mesh;
import tests.MeshBuilder;

public class MbwfIOReadTest {
   private static final boolean IS_BIG_ENDIAN = true;

   @Test
   public void testRead() throws IOException {
      testReadMesh(null, null, null);
      testReadMesh(format(), vertices(), faces());
      testReadMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y), vertices(0, 0), faces(0, 1, 2));
      testReadMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(-1, 0, 1), faces(0, 1, 2));
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

   private void testReadMesh(MeshVertexType[] vertexFormat, float[] vertexData, int[] faceIndices) throws IOException {
      Set<MeshVertexType> types = new HashSet<>();
      if (vertexFormat != null)
         types.addAll(Arrays.asList(vertexFormat));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrimitiveOutputStream pos = new PrimitiveOutputStream(baos);
      pos.write(new byte[] { 'M', 'B', 'W', 'F' });
      pos.writeShort((short) 1, IS_BIG_ENDIAN);
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
      pos.writeShort(metadata, IS_BIG_ENDIAN);
      int numVertices = (vertexFormat == null || vertexData == null || vertexFormat.length == 0)
            ? 0
            : vertexData.length / vertexFormat.length;
      pos.writeInt(numVertices, IS_BIG_ENDIAN);
      int numFaces = (faceIndices == null)
            ? 0
            : faceIndices.length / 3;
      pos.writeInt(numFaces, IS_BIG_ENDIAN);
      writeVertices(vertexFormat, numVertices, vertexData, pos);
      writeIndices(numFaces, faceIndices, pos);
      pos.flush();
      byte[] buffer = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      Mesh actualMesh = null;
      try {
         actualMesh = MbwfIO.read(new MeshBuilder(vertexFormat), bais);
      } catch (MeshIOException e) {
         Assert.fail();
      }
      Mesh expectedMesh = new Mesh(vertexFormat, vertexData, faceIndices);
      Assert.assertEquals(expectedMesh, actualMesh);
   }

   private void writeVertices(MeshVertexType[] vertexFormat, int numVertices, float[] vertexData, PrimitiveOutputStream pos) throws IOException {
      for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++) {
         int startIndex = vertexIndex * vertexFormat.length;
         for (int datumIndex = 0; datumIndex < vertexFormat.length; datumIndex++) {
            MeshVertexType type = vertexFormat[datumIndex];
            float datum = vertexData[startIndex + datumIndex];
            writeDatum(datum, type, pos);
         }
      }
   }

   private void writeIndices(int numFaces, int[] indices, PrimitiveOutputStream pos) throws IOException {
      for (int faceIndex = 0; faceIndex < numFaces; faceIndex++) {
         int startIndex = faceIndex * 3;
         for (int datumIndex = 0; datumIndex < 3; datumIndex++)
            pos.writeByte((byte) indices[startIndex + datumIndex]);
      }
   }

   private void writeDatum(float datum, MeshVertexType type, PrimitiveOutputStream pos) throws IOException {
      switch (type) {
      case Position_X:
      case Position_Y:
      case Position_Z:
      case Normal_X:
      case Normal_Y:
      case Normal_Z:
         pos.writeShort((short) (Short.MAX_VALUE * datum), IS_BIG_ENDIAN);
         break;
      case Color_R:
      case Color_G:
      case Color_B:
      case Color_A:
      case TextureCoordinate_U:
      case TextureCoordinate_V:
         pos.writeByte((byte) (Byte.MAX_VALUE * datum));
         break;
      default:
         throw new IOException();
      }
   }

   @Test
   public void testDecodeByte() {
      System.out.println("=====Decoding byte=====");
      System.out.println(MbwfIO.decodeByte((byte) 0x81, true));
      System.out.println(MbwfIO.decodeByte((byte) 0, true));
      System.out.println(MbwfIO.decodeByte((byte) 0x7F, true));
      System.out.println(MbwfIO.decodeByte((byte) 0x81, false));
      System.out.println(MbwfIO.decodeByte((byte) 0, false));
      System.out.println(MbwfIO.decodeByte((byte) 0x7F, false));
   }

   @Test
   public void testDecodeShort() {
      System.out.println("=====Decoding short=====");
      System.out.println(MbwfIO.decodeShort((short) 0x8001, true));
      System.out.println(MbwfIO.decodeShort((short) 0, true));
      System.out.println(MbwfIO.decodeShort((short) 0x7FFF, true));
      System.out.println(MbwfIO.decodeShort((short) 0x8001, false));
      System.out.println(MbwfIO.decodeShort((short) 0, false));
      System.out.println(MbwfIO.decodeShort((short) 0x7FFF, false));
   }

   @Test
   public void testEncodeByte() {
      System.out.println("=====Encoding byte=====");
      System.out.println(MbwfIO.encodeAsByte(-1, true));
      System.out.println(MbwfIO.encodeAsByte(0, true));
      System.out.println(MbwfIO.encodeAsByte(1, true));
      System.out.println(MbwfIO.encodeAsByte(0, false));
      System.out.println(MbwfIO.encodeAsByte(0.5f, false));
      System.out.println(MbwfIO.encodeAsByte(1, false));
   }

   @Test
   public void testEncodeShort() {
      System.out.println("=====Encoding short=====");
      System.out.println(MbwfIO.encodeAsShort(-1, true));
      System.out.println(MbwfIO.encodeAsShort(0, true));
      System.out.println(MbwfIO.encodeAsShort(1, true));
      System.out.println(MbwfIO.encodeAsShort(0, false));
      System.out.println(MbwfIO.encodeAsShort(0.5f, false));
      System.out.println(MbwfIO.encodeAsShort(1, false));
   }
}
