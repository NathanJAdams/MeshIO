package com.ripplar_games.mesh_io.formats.obj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.MeshFormatBase;
import com.ripplar_games.mesh_io.MeshIOException;
import com.ripplar_games.mesh_io.io.PrimitiveInputStream;
import com.ripplar_games.mesh_io.io.PrimitiveOutputStream;
import com.ripplar_games.mesh_io.mesh.IMesh;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

public class ObjFormat extends MeshFormatBase {
    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");
    private static final Pattern SLASH_PATTERN = Pattern.compile("/");

    @Override
    public String getFileExtension() {
        return "obj";
    }

    @Override
    protected void read(IMeshBuilder<?> builder, PrimitiveInputStream pis) throws IOException, MeshIOException {
        List<float[]> positionColors = new ArrayList<float[]>();
        List<float[]> imageCoords = new ArrayList<float[]>();
        List<float[]> normals = new ArrayList<float[]>();
        Map<VertexDataIndices, Integer> vertexDataVertexIndices = new HashMap<VertexDataIndices, Integer>();
        List<int[]> faces = new ArrayList<int[]>();
        readAllData(pis, positionColors, imageCoords, normals, vertexDataVertexIndices, faces);
        buildCounts(builder, vertexDataVertexIndices, faces);
        buildVertexData(builder, positionColors, imageCoords, normals, vertexDataVertexIndices);
        buildFaceData(builder, faces);
    }

    @Override
    protected void write(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException, MeshIOException {
        writeVertices(saver, pos);
        writeFaces(saver, pos);
    }

    private static void readAllData(PrimitiveInputStream pis, List<float[]> positionColors, List<float[]> imageCoords, List<float[]> normals, Map<VertexDataIndices, Integer> vertexDataVertexIndices, List<int[]> faces) throws IOException, MeshIOException {
        String line;
        do {
            try {
                pis.peek();
            } catch (IOException e) {
                // end of file
                return;
            }
            line = pis.readLine();
            String[] parts = SPACE_PATTERN.split(line);
            String firstPart = parts[0];
            if ((firstPart == null) || "#".equals(firstPart)) {
                // empty line or comment - ignore
            } else if ("f".equals(firstPart)) {
                appendFace(parts, faces, vertexDataVertexIndices, imageCoords.size(), normals.size());
            } else if ("v".equals(firstPart)) {
                positionColors.add(toFloatArrayFromIndex1(parts));
            } else if ("vn".equals(firstPart)) {
                normals.add(toFloatArrayFromIndex1(parts));
            } else if ("vt".equals(firstPart)) {
                imageCoords.add(toFloatArrayFromIndex1(parts));
            }
        } while (line != null);
    }

    private static float[] toFloatArrayFromIndex1(String[] parts) throws MeshIOException {
        float[] floatArray = new float[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            try {
                floatArray[i - 1] = Float.parseFloat(part);
            } catch (NumberFormatException e) {
                throw new MeshIOException("Could not parse value from: \"" + part + '\"');
            }
        }
        return floatArray;
    }

    private static <T extends IMesh> void appendFace(String[] parts, List<int[]> faces, Map<VertexDataIndices, Integer> vertexDataVertexIndices, int currentImageCoordsCount, int currentNormalCount) throws MeshIOException {
        int[] face = new int[3];
        for (int i = 1; i <= 3; i++) {
            String part = parts[i];
            String[] indexParts = SLASH_PATTERN.split(part);
            int positionIndex = parseInt(indexParts[0]);
            int imageCoordIndex = (indexParts.length < 2) ? 0 : parseInt(indexParts[1]);
            int normalIndex = (indexParts.length < 3) ? 0 : parseInt(indexParts[2]);
            if (imageCoordIndex < 0)
                imageCoordIndex += currentImageCoordsCount;
            if (normalIndex < 0)
                normalIndex += currentNormalCount;
            VertexDataIndices vertexDataIndices = new VertexDataIndices(positionIndex, imageCoordIndex, normalIndex);
            Integer index = vertexDataVertexIndices.get(vertexDataIndices);
            if (index == null) {
                index = vertexDataVertexIndices.size();
                vertexDataVertexIndices.put(vertexDataIndices, index);
            }
            face[i - 1] = index;
        }
        faces.add(face);
    }

    private static int parseInt(String s) throws MeshIOException {
        if ((s == null) || s.isEmpty())
            return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new MeshIOException("Could not parse value from: \"" + s + '\"');
        }
    }

    private static void buildCounts(IMeshBuilder<?> builder, Map<VertexDataIndices, Integer> vertexDataVertexIndices, List<int[]> faces) {
        builder.setVertexCount(vertexDataVertexIndices.size());
        builder.setFaceCount(faces.size());
    }

