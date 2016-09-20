package meshio.mbwf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.util.PrimitiveInputStream;
import meshio.util.PrimitiveOutputStream;

public class MbwfIO {
   private static final byte[] MAGIC                 = { 'M', 'B', 'W', 'F' };
   private static final int    USE_Z_INDEX           = 0;
   private static final int    USE_NORMALS_INDEX     = 1;
   private static final int    USE_TEX_COORDS_INDEX  = 2;
   private static final int    USE_COLORS_INDEX      = 3;
   private static final int    USE_COLOR_ALPHA_INDEX = 4;
   private static final int    BITS_PER_BYTE         = 8;
   private static final int    META_DATA_BYTES       = 2;

   public static <T> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
      PrimitiveInputStream pis = null;
      try {
         pis = new PrimitiveInputStream(is);
         readMagic(pis);
         BitSet metadata = readMetaData(pis);
         int numVertices = pis.readInt(true, 4);
         int numFaces = pis.readInt(true, 4);
         readVertices(builder, pis, metadata, numVertices);
         readFaces(builder, pis, metadata, numFaces);
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

   private static BitSet readMetaData(PrimitiveInputStream pis) throws IOException, MeshIOException {
      byte[] metaData = new byte[2];
      pis.read(metaData);
      BitSet bits = new BitSet();
      for (int byteIndex = 0; byteIndex < metaData.length; byteIndex++) {
         byte b = metaData[byteIndex];
         for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            byte mask = (byte) (1 << bitIndex);
            if (1 == (b & mask))
               bits.set(8 * byteIndex + bitIndex);
         }
      }
      return bits;
   }

   private static void readVertices(IMeshBuilder<?> builder, PrimitiveInputStream pis, BitSet metadata, int numVertices)
         throws IOException, MeshIOException {
      // TODO
   }

   private static void readFaces(IMeshBuilder<?> builder, PrimitiveInputStream pis, BitSet metadata, int numFaces)
         throws IOException, MeshIOException {
      // TODO
   }

   public static void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
      if (saver == null)
         throw new MeshIOException("A mesh saver is required", new NullPointerException());
      if (os == null)
         throw new MeshIOException("An output stream is required", new NullPointerException());
      PrimitiveOutputStream pos = null;
      try {
         pos = new PrimitiveOutputStream(os);
         pos.write(MAGIC);
         pos.write(createMetaData(saver).toByteArray());
         pos.writeInt(saver.getVertexCount(), true, 4);
         pos.writeInt(saver.getFaceCount(), true, 4);
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

   private static BitSet createMetaData(IMeshSaver saver) {
      BitSet metaData = new BitSet(META_DATA_BYTES * BITS_PER_BYTE);
      Set<MeshVertexType> usedTypes = new HashSet<>(Arrays.asList(saver.getVertexFormat()));
      if (usedTypes.contains(MeshVertexType.Position_Z))
         metaData.set(USE_Z_INDEX);
      if (usedTypes.contains(MeshVertexType.Normal_X) || usedTypes.contains(MeshVertexType.Normal_Y) || usedTypes.contains(MeshVertexType.Normal_Z))
         metaData.set(USE_NORMALS_INDEX);
      if (usedTypes.contains(MeshVertexType.TextureCoordinate_U) || usedTypes.contains(MeshVertexType.TextureCoordinate_V))
         metaData.set(USE_TEX_COORDS_INDEX);
      if (usedTypes.contains(MeshVertexType.Color_R) || usedTypes.contains(MeshVertexType.Color_G) || usedTypes.contains(MeshVertexType.Color_B))
         metaData.set(USE_COLORS_INDEX);
      if (usedTypes.contains(MeshVertexType.Color_A)) {
         metaData.set(USE_COLORS_INDEX);
         metaData.set(USE_COLOR_ALPHA_INDEX);
      }
      return metaData;
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
               intValue = (int) (value * Short.MAX_VALUE);
               numBytes = 2;
               break;
            case Color_R:
            case Color_G:
            case Color_B:
            case Color_A:
            case TextureCoordinate_U:
            case TextureCoordinate_V:
               intValue = (int) (value * Byte.MAX_VALUE);
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
}
