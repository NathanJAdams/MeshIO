package meshio.mbwf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.util.PrimitiveInputStream;
import meshio.util.PrimitiveOutputStream;

public class MbwfIO {
   private static final boolean IS_BIG_ENDIAN        = true;
   private static final byte[]  MAGIC                = { 'M', 'B', 'W', 'F' };
   private static final int     USE_Z_MASK           = 1 << 15;
   private static final int     USE_NORMALS_MASK     = 1 << 14;
   private static final int     USE_TEX_COORDS_MASK  = 1 << 13;
   private static final int     USE_COLORS_MASK      = 1 << 12;
   private static final int     USE_COLOR_ALPHA_MASK = 1 << 11;

   public static <T> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
      PrimitiveInputStream pis = null;
      try {
         pis = new PrimitiveInputStream(is);
         readMagic(pis);
         short metadata = pis.readShort(IS_BIG_ENDIAN);
         int vertexCount = pis.readInt(IS_BIG_ENDIAN);
         builder.setVertexCount(vertexCount);
         int faceCount = pis.readInt(IS_BIG_ENDIAN);
         builder.setFaceCount(faceCount);
         readVertices(builder, pis, metadata, vertexCount);
         readFaces(builder, pis, metadata, faceCount);
         return builder.build();
      } catch (IOException ioe) {
         throw new MeshIOException("Exception when reading from stream", ioe);
      } finally {
         if (pis != null)
            try {
               pis.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
      }
   }

   private static void readMagic(PrimitiveInputStream pis) throws IOException, MeshIOException {
      byte[] magicBytes = new byte[4];
      pis.read(magicBytes);
      if (!Arrays.equals(MAGIC, magicBytes)) {
         StringBuilder sb = new StringBuilder();
         sb.append("Unrecognised magic: \"");
         for (byte b : magicBytes)
            sb.append((char) b);
         sb.append("\". Expected \"");
         for (byte b : MAGIC)
            sb.append((char) b);
         sb.append('"');
         throw new MeshIOException(sb.toString());
      }
   }

