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
   private static final byte[] MAGIC                = { 'M', 'B', 'W', 'F' };
   private static final int    USE_Z_MASK           = 1 << 15;
   private static final int    USE_NORMALS_MASK     = 1 << 14;
   private static final int    USE_TEX_COORDS_MASK  = 1 << 13;
   private static final int    USE_COLORS_MASK      = 1 << 12;
   private static final int    USE_COLOR_ALPHA_MASK = 1 << 11;

   public static <T> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
      PrimitiveInputStream pis = null;
      try {
         pis = new PrimitiveInputStream(is);
         readMagic(pis);
         int metadata = pis.readInt(true, 2);
         int vertexCount = pis.readInt(true, 4);
         builder.setVertexCount(vertexCount);
         int faceCount = pis.readInt(true, 4);
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
         builder.setVertexDatum(vertexIndex, MeshVertexType.Position_X, decodeShort((pis.readInt(true, 2))));
         builder.setVertexDatum(vertexIndex, MeshVertexType.Position_Y, decodeShort((pis.readInt(true, 2))));
         if (isPositionZ)
            builder.setVertexDatum(vertexIndex, MeshVertexType.Position_Z, decodeShort((pis.readInt(true, 2))));
         if (isNormals) {
            builder.setVertexDatum(vertexIndex, MeshVertexType.Normal_X, decodeShort((pis.readInt(true, 2))));
            builder.setVertexDatum(vertexIndex, MeshVertexType.Normal_Y, decodeShort((pis.readInt(true, 2))));
            if (isPositionZ)
               builder.setVertexDatum(vertexIndex, MeshVertexType.Normal_Z, decodeShort((pis.readInt(true, 2))));
         }
         if (isTexCoords) {
            builder.setVertexDatum(vertexIndex, MeshVertexType.TextureCoordinate_U, decodeByte((pis.readInt(true, 1))));
            builder.setVertexDatum(vertexIndex, MeshVertexType.TextureCoordinate_V, decodeByte((pis.readInt(true, 1))));
         }
         if (isColors) {
            builder.setVertexDatum(vertexIndex, MeshVertexType.Color_R, decodeByte((pis.readInt(true, 1))));
            builder.setVertexDatum(vertexIndex, MeshVertexType.Color_G, decodeByte((pis.readInt(true, 1))));
            builder.setVertexDatum(vertexIndex, MeshVertexType.Color_B, decodeByte((pis.readInt(true, 1))));
            if (isColorAlpha)
               builder.setVertexDatum(vertexIndex, MeshVertexType.Color_A, decodeByte((pis.readInt(true, 1))));
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
            faceIndices[vertexIndex] = pis.readInt(true, numBytes);
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
      int metaData = 0;
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
      pos.writeInt(metaData, true, 2);
      pos.writeInt(saver.getVertexCount(), true, 4);
      pos.writeInt(saver.getFaceCount(), true, 4);
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
            int intValue;
            int numBytes;
            switch (vertexType) {
            case Position_X:
            case Position_Y:
            case Position_Z:
            case Normal_X:
            case Normal_Y:
            case Normal_Z:
               intValue = encodeAsShort(value);
               numBytes = 2;
               break;
            case Color_R:
            case Color_G:
            case Color_B:
            case Color_A:
            case TextureCoordinate_U:
            case TextureCoordinate_V:
               intValue = encodeAsByte(value);
               numBytes = 1;
               break;
            default:
               throw new MeshIOException("Unknown vertex type: " + vertexType.name());
            }
            pos.writeInt(intValue, true, numBytes);
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
            pos.writeInt(faceIndices[vertexIndex], true, numBytes);
      }
   }

   private static int encodeAsByte(float decoded) {
      return (int) (decoded * Byte.MAX_VALUE);
   }

   private static int encodeAsShort(float decoded) {
      return (int) (decoded * Short.MAX_VALUE);
   }

   private static float decodeByte(int encoded) {
      return (float) ((double) encoded / Byte.MAX_VALUE);
   }

   private static float decodeShort(int encoded) {
      return (float) ((double) encoded / Short.MAX_VALUE);
   }
}
