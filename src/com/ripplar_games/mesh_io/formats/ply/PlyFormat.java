package com.ripplar_games.mesh_io.formats.ply;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshFormat;
import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.MeshIOException;
import com.ripplar_games.mesh_io.io.PrimitiveInputStream;
import com.ripplar_games.mesh_io.io.PrimitiveOutputStream;
import com.ripplar_games.mesh_io.mesh.IMesh;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

public abstract class PlyFormat implements IMeshFormat {
    private static final Logger LOGGER = Logger.getLogger("PlyFormat");
    private static final Map<String, PlyFormat> BY_ENCODING_VERSION = new HashMap<String, PlyFormat>();
    private static final String PLY = "ply";
    private static final String FORMAT = "format";
    private static final String COMMENT = "comment";
    private static final String ELEMENT = "element";
    private static final String VERTEX = "vertex";
    private static final String FACE = "face";
    private static final String PROPERTY = "property";
    private static final String LIST = "list";
    private static final String VERTEX_INDEX = "vertex_index";
    private static final String END_HEADER = "end_header";
    private static final Map<VertexType, String> PROPERTY_NAMES = new HashMap<VertexType, String>();
    private static final Map<String, VertexType> PROPERTIES_BY_NAME = new HashMap<String, VertexType>();
    private static final String PROPERTY_POSITION_X_NAME = "x";
    private static final String PROPERTY_POSITION_Y_NAME = "y";
    private static final String PROPERTY_POSITION_Z_NAME = "z";
    private static final String PROPERTY_NORMAL_X_NAME = "nx";
    private static final String PROPERTY_NORMAL_Y_NAME = "ny";
    private static final String PROPERTY_NORMAL_Z_NAME = "nz";
    private static final String PROPERTY_COLOR_R_NAME = "red";
    private static final String PROPERTY_COLOR_G_NAME = "green";
    private static final String PROPERTY_COLOR_B_NAME = "blue";
    private static final String PROPERTY_COLOR_A_NAME = "alpha";
    private static final String PROPERTY_TEXTURE_COORDINATE_U_NAME = "u";
    private static final String PROPERTY_TEXTURE_COORDINATE_V_NAME = "v";

    static {
        addPropertyNameMapping(VertexType.Position_X, PROPERTY_POSITION_X_NAME);
        addPropertyNameMapping(VertexType.Position_Y, PROPERTY_POSITION_Y_NAME);
        addPropertyNameMapping(VertexType.Position_Z, PROPERTY_POSITION_Z_NAME);
        addPropertyNameMapping(VertexType.Normal_X, PROPERTY_NORMAL_X_NAME);
        addPropertyNameMapping(VertexType.Normal_Y, PROPERTY_NORMAL_Y_NAME);
        addPropertyNameMapping(VertexType.Normal_Z, PROPERTY_NORMAL_Z_NAME);
        addPropertyNameMapping(VertexType.Color_R, PROPERTY_COLOR_R_NAME);
        addPropertyNameMapping(VertexType.Color_G, PROPERTY_COLOR_G_NAME);
        addPropertyNameMapping(VertexType.Color_B, PROPERTY_COLOR_B_NAME);
        addPropertyNameMapping(VertexType.Color_A, PROPERTY_COLOR_A_NAME);
        addPropertyNameMapping(VertexType.ImageCoord_X, PROPERTY_TEXTURE_COORDINATE_U_NAME);
        addPropertyNameMapping(VertexType.ImageCoord_Y, PROPERTY_TEXTURE_COORDINATE_V_NAME);
    }

    private final String encoding;
    private final String version;

    public PlyFormat(String encoding, String version) {
        this.encoding = encoding;
        this.version = version;
        BY_ENCODING_VERSION.put(encoding + version, this);
    }

    private static void addPropertyNameMapping(VertexType type, String name) {
        PROPERTIES_BY_NAME.put(name, type);
        PROPERTY_NAMES.put(type, name);
    }

