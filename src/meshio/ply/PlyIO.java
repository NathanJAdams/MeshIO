package meshio.ply;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.util.PrimitiveInputStream;
import meshio.util.PrimitiveOutputStream;

public class PlyIO {
   private static final String                      PLY                                = "ply";
   private static final String                      FORMAT                             = "format";
   private static final String                      COMMENT                            = "comment";
   private static final String                      ELEMENT                            = "element";
   private static final String                      VERTEX                             = "vertex";
   private static final String                      FACE                               = "face";
   private static final String                      PROPERTY                           = "property";
   private static final String                      LIST                               = "list";
   private static final String                      VERTEX_INDEX                       = "vertex_index";
   private static final String                      END_HEADER                         = "end_header";
   private static final Map<MeshVertexType, String> PROPERTY_NAMES                     = new HashMap<>();
   private static final Map<String, MeshVertexType> PROPERTIES_BY_NAME                 = new HashMap<>();
   private static final String                      PROPERTY_POSITION_X_NAME           = "x";
   private static final String                      PROPERTY_POSITION_Y_NAME           = "y";
   private static final String                      PROPERTY_POSITION_Z_NAME           = "z";
   private static final String                      PROPERTY_NORMAL_X_NAME             = "nx";
   private static final String                      PROPERTY_NORMAL_Y_NAME             = "ny";
   private static final String                      PROPERTY_NORMAL_Z_NAME             = "nz";
   private static final String                      PROPERTY_COLOR_R_NAME              = "red";
   private static final String                      PROPERTY_COLOR_G_NAME              = "green";
   private static final String                      PROPERTY_COLOR_B_NAME              = "blue";
   private static final String                      PROPERTY_COLOR_A_NAME              = "alpha";
   private static final String                      PROPERTY_TEXTURE_COORDINATE_U_NAME = "u";
   private static final String                      PROPERTY_TEXTURE_COORDINATE_V_NAME = "v";

   static {
      addPropertyNameMapping(MeshVertexType.Position_X, PROPERTY_POSITION_X_NAME);
      addPropertyNameMapping(MeshVertexType.Position_Y, PROPERTY_POSITION_Y_NAME);
      addPropertyNameMapping(MeshVertexType.Position_Z, PROPERTY_POSITION_Z_NAME);
      addPropertyNameMapping(MeshVertexType.Normal_X, PROPERTY_NORMAL_X_NAME);
      addPropertyNameMapping(MeshVertexType.Normal_Y, PROPERTY_NORMAL_Y_NAME);
      addPropertyNameMapping(MeshVertexType.Normal_Z, PROPERTY_NORMAL_Z_NAME);
      addPropertyNameMapping(MeshVertexType.Color_R, PROPERTY_COLOR_R_NAME);
      addPropertyNameMapping(MeshVertexType.Color_G, PROPERTY_COLOR_G_NAME);
      addPropertyNameMapping(MeshVertexType.Color_B, PROPERTY_COLOR_B_NAME);
      addPropertyNameMapping(MeshVertexType.Color_A, PROPERTY_COLOR_A_NAME);
      addPropertyNameMapping(MeshVertexType.ImageCoord_X, PROPERTY_TEXTURE_COORDINATE_U_NAME);
      addPropertyNameMapping(MeshVertexType.ImageCoord_Y, PROPERTY_TEXTURE_COORDINATE_V_NAME);
   }

   private static void addPropertyNameMapping(MeshVertexType type, String name) {
      PROPERTIES_BY_NAME.put(name, type);
      PROPERTY_NAMES.put(type, name);
   }