   private static void readVertices(IMeshBuilder<?> builder, PrimitiveInputStream pis, int metadata, int vertexCount)
         throws IOException, MeshIOException {
      boolean isPositionZ = (metadata & USE_Z_MASK) != 0;
      boolean isNormals = (metadata & USE_NORMALS_MASK) != 0;
      boolean isTexCoords = (metadata & USE_TEX_COORDS_MASK) != 0;
      boolean isColors = (metadata & USE_COLORS_MASK) != 0;
      boolean isColorAlpha = (metadata & USE_COLOR_ALPHA_MASK) != 0;
      for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
         builder.setVertexDatum(vertexIndex, MeshVertexType.Position_X, decodeShort(pis.readShort(IS_BIG_ENDIAN), true));
         builder.setVertexDatum(vertexIndex, MeshVertexType.Position_Y, decodeShort(pis.readShort(IS_BIG_ENDIAN), true));
         if (isPositionZ)
            builder.setVertexDatum(vertexIndex, MeshVertexType.Position_Z, decodeShort(pis.readShort(IS_BIG_ENDIAN), true));
         if (isNormals) {
            builder.setVertexDatum(vertexIndex, MeshVertexType.Normal_X, decodeShort(pis.readShort(IS_BIG_ENDIAN), true));
            builder.setVertexDatum(vertexIndex, MeshVertexType.Normal_Y, decodeShort(pis.readShort(IS_BIG_ENDIAN), true));
            if (isPositionZ)
               builder.setVertexDatum(vertexIndex, MeshVertexType.Normal_Z, decodeShort(pis.readShort(IS_BIG_ENDIAN), true));
         }
         if (isTexCoords) {
            builder.setVertexDatum(vertexIndex, MeshVertexType.TextureCoordinate_U, decodeByte(pis.readByte(), false));
            builder.setVertexDatum(vertexIndex, MeshVertexType.TextureCoordinate_V, decodeByte(pis.readByte(), false));
         }
         if (isColors) {
            builder.setVertexDatum(vertexIndex, MeshVertexType.Color_R, decodeByte(pis.readByte(), false));
            builder.setVertexDatum(vertexIndex, MeshVertexType.Color_G, decodeByte(pis.readByte(), false));
            builder.setVertexDatum(vertexIndex, MeshVertexType.Color_B, decodeByte(pis.readByte(), false));
            if (isColorAlpha)
               builder.setVertexDatum(vertexIndex, MeshVertexType.Color_A, decodeByte(pis.readByte(), false));
         }
      }
   }

   private static void readFaces(IMeshBuilder<?> builder, PrimitiveInputStream pis, int metadata, int faceCount) throws IOException, MeshIOException {
      int numBytes;
      if (faceCount <= 256)
         numBytes = 1;
      else if (faceCount <= 256 * 256)
         numBytes = 2;
      else if (faceCount <= 256 * 256 * 256)
         numBytes = 3;
      else
         numBytes = 4;
      int[] faceIndices = new int[3];
      for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
         for (int vertexIndex = 0; vertexIndex < faceIndices.length; vertexIndex++)
            faceIndices[vertexIndex] = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
         builder.setFaceIndices(faceIndex, faceIndices);
      }
   }

   public static void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
      if (saver == null)
         throw new MeshIOException("A mesh saver is required", new NullPointerException());
      if (os == null)
         throw new MeshIOException("An output stream is required", new NullPointerException());
      PrimitiveOutputStream pos = null;
      try {
         pos = new PrimitiveOutputStream(os);
         writeHeader(saver, pos);
         writeVertices(saver, pos);
         writeFaces(saver, pos);
      } catch (IOException ioe) {
         throw new MeshIOException("Exception when writing to stream", ioe);
      } finally {
         if (pos != null)
            try {
               pos.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
      }
   }

   private static void writeHeader(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException {
      pos.write(MAGIC);
      Set<MeshVertexType> usedTypes = new HashSet<>(Arrays.asList(saver.getVertexFormat()));
      short metaData = 0;
      if (usedTypes.contains(MeshVertexType.Position_Z))
         metaData |= USE_Z_MASK;
      if (usedTypes.contains(MeshVertexType.Normal_X) || usedTypes.contains(MeshVertexType.Normal_Y) || usedTypes.contains(MeshVertexType.Normal_Z))
         metaData |= USE_NORMALS_MASK;
      if (usedTypes.contains(MeshVertexType.TextureCoordinate_U) || usedTypes.contains(MeshVertexType.TextureCoordinate_V))
         metaData |= USE_TEX_COORDS_MASK;
      if (usedTypes.contains(MeshVertexType.Color_R) || usedTypes.contains(MeshVertexType.Color_G) || usedTypes.contains(MeshVertexType.Color_B))
         metaData |= USE_COLORS_MASK;
      if (usedTypes.contains(MeshVertexType.Color_A)) {
         metaData |= USE_COLORS_MASK;
         metaData |= USE_COLOR_ALPHA_MASK;
      }
      pos.writeShort(metaData, IS_BIG_ENDIAN);
      pos.writeInt(saver.getVertexCount(), IS_BIG_ENDIAN);
      pos.writeInt(saver.getFaceCount(), IS_BIG_ENDIAN);
   }

   private static void writeVertices(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException, MeshIOException {
      int vertexCount = saver.getVertexCount();
      MeshVertexType[] format = saver.getVertexFormat();
      float[] vertexData = new float[format.length];
      for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
         saver.fillVertexData(vertexIndex, vertexData);
         for (int vertexTypeIndex = 0; vertexTypeIndex < format.length; vertexTypeIndex++) {
            MeshVertexType vertexType = format[vertexTypeIndex];
            float value = vertexData[vertexTypeIndex];
            switch (vertexType) {
            case Position_X:
            case Position_Y:
            case Position_Z:
            case Normal_X:
            case Normal_Y:
            case Normal_Z:
               pos.writeShort(encodeAsShort(value, true), IS_BIG_ENDIAN);
               break;
            case Color_R:
            case Color_G:
            case Color_B:
            case Color_A:
            case TextureCoordinate_U:
            case TextureCoordinate_V:
               pos.writeByte(encodeAsByte(value, false));
               break;
            default:
               throw new MeshIOException("Unknown vertex type: " + vertexType.name());
            }
         }
      }
   }

   private static void writeFaces(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException, MeshIOException {
      int faceCount = saver.getFaceCount();
      int numBytes;
      if (faceCount <= 256)
         numBytes = 1;
      else if (faceCount <= 256 * 256)
         numBytes = 2;
      else if (faceCount <= 256 * 256 * 256)
         numBytes = 3;
      else
         numBytes = 4;
      int[] faceIndices = new int[3];
      for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
         saver.fillFaceIndices(faceIndex, faceIndices);
         for (int vertexIndex = 0; vertexIndex < faceIndices.length; vertexIndex++)
            pos.writeLong(faceIndices[vertexIndex], IS_BIG_ENDIAN, numBytes);
      }
   }

   public static byte encodeAsByte(float decoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (byte) (decoded * Byte.MAX_VALUE)
            : (byte) ((int) (decoded * 0xFE) - Byte.MAX_VALUE);
   }

   public static short encodeAsShort(float decoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (short) (decoded * Short.MAX_VALUE)
            : (short) ((int) (decoded * 0xFFFE) - Short.MAX_VALUE);
   }

   public static float decodeByte(byte encoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (float) ((double) encoded / Byte.MAX_VALUE)
            : (float) ((double) (encoded + Byte.MAX_VALUE) / 0xFE);
   }

   public static float decodeShort(short encoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (float) ((double) encoded / Short.MAX_VALUE)
            : (float) ((double) (encoded + Short.MAX_VALUE) / 0xFFFE);
   }
}
