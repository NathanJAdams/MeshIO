package meshio.formats.mbwf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumMap;

import meshio.IMeshBuilder;
import meshio.IMeshFormat;
import meshio.IMeshSaver;
import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.io.PrimitiveInputStream;
import meshio.io.PrimitiveOutputStream;
import meshio.mesh.IMesh;

public class MbwfFormat implements IMeshFormat {
   private static final boolean IS_BIG_ENDIAN        = true;
   private static final byte[]  MAGIC                = { 'M', 'B', 'W', 'F' };
   private static final short   MAX_VERSION          = 1;
   private static final int     IS_3D_MASK           = 1 << 15;
   private static final int     IS_NORMALS_MASK      = 1 << 14;
   private static final int     IS_IMAGE_COORDS_MASK = 1 << 13;
   private static final int     IS_COLORS_MASK       = 1 << 12;
   private static final int     IS_COLOR_ALPHA_MASK  = 1 << 11;

   @Override
   public String getFileExtension() {
      return "mbwf";
   }

   @Override
   public <T extends IMesh> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
      PrimitiveInputStream pis = null;
      try {
         pis = new PrimitiveInputStream(is);
         readMagic(pis);
         short version = pis.readShort(IS_BIG_ENDIAN);
         return readWithVersion(builder, pis, version);
      } catch (IOException ioe) {
         throw new MeshIOException("Exception when reading from stream", ioe);
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

   private static <T extends IMesh> T readWithVersion(IMeshBuilder<T> builder, PrimitiveInputStream pis, short version) throws IOException {
      short metadata = pis.readShort(IS_BIG_ENDIAN);
      readVertices(builder, pis, metadata);
      readFaces(builder, pis, metadata);
      return builder.build();
   }

   private static void readVertices(IMeshBuilder<?> builder, PrimitiveInputStream pis, short metadata) throws IOException {
      int vertexCount = pis.readInt(IS_BIG_ENDIAN);
      builder.setVertexCount(vertexCount);
      boolean is3D = (metadata & IS_3D_MASK) != 0;
      boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
      boolean isImageCoords = (metadata & IS_IMAGE_COORDS_MASK) != 0;
      boolean isColors = (metadata & IS_COLORS_MASK) != 0;
      boolean isColorAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
      MeshVertexType[] vertexFormat = builder.getVertexFormat();
      EnumMap<MeshVertexType, Integer> typeIndexes = MeshVertexType.createTypeIndexes(vertexFormat);
      float[] vertexData = new float[vertexFormat.length];
      for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
         readShorts(vertexData, typeIndexes, pis, true, MeshVertexType.Position_X, MeshVertexType.Position_Y);
         if (is3D)
            readShorts(vertexData, typeIndexes, pis, true, MeshVertexType.Position_Z);
         if (isNormals) {
            readShorts(vertexData, typeIndexes, pis, true, MeshVertexType.Normal_X, MeshVertexType.Normal_Y);
            if (is3D)
               readShorts(vertexData, typeIndexes, pis, true, MeshVertexType.Normal_Z);
         }
         if (isImageCoords) {
            readBytes(vertexData, typeIndexes, pis, false, MeshVertexType.ImageCoord_X, MeshVertexType.ImageCoord_Y);
         }
         if (isColors) {
            readBytes(vertexData, typeIndexes, pis, false, MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B);
            if (isColorAlpha)
               readBytes(vertexData, typeIndexes, pis, false, MeshVertexType.Color_A);
         }
         builder.setVertexData(vertexIndex, vertexData);
      }
   }

   private static void readShorts(float[] vertexData, EnumMap<MeshVertexType, Integer> typeIndexes, PrimitiveInputStream pis,
         boolean areNegativesUsed, MeshVertexType... types) throws IOException {
      for (int i = 0; i < types.length; i++) {
         float value = DatumEnDecode.decodeShort(pis.readShort(IS_BIG_ENDIAN), areNegativesUsed);
         setVertexDatum(vertexData, typeIndexes, types[i], value);
      }
   }