   public static <T> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
      PrimitiveInputStream pis = null;
      try {
         pis = new PrimitiveInputStream(is);
         String line;
         line = readNonCommentLine(pis);
         if (!PLY.equals(line))
            throw new MeshIOException("Unrecognised magic: " + line + ". \"ply\" expected");
         line = readNonCommentLine(pis);
         String[] formatParts = line.split(" ");
         if (formatParts.length != 3 || !FORMAT.equals(formatParts[0]))
            throw new MeshIOException("Unrecognised format: " + line);
         PlyFormat plyFormat = PlyFormat.getFrom(formatParts[1], formatParts[2]);
         if (plyFormat == null)
            throw new MeshIOException("Unrecognised encoding-version combination: " + line);
         boolean isVerticesFirst = true;
         boolean isVertexHeader = false;
         boolean isFaceHeader = false;
         List<MeshVertexType> vertexFormat = new ArrayList<>();
         List<PlyDataType> vertexDataTypes = new ArrayList<>();
         int numVertices = -1;
         int numFaces = -1;
         PlyDataType faceIndexCountType = null;
         PlyDataType faceIndexType = null;
         while (true) {
            line = readNonCommentLine(pis);
            if (END_HEADER.equals(line))
               break;
            String[] lineParts = line.split(" ");
            if (lineParts.length == 3 && ELEMENT.equals(lineParts[0])) {
               int parsedCount = Integer.parseInt(lineParts[2]);
               if (VERTEX.equals(lineParts[1])) {
                  isVertexHeader = true;
                  isFaceHeader = false;
                  numVertices = parsedCount;
               } else if (FACE.equals(lineParts[1])) {
                  if (!isVertexHeader)
                     isVerticesFirst = false;
                  isVertexHeader = false;
                  isFaceHeader = true;
                  numFaces = parsedCount;
               } else {
                  // other headers are ignored
                  isVertexHeader = false;
                  isFaceHeader = false;
               }
            } else if (lineParts.length == 3 && PROPERTY.equals(lineParts[0])) {
               if (isVertexHeader) {
                  PlyDataType dataType = PlyDataType.getDataType(lineParts[1]);
                  MeshVertexType vertexType = PROPERTIES_BY_NAME.get(lineParts[2]);
                  if (dataType == null || vertexType == null)
                     throw new MeshIOException("Unrecognised vertex property: " + lineParts[1] + " - " + lineParts[2]);
                  vertexDataTypes.add(dataType);
                  vertexFormat.add(vertexType);
               }
            } else if (lineParts.length == 5 && isFaceHeader && PROPERTY.equals(lineParts[0]) && LIST.equals(lineParts[1])
                  && VERTEX_INDEX.equals(lineParts[4])) {
               faceIndexCountType = PlyDataType.getDataType(lineParts[2]);
               faceIndexType = PlyDataType.getDataType(lineParts[3]);
            }
         }
         if (numVertices == -1)
            throw new MeshIOException("Failed to read vertex data");
         if (numFaces == -1 || faceIndexCountType == null || faceIndexType == null)
            throw new MeshIOException("Failed to read face indices");
         builder.setVertexCount(numVertices);
         builder.setFaceCount(numFaces);
         if (isVerticesFirst) {
            readVertices(plyFormat, pis, builder, vertexFormat, vertexDataTypes, numVertices);
            readFaces(plyFormat, pis, builder, faceIndexCountType, faceIndexType, numFaces);
         } else {
            readFaces(plyFormat, pis, builder, faceIndexCountType, faceIndexType, numFaces);
            readVertices(plyFormat, pis, builder, vertexFormat, vertexDataTypes, numVertices);
         }
         return builder.build();
      } catch (IOException e) {
         throw new MeshIOException("Exception when reading from stream", e);
      } finally {
         if (pis != null)
            try {
               pis.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
      }
   }

   private static void readVertices(PlyFormat plyFormat, PrimitiveInputStream pis, IMeshBuilder<?> builder, List<MeshVertexType> vertexFormat,
         List<PlyDataType> vertexDataTypes, int numVertices) throws IOException {
      float[] vertexData = new float[vertexFormat.size()];
      for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++) {
         plyFormat.fillVertexData(pis, vertexData, vertexDataTypes.get(vertexIndex));
         for (int vertexDatumIndex = 0; vertexDatumIndex < vertexData.length; vertexDatumIndex++) {
            MeshVertexType vertexDatumType = vertexFormat.get(vertexDatumIndex);
            float datum = vertexData[vertexDatumIndex];
            builder.setVertexDatum(vertexIndex, vertexDatumType, datum);
         }
      }
   }

   private static void readFaces(PlyFormat plyFormat, PrimitiveInputStream pis, IMeshBuilder<?> builder, PlyDataType countType, PlyDataType indexType,
         int numFaces) throws IOException {
      for (int faceIndex = 0; faceIndex < numFaces; faceIndex++)
         builder.setFaceIndices(faceIndex, plyFormat.readFaceIndices(pis, countType, indexType));
   }

   private static String readNonCommentLine(PrimitiveInputStream pis) throws IOException {
      String line;
      do {
         line = pis.readLine();
      } while (line != null && line.startsWith(COMMENT));
      return line;
   }

   public static void write(IMeshSaver saver, OutputStream os, PlyFormat plyFormat) throws MeshIOException {
      if (saver == null)
         throw new MeshIOException("A mesh saver is required", new NullPointerException());
      if (os == null)
         throw new MeshIOException("An output stream is required", new NullPointerException());
      PrimitiveOutputStream pos = null;
      try {
         pos = new PrimitiveOutputStream(os);
         pos.writeLine(PLY);
         pos.writeLine(FORMAT + ' ' + plyFormat.getEncoding() + ' ' + plyFormat.getVersion());
         pos.writeLine("element vertex " + saver.getVertexCount());
         MeshVertexType[] vertexFormat = saver.getVertexFormat();
         if (vertexFormat != null)
            for (MeshVertexType meshVertexType : vertexFormat)
               pos.writeLine("property float " + PROPERTY_NAMES.get(meshVertexType));
         pos.writeLine("element face " + saver.getFaceCount());
         pos.writeLine("property list uchar int vertex_index");
         pos.writeLine(END_HEADER);
         if (vertexFormat != null) {
            float[] vertexData = new float[vertexFormat.length];
            for (int i = 0; i < saver.getVertexCount(); i++) {
               saver.fillVertexData(i, vertexData);
               plyFormat.writeVertexData(pos, vertexData, PlyDataType.Float);
            }
         }
         int[] faceIndices = new int[3];
         for (int i = 0; i < saver.getFaceCount(); i++) {
            saver.fillFaceIndices(i, faceIndices);
            plyFormat.writeFaceIndices(pos, faceIndices, PlyDataType.Uchar, PlyDataType.Int);
         }
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

   public static String getPropertyName(MeshVertexType meshVertexType) {
      return PROPERTY_NAMES.get(meshVertexType);
   }
}