    private static void buildVertexData(IMeshBuilder<?> builder, List<float[]> positionColors, List<float[]> imageCoords, List<float[]> normals, Map<VertexDataIndices, Integer> vertexDataVertexIndices) {
        for (Map.Entry<VertexDataIndices, Integer> entry : vertexDataVertexIndices.entrySet()) {
            VertexDataIndices vertexDataIndices = entry.getKey();
            int vertexIndex = entry.getValue();
            int positionIndex = vertexDataIndices.positionIndex();
            int imageCoordIndex = vertexDataIndices.imageCoordIndex();
            int normalIndex = vertexDataIndices.normalIndex();
            float[] positionColorData = (positionIndex == 0) ? null : positionColors.get(positionIndex);
            float[] imageCoordData = (imageCoordIndex == 0) ? null : imageCoords.get(imageCoordIndex);
            float[] normalData = (normalIndex == 0) ? null : normals.get(normalIndex);
            if ((positionColorData != null) && (positionColorData.length >= 3)) {
                builder.setVertexDatum(vertexIndex, VertexType.Position_X, positionColorData[0]);
                builder.setVertexDatum(vertexIndex, VertexType.Position_Y, positionColorData[1]);
                builder.setVertexDatum(vertexIndex, VertexType.Position_Z, positionColorData[2]);
                if (positionColorData.length == 6) {
                    builder.setVertexDatum(vertexIndex, VertexType.Color_R, positionColorData[3]);
                    builder.setVertexDatum(vertexIndex, VertexType.Color_G, positionColorData[4]);
                    builder.setVertexDatum(vertexIndex, VertexType.Color_B, positionColorData[5]);
                }
            }
            if ((imageCoordData != null) && (imageCoordData.length == 2)) {
                builder.setVertexDatum(vertexIndex, VertexType.ImageCoord_X, imageCoordData[0]);
                builder.setVertexDatum(vertexIndex, VertexType.ImageCoord_Y, imageCoordData[1]);
            }
            if ((normalData != null) && (normalData.length == 3)) {
                builder.setVertexDatum(vertexIndex, VertexType.Normal_X, normalData[0]);
                builder.setVertexDatum(vertexIndex, VertexType.Normal_Y, normalData[1]);
                builder.setVertexDatum(vertexIndex, VertexType.Normal_Z, normalData[2]);
            }
        }
    }

    private static void buildFaceData(IMeshBuilder<?> builder, List<int[]> faces) {
        for (int faceIndex = 0; faceIndex < faces.size(); faceIndex++) {
            int[] face = faces.get(faceIndex);
            builder.setFaceIndices(faceIndex, face);
        }
    }

    private static void writeVertices(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException {
        Set<VertexType> vertexTypes = new HashSet<VertexType>();
        for (VertexFormat format : saver.getVertexFormats()) {
            vertexTypes.addAll(format.getVertexTypes());
        }
        boolean isColors = vertexTypes.contains(VertexType.Color_R) && vertexTypes.contains(VertexType.Color_G) && vertexTypes.contains(VertexType.Color_B);
        boolean isImageCoords = vertexTypes.contains(VertexType.ImageCoord_X) && vertexTypes.contains(VertexType.ImageCoord_Y);
        boolean isNormals = vertexTypes.contains(VertexType.Normal_X) && vertexTypes.contains(VertexType.Normal_Y) && vertexTypes.contains(VertexType.Normal_Z);
        int vertexCount = saver.getVertexCount();
        List<VertexType> positionColorsList = isColors
                ? Arrays.asList(VertexType.Position_X, VertexType.Position_Y, VertexType.Position_Z, VertexType.Color_R, VertexType.Color_G, VertexType.Color_B)
                : Arrays.asList(VertexType.Position_X, VertexType.Position_Y, VertexType.Position_Z);
        writeVertexDataLine(saver, pos, "v", vertexCount, positionColorsList);
        if (isImageCoords)
            writeVertexDataLine(saver, pos, "vt", vertexCount, Arrays.asList(VertexType.ImageCoord_X, VertexType.ImageCoord_Y));
        if (isNormals)
            writeVertexDataLine(saver, pos, "vn", vertexCount, Arrays.asList(VertexType.Normal_X, VertexType.Normal_Y, VertexType.Normal_Z));
    }

    private static void writeVertexDataLine(IMeshSaver saver, PrimitiveOutputStream pos, String id, int vertexCount, List<VertexType> vertexTypes) throws IOException {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            StringBuilder sb = new StringBuilder();
            sb.append(id);
            for (VertexType vertexType : vertexTypes) {
                sb.append(' ');
                float datum = saver.getVertexDatum(vertexIndex, vertexType);
                sb.append(datum);
            }
            String line = sb.toString();
            pos.writeLine(line);
        }
    }

    private static void writeFaces(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException {
        int faceCount = saver.getFaceCount();
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            StringBuilder sb = new StringBuilder();
            sb.append("f");
            int[] face = saver.getFaceIndices(faceIndex);
            for (int faceIndice : face) {
                sb.append(' ');
                sb.append(faceIndice);
            }
            String line = sb.toString();
            pos.writeLine(line);
        }
    }
}