    private static void readVertices(PlyFormat plyFormat, PrimitiveInputStream pis, IMeshBuilder<?> builder, List<VertexType> readVertexFormat,
                                     List<PlyDataType> vertexDataTypes, int numVertices) throws IOException {
        float[] readVertexData = new float[readVertexFormat.size()];
        for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++) {
            plyFormat.fillVertexData(pis, readVertexData, vertexDataTypes.get(vertexIndex));
            for (int vertexFormatIndex = 0; vertexFormatIndex < readVertexFormat.size(); vertexFormatIndex++) {
                VertexType readType = readVertexFormat.get(vertexFormatIndex);
                float vertexDatum = readVertexData[vertexFormatIndex];
                builder.setVertexDatum(vertexIndex, readType, vertexDatum);
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

    public static PlyFormat getFrom(String encoding, String version) {
        return BY_ENCODING_VERSION.get(encoding + version);
    }

    public static String getPropertyName(VertexType vertexType) {
        return PROPERTY_NAMES.get(vertexType);
    }

    public String getEncoding() {
        return encoding;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getFileExtension() {
        return "ply";
    }

    @Override
    public <T extends IMesh> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
        builder.clear();
        try {
            PrimitiveInputStream pis = new PrimitiveInputStream(is);
            String line;
            line = readNonCommentLine(pis);
            if (!PLY.equals(line))
                throw new MeshIOException("Unrecognised magic: " + line + ". \"ply\" expected");
            line = readNonCommentLine(pis);
            String[] formatParts = line.split(" ");
            if (formatParts.length != 3 || !FORMAT.equals(formatParts[0]))
                throw new MeshIOException("Unrecognised format: " + line);
            PlyFormat plyFormat = getFrom(formatParts[1], formatParts[2]);
            if (plyFormat == null)
                throw new MeshIOException("Unrecognised encoding-version combination: " + line);
            boolean isVerticesFirst = true;
            boolean isVertexHeader = false;
            boolean isFaceHeader = false;
            List<VertexType> vertexTypes = new ArrayList<VertexType>();
            List<PlyDataType> vertexDataTypes = new ArrayList<PlyDataType>();
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
                        VertexType vertexType = PROPERTIES_BY_NAME.get(lineParts[2]);
                        if (dataType == null || vertexType == null)
                            throw new MeshIOException("Unrecognised vertex property: " + lineParts[1] + " - " + lineParts[2]);
                        vertexDataTypes.add(dataType);
                        vertexTypes.add(vertexType);
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
                readVertices(plyFormat, pis, builder, vertexTypes, vertexDataTypes, numVertices);
                readFaces(plyFormat, pis, builder, faceIndexCountType, faceIndexType, numFaces);
            } else {
                readFaces(plyFormat, pis, builder, faceIndexCountType, faceIndexType, numFaces);
                readVertices(plyFormat, pis, builder, vertexTypes, vertexDataTypes, numVertices);
            }
            return builder.build();
        } catch (IOException e) {
            throw new MeshIOException("Exception when reading from stream", e);
        }
    }

    @Override
    public void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
        if (saver == null)
            throw new MeshIOException("A mesh saver is required", new NullPointerException());
        if (os == null)
            throw new MeshIOException("An output stream is required", new NullPointerException());
        PrimitiveOutputStream pos = new PrimitiveOutputStream(os);
        try {
            pos.writeLine(PLY);
            pos.writeLine(FORMAT + ' ' + encoding + ' ' + version);
            pos.writeLine("element vertex " + saver.getVertexCount());
            List<VertexType> vertexTypes = getVertexTypes(saver);
            for (VertexType vertexType : vertexTypes)
                pos.writeLine("property float " + PROPERTY_NAMES.get(vertexType));
            pos.writeLine("element face " + saver.getFaceCount());
            pos.writeLine("property list uchar int vertex_index");
            pos.writeLine(END_HEADER);
            int vertexTypeCount = vertexTypes.size();
            float[] vertexData = new float[vertexTypeCount];
            for (int vertexIndex = 0; vertexIndex < saver.getVertexCount(); vertexIndex++) {
                for (int vertexTypeIndex = 0; vertexTypeIndex < vertexTypes.size(); vertexTypeIndex++) {
                    VertexType vertexType = vertexTypes.get(vertexTypeIndex);
                    float datum = saver.getVertexDatum(vertexIndex, vertexType);
                    vertexData[vertexTypeIndex] = datum;
                }
                writeVertexData(pos, vertexData, PlyDataType.Float);
            }
            for (int i = 0; i < saver.getFaceCount(); i++) {
                int[] faceIndices = saver.getFaceIndices(i);
                writeFaceIndices(pos, faceIndices, PlyDataType.Uchar, PlyDataType.Int);
            }
        } catch (IOException ioe) {
            throw new MeshIOException("Exception when writing to stream", ioe);
        } finally {
            try {
                pos.flush();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to properly write data");
            }
        }
    }

    private List<VertexType> getVertexTypes(IMeshSaver saver) {
        List<VertexType> vertexTypes = new ArrayList<VertexType>();
        for (VertexFormat format : saver.getVertexFormats()) {
            vertexTypes.addAll(format.getVertexTypes());
        }
        return vertexTypes;
    }

    public abstract void fillVertexData(PrimitiveInputStream pis, float[] vertexData, PlyDataType vertexType) throws IOException;

    public abstract void writeVertexData(PrimitiveOutputStream pos, float[] vertexData, PlyDataType vertexType) throws IOException;

    public abstract int[] readFaceIndices(PrimitiveInputStream pis, PlyDataType countType, PlyDataType indicesType) throws IOException;

    public abstract void writeFaceIndices(PrimitiveOutputStream pos, int[] faceIndices, PlyDataType countType, PlyDataType indicesType)
            throws IOException;
}
