package meshio.mbwf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.util.PrimitiveInputStream;
import meshio.util.PrimitiveOutputStream;

public class MbwfIO {
   private static final boolean IS_BIG_ENDIAN        = true;
   private static final byte[]  MAGIC                = { 'M', 'B', 'W', 'F' };
   private static final short   MAX_VERSION          = 1;
   private static final int     IS_3D_MASK           = 1 << 15;
   private static final int     IS_NORMALS_MASK      = 1 << 14;
   private static final int     IS_IMAGE_COORDS_MASK = 1 << 13;
   private static final int     IS_COLORS_MASK       = 1 << 12;
   private static final int     IS_COLOR_ALPHA_MASK  = 1 << 11;

   public static <T> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
      PrimitiveInputStream pis = null;
      try {
         pis = new PrimitiveInputStream(is);
         readMagic(pis);
         short version = pis.readShort(IS_BIG_ENDIAN);
         switch (version) {
         case 1:
            return readVersion1(builder, pis);
         default:
            throw new MeshIOException("Unknown version: " + version);
         }
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

   private static <T> T readVersion1(IMeshBuilder<T> builder, PrimitiveInputStream pis) throws IOException, MeshIOException {
      short metadata = pis.readShort(IS_BIG_ENDIAN);
      int vertexCount = pis.readInt(IS_BIG_ENDIAN);
      builder.setVertexCount(vertexCount);
      int faceCount = pis.readInt(IS_BIG_ENDIAN);
      builder.setFaceCount(faceCount);
      readVertices(builder, pis, metadata, vertexCount);
      readFaces(builder, pis, metadata, faceCount);
      return builder.build();
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
      boolean isPositionZ = (metadata & IS_3D_MASK) != 0;
      boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
      boolean isImageCoords = (metadata & IS_IMAGE_COORDS_MASK) != 0;
      boolean isColors = (metadata & IS_COLORS_MASK) != 0;
      boolean isColorAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
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
         if (isImageCoords) {
            builder.setVertexDatum(vertexIndex, MeshVertexType.ImageCoord_X, decodeByte(pis.readByte(), false));
            builder.setVertexDatum(vertexIndex, MeshVertexType.ImageCoord_Y, decodeByte(pis.readByte(), false));
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
         MeshVertexType[] format = saver.getVertexFormat();
         int vertexCount = saver.getVertexCount();
         int faceCount = saver.getFaceCount();
         Map<MeshVertexType, Integer> typeIndexes = createTypeIndexes(format);
         short metadata = createMetadata(typeIndexes);
         writeHeader(pos, metadata, vertexCount, faceCount);
         writeVertices(saver, pos, metadata, vertexCount, format, typeIndexes);
         writeFaces(saver, pos, faceCount);
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

   private static Map<MeshVertexType, Integer> createTypeIndexes(MeshVertexType[] format) {
      Map<MeshVertexType, Integer> typeIndexes = new HashMap<>();
      for (int i = 0; i < format.length; i++)
         typeIndexes.put(format[i], i);
      return typeIndexes;
   }

   private static short createMetadata(Map<MeshVertexType, Integer> typeIndexes) throws MeshIOException {
      if (!typeIndexes.containsKey(MeshVertexType.Position_X) || !typeIndexes.containsKey(MeshVertexType.Position_Y))
         throw new MeshIOException("No position data found");
      short metaData = 0;
      boolean is3D = typeIndexes.containsKey(MeshVertexType.Position_Z);
      if (is3D)
         metaData |= IS_3D_MASK;
      if (typeIndexes.containsKey(MeshVertexType.Normal_X) && typeIndexes.containsKey(MeshVertexType.Normal_Y)
            && (!is3D || typeIndexes.containsKey(MeshVertexType.Normal_Z)))
         metaData |= IS_NORMALS_MASK;
      if (typeIndexes.containsKey(MeshVertexType.ImageCoord_X) && typeIndexes.containsKey(MeshVertexType.ImageCoord_Y))
         metaData |= IS_IMAGE_COORDS_MASK;
      if (typeIndexes.containsKey(MeshVertexType.Color_R) && typeIndexes.containsKey(MeshVertexType.Color_G)
            && typeIndexes.containsKey(MeshVertexType.Color_B)) {
         metaData |= IS_COLORS_MASK;
         if (typeIndexes.containsKey(MeshVertexType.Color_A))
            metaData |= IS_COLOR_ALPHA_MASK;
      }
      return metaData;
   }

   private static void writeHeader(PrimitiveOutputStream pos, short metadata, int vertexCount, int faceCount) throws IOException {
      pos.write(MAGIC);
      pos.writeShort(MAX_VERSION, IS_BIG_ENDIAN);
      pos.writeShort(metadata, IS_BIG_ENDIAN);
      pos.writeInt(vertexCount, IS_BIG_ENDIAN);
      pos.writeInt(faceCount, IS_BIG_ENDIAN);
   }

   private static void writeVertices(IMeshSaver saver, PrimitiveOutputStream pos, short metadata, int vertexCount, MeshVertexType[] format,
         Map<MeshVertexType, Integer> typeIndexes) throws IOException, MeshIOException {
      boolean is3D = (metadata & IS_3D_MASK) != 0;
      boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
      boolean isImages = (metadata & IS_IMAGE_COORDS_MASK) != 0;
      boolean isColors = (metadata & IS_COLORS_MASK) != 0;
      boolean isAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
      float[] vertexData = new float[format.length];
      for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
         saver.fillVertexData(vertexIndex, vertexData);
         writeValues(pos, vertexData, typeIndexes, MeshVertexType.Position_X, MeshVertexType.Position_Y);
         if (is3D)
            writeValues(pos, vertexData, typeIndexes, MeshVertexType.Position_Z);
         if (isNormals)
            writeValues(pos, vertexData, typeIndexes, MeshVertexType.Normal_X, MeshVertexType.Normal_Y);
         if (isNormals && is3D)
            writeValues(pos, vertexData, typeIndexes, MeshVertexType.Normal_Z);
         if (isImages)
            writeValues(pos, vertexData, typeIndexes, MeshVertexType.ImageCoord_X, MeshVertexType.ImageCoord_Y);
         if (isColors)
            writeValues(pos, vertexData, typeIndexes, MeshVertexType.ImageCoord_X, MeshVertexType.Color_R, MeshVertexType.Color_G,
                  MeshVertexType.Color_B);
         if (isAlpha)
            writeValues(pos, vertexData, typeIndexes, MeshVertexType.ImageCoord_X, MeshVertexType.Color_A);
      }
   }

   private static void writeValues(PrimitiveOutputStream pos, float[] vertexData, Map<MeshVertexType, Integer> typeIndexes, MeshVertexType... types)
         throws IOException, MeshIOException {
      for (int i = 0; i < types.length; i++) {
         MeshVertexType type = types[i];
         int index = typeIndexes.get(type);
         float value = vertexData[index];
         switch (type) {
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
         case ImageCoord_X:
         case ImageCoord_Y:
            pos.writeByte(encodeAsByte(value, false));
            break;
         default:
            throw new MeshIOException("Unknown vertex type: " + type.name());
         }
      }
   }

   private static void writeFaces(IMeshSaver saver, PrimitiveOutputStream pos, int faceCount) throws IOException, MeshIOException {
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
