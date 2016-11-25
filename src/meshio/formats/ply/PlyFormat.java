package meshio.formats.ply;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public abstract class PlyFormat {
   private static class Ascii extends PlyFormat {
      private Ascii(String version) {
         super("ascii", version);
      }

      @Override
      public void fillVertexData(PrimitiveInputStream pis, float[] vertexData, PlyDataType vertexType) throws IOException {
         String line = pis.readLine();
         String[] parts = line.split(" ");
         int minSize = Math.min(parts.length, vertexData.length);
         try {
            for (int i = 0; i < minSize; i++)
               vertexData[i] = Float.parseFloat(parts[i]);
         } catch (NumberFormatException e) {
            throw new IOException();
         }
      }

      @Override
      public void writeVertexData(PrimitiveOutputStream pos, float[] vertexData, PlyDataType vertexType) throws IOException {
         StringBuilder sb = new StringBuilder();
         for (float f : vertexData) {
            sb.append(f);
            sb.append(' ');
         }
         if (vertexData.length > 0)
            sb.setLength(sb.length() - 1);
         pos.writeLine(sb.toString());
      }

      @Override
      public int[] readFaceIndices(PrimitiveInputStream pis, PlyDataType countType, PlyDataType indicesType) throws IOException {
         String line = pis.readLine();
         String[] parts = line.split(" ");
         try {
            int numFaceIndices = Integer.parseInt(parts[0]);
            int[] faceIndices = new int[numFaceIndices];
            for (int i = 0; i < numFaceIndices; i++)
               faceIndices[i] = Integer.parseInt(parts[1 + i]);
            return faceIndices;
         } catch (NumberFormatException e) {
            throw new IOException();
         }
      }

      @Override
      public void writeFaceIndices(PrimitiveOutputStream pos, int[] faceIndices, PlyDataType countType, PlyDataType indicesType) throws IOException {
         StringBuilder sb = new StringBuilder();
         sb.append(faceIndices.length);
         if (faceIndices.length > 0) {
            sb.append(' ');
            for (int i : faceIndices) {
               sb.append(i);
               sb.append(' ');
            }
            sb.setLength(sb.length() - 1);
         }
         pos.writeLine(sb.toString());
      }
   }

   private static class Binary extends PlyFormat {
      private final boolean isBigEndian;

      private Binary(boolean isBigEndian, String version) {
         super("binary_" + getEndiannessString(isBigEndian) + "_endian", version);
         this.isBigEndian = isBigEndian;
      }

      private static String getEndiannessString(boolean isBigEndian) {
         return isBigEndian
               ? "big"
               : "little";
      }

      @Override
      public void fillVertexData(PrimitiveInputStream pis, float[] vertexData, PlyDataType vertexType) throws IOException {
         for (int i = 0; i < vertexData.length; i++)
            vertexData[i] = (float) vertexType.readReal(pis, isBigEndian);
      }

      @Override
      public void writeVertexData(PrimitiveOutputStream pos, float[] vertexData, PlyDataType vertexType) throws IOException {
         for (float f : vertexData)
            vertexType.writeReal(pos, isBigEndian, f);
      }

      @Override
      public int[] readFaceIndices(PrimitiveInputStream pis, PlyDataType countType, PlyDataType indicesType) throws IOException {
         int numFaceIndices = (int) countType.readInteger(pis, isBigEndian);
         int[] faceIndices = new int[numFaceIndices];
         for (int i = 0; i < numFaceIndices; i++)
            faceIndices[i] = (int) indicesType.readInteger(pis, isBigEndian);
         return faceIndices;
      }

      @Override
      public void writeFaceIndices(PrimitiveOutputStream pos, int[] faceIndices, PlyDataType countType, PlyDataType indicesType) throws IOException {
         countType.writeInteger(pos, isBigEndian, faceIndices.length);
         for (int i : faceIndices)
            indicesType.writeInteger(pos, isBigEndian, i);
      }
   }

   private static final Map<String, PlyFormat> BY_ENCODING_VERSION      = new HashMap<>();
   public static final PlyFormat               ASCII_1_0                = new Ascii("1.0");
   public static final PlyFormat               BINARY_BIG_ENDIAN_1_0    = new Binary(true, "1.0");
   public static final PlyFormat               BINARY_LITTLE_ENDIAN_1_0 = new Binary(false, "1.0");
   private final String                        encoding;
   private final String                        version;

   private PlyFormat(String encoding, String version) {
      this.encoding = encoding;
      this.version = version;
      BY_ENCODING_VERSION.put(encoding + version, this);
   }

   public String getEncoding() {
      return encoding;
   }

   public String getVersion() {
      return version;
   }

   public static PlyFormat getFrom(String encoding, String version) {
      return BY_ENCODING_VERSION.get(encoding + version);
   }

   public abstract void fillVertexData(PrimitiveInputStream pis, float[] vertexData, PlyDataType vertexType) throws IOException;

   public abstract void writeVertexData(PrimitiveOutputStream pos, float[] vertexData, PlyDataType vertexType) throws IOException;

   public abstract int[] readFaceIndices(PrimitiveInputStream pis, PlyDataType countType, PlyDataType indicesType) throws IOException;

   public abstract void writeFaceIndices(PrimitiveOutputStream pos, int[] faceIndices, PlyDataType countType, PlyDataType indicesType)
         throws IOException;
}