   private static void readBytes(float[] vertexData, EnumMap<MeshVertexType, Integer> typeIndexes, PrimitiveInputStream pis, boolean areNegativesUsed,
         MeshVertexType... types) throws IOException {
      for (int i = 0; i < types.length; i++) {
         float value = DatumEnDecode.decodeByte(pis.readByte(), areNegativesUsed);
         setVertexDatum(vertexData, typeIndexes, types[i], value);
      }
   }

   private static void setVertexDatum(float[] vertexData, EnumMap<MeshVertexType, Integer> typeIndexes, MeshVertexType type, float value) {
      Integer indexObject = typeIndexes.get(type);
      if (indexObject != null)
         vertexData[indexObject] = value;
   }

   private static void readFaces(IMeshBuilder<?> builder, PrimitiveInputStream pis, int metadata) throws IOException {
      int faceCount = pis.readInt(IS_BIG_ENDIAN);
      builder.setFaceCount(faceCount);
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

   @Override
   public void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
      if (saver == null)
         throw new MeshIOException("A mesh saver is required", new NullPointerException());
      if (os == null)
         throw new MeshIOException("An output stream is required", new NullPointerException());
      PrimitiveOutputStream pos = null;
      try {
         pos = new PrimitiveOutputStream(os);
         MeshVertexType[] format = saver.getVertexFormat();
         EnumMap<MeshVertexType, Integer> typeIndexes = MeshVertexType.createTypeIndexes(format);
         short metadata = createMetadata(typeIndexes);
         writeHeader(pos, metadata);
         writeVertices(saver, pos, metadata, typeIndexes);
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

   private static short createMetadata(EnumMap<MeshVertexType, Integer> typeIndexes) throws MeshIOException {
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

   private static void writeHeader(PrimitiveOutputStream pos, short metadata) throws IOException {
      pos.write(MAGIC);
      pos.writeShort(MAX_VERSION, IS_BIG_ENDIAN);
      pos.writeShort(metadata, IS_BIG_ENDIAN);
   }

   private static void writeVertices(IMeshSaver saver, PrimitiveOutputStream pos, short metadata, EnumMap<MeshVertexType, Integer> typeIndexes)
         throws IOException, MeshIOException {
      int vertexCount = saver.getVertexCount();
      pos.writeInt(vertexCount, IS_BIG_ENDIAN);
      boolean is3D = (metadata & IS_3D_MASK) != 0;
      boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
      boolean isImages = (metadata & IS_IMAGE_COORDS_MASK) != 0;
      boolean isColors = (metadata & IS_COLORS_MASK) != 0;
      boolean isAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
      float[] vertexData = new float[typeIndexes.size()];
      for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
         saver.getVertexData(vertexIndex, vertexData);
         writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Position_X, MeshVertexType.Position_Y);
         if (is3D)
            writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Position_Z);
         if (isNormals) {
            writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Normal_X, MeshVertexType.Normal_Y);
            if (is3D)
               writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Normal_Z);
         }
         if (isImages)
            writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.ImageCoord_X, MeshVertexType.ImageCoord_Y);
         if (isColors) {
            writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B);
            if (isAlpha)
               writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.ImageCoord_X, MeshVertexType.Color_A);
         }
      }
   }

   private static void writeVertexData(PrimitiveOutputStream pos, float[] vertexData, EnumMap<MeshVertexType, Integer> typeIndexes,
         MeshVertexType... types) throws IOException, MeshIOException {
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
            pos.writeShort(DatumEnDecode.encodeAsShort(value, true), IS_BIG_ENDIAN);
            break;
         case Color_R:
         case Color_G:
         case Color_B:
         case Color_A:
         case ImageCoord_X:
         case ImageCoord_Y:
            pos.writeByte(DatumEnDecode.encodeAsByte(value, false));
            break;
         default:
            throw new MeshIOException("Unknown vertex type: " + type.name());
         }
      }
   }

   private static void writeFaces(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException, MeshIOException {
      int faceCount = saver.getFaceCount();
      pos.writeInt(faceCount, IS_BIG_ENDIAN);
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
         saver.getFaceIndices(faceIndex, faceIndices);
         for (int vertexIndex = 0; vertexIndex < faceIndices.length; vertexIndex++)
            pos.writeLong(faceIndices[vertexIndex], IS_BIG_ENDIAN, numBytes);
      }
   }
}
