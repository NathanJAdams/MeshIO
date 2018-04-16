package com.ripplargames.meshio.meshformats.ply;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.Mesh;
import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.meshformats.AMeshFormat;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.util.StringSplitter;
import com.ripplargames.meshio.vertices.VertexType;

public abstract class PlyFormat extends AMeshFormat {
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
    private static final String VERTEX_INDICES = "vertex_indices";
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

    private void readVertices(PrimitiveInputStream pis, Mesh mesh, List<PlyVertexDataType> plyVertexDataTypes, int numVertices) throws IOException {
        for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++) {
            float[] vertexData = readVertexData(pis, plyVertexDataTypes);
            for (int plyVertexDataTypeIndex = 0; plyVertexDataTypeIndex < plyVertexDataTypes.size(); plyVertexDataTypeIndex++) {
                VertexType vertexType = plyVertexDataTypes.get(plyVertexDataTypeIndex).vertexType();
                float vertexDatum = vertexData[plyVertexDataTypeIndex];
                mesh.setVertexTypeDatum(vertexType, vertexIndex, vertexDatum);
            }
        }
    }

    private void readFaces(PrimitiveInputStream pis, Mesh mesh, PlyDataType countType, PlyDataType indexType,
                           int numFaces) throws IOException {
        for (int faceIndex = 0; faceIndex < numFaces; faceIndex++) {
            int[] faceIndices = readFaceIndices(pis, countType, indexType);
            mesh.appendFace(new Face(faceIndices[0], faceIndices[1], faceIndices[2]));
        }
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
    protected Mesh read(PrimitiveInputStream pis) throws IOException, MeshIOException {
        String line;
        line = readNonCommentLine(pis);
        if (!PLY.equals(line))
            throw new MeshIOException("Unrecognised magic: " + line + ". \"ply\" expected");
        line = readNonCommentLine(pis);
        String[] formatParts = line.split(" ");
        if (formatParts.length != 3 || !FORMAT.equals(formatParts[0]))
            throw new MeshIOException("Unrecognised format: " + line);
        PlyFormat actualPlyFormat = getFrom(formatParts[1], formatParts[2]);
        if (actualPlyFormat == null)
            throw new MeshIOException("Unrecognised encoding-version combination: " + line);
        boolean isVerticesFirst = true;
        boolean isVertexHeader = false;
        boolean isFaceHeader = false;
        List<PlyVertexDataType> plyVertexDataTypes = new ArrayList<PlyVertexDataType>();
        int numVertices = -1;
        int numFaces = -1;
        PlyDataType faceIndexCountType = null;
        PlyDataType faceIndexType = null;
        while (true) {
            line = readNonCommentLine(pis);
            if (END_HEADER.equals(line))
                break;
            List<String> lineParts = StringSplitter.splitChar(line, ' ');
            if (lineParts.size() == 3 && ELEMENT.equals(lineParts.get(0))) {
                int parsedCount = Integer.parseInt(lineParts.get(2));
                if (VERTEX.equals(lineParts.get(1))) {
                    isVertexHeader = true;
                    isFaceHeader = false;
                    numVertices = parsedCount;
                } else if (FACE.equals(lineParts.get(1))) {
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
            } else if (lineParts.size() == 3 && PROPERTY.equals(lineParts.get(0))) {
                if (isVertexHeader) {
                    PlyDataType dataType = PlyDataType.getDataType(lineParts.get(1));
                    VertexType vertexType = PROPERTIES_BY_NAME.get(lineParts.get(2));
                    if (dataType == null || vertexType == null)
                        throw new MeshIOException("Unrecognised vertex property: " + lineParts.get(1) + " - " + lineParts.get(2));
                    plyVertexDataTypes.add(new PlyVertexDataType(vertexType, dataType));
                }
            } else if (lineParts.size() == 5 && isFaceHeader && PROPERTY.equals(lineParts.get(0)) && LIST.equals(lineParts.get(1))
                    && (VERTEX_INDEX.equals(lineParts.get(4)) || VERTEX_INDICES.equals(lineParts.get(4)))) {
                faceIndexCountType = PlyDataType.getDataType(lineParts.get(2));
                faceIndexType = PlyDataType.getDataType(lineParts.get(3));
            }
        }
        if (numVertices == -1)
            throw new MeshIOException("Failed to read vertex data");
        if (numFaces == -1 || faceIndexCountType == null || faceIndexType == null)
            throw new MeshIOException("Failed to read face indices");
        Mesh mesh = new Mesh();
        if (isVerticesFirst) {
            actualPlyFormat.readVertices(pis, mesh, plyVertexDataTypes, numVertices);
            actualPlyFormat.readFaces(pis, mesh, faceIndexCountType, faceIndexType, numFaces);
        } else {
            actualPlyFormat.readFaces(pis, mesh, faceIndexCountType, faceIndexType, numFaces);
            actualPlyFormat.readVertices(pis, mesh, plyVertexDataTypes, numVertices);
        }
        return mesh;
    }

    @Override
    protected void write(Mesh mesh, PrimitiveOutputStream pos) throws IOException, MeshIOException {
        pos.writeLine(PLY);
        pos.writeLine(FORMAT + ' ' + encoding + ' ' + version);
        int vertexCount = mesh.vertexCount();
        int faceCount = mesh.faceCount();
        pos.writeLine("element vertex " + vertexCount);
        List<VertexType> vertexTypes = new ArrayList<VertexType>();
        for (VertexType vertexType : VertexType.values()) {
            if (mesh.hasVertexTypeData(vertexType)) {
                vertexTypes.add(vertexType);
            }
        }
        for (VertexType vertexType : vertexTypes)
            pos.writeLine("property float " + PROPERTY_NAMES.get(vertexType));
        pos.writeLine("element face " + faceCount);
        pos.writeLine("property list uchar int vertex_index");
        pos.writeLine(END_HEADER);
        int vertexTypeCount = vertexTypes.size();
        float[] vertexData = new float[vertexTypeCount];
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            for (int vertexTypeIndex = 0; vertexTypeIndex < vertexTypes.size(); vertexTypeIndex++) {
                VertexType vertexType = vertexTypes.get(vertexTypeIndex);
                float datum = mesh.vertexTypeDatum(vertexType, vertexIndex);
                vertexData[vertexTypeIndex] = datum;
            }
            writeVertexData(pos, PlyDataType.Float, vertexData);
        }
        for (Face face : mesh.faces()) {
            writeFaceIndices(pos, face, PlyDataType.Uchar, PlyDataType.Int);
        }
    }

    public abstract float[] readVertexData(PrimitiveInputStream pis, List<PlyVertexDataType> plyVertexDataTypes) throws IOException;

    public abstract void writeVertexData(PrimitiveOutputStream pos, PlyDataType vertexType, float[] vertexData) throws IOException;

    public abstract int[] readFaceIndices(PrimitiveInputStream pis, PlyDataType countType, PlyDataType indicesType) throws IOException;

    public abstract void writeFaceIndices(PrimitiveOutputStream pos, Face face, PlyDataType countType, PlyDataType indicesType)
            throws IOException;
}
