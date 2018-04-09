package com.ripplargames.meshio.meshformats.obj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.MeshRawData;
import com.ripplargames.meshio.meshformats.AMeshFormat;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.util.StringSplitter;
import com.ripplargames.meshio.vertex.VertexType;

public class ObjFormat extends AMeshFormat {
    @Override
    public String getFileExtension() {
        return "obj";
    }

    @Override
    protected MeshRawData read(PrimitiveInputStream pis) throws IOException, MeshIOException {
        List<float[]> positionColors = new ArrayList<float[]>();
        List<float[]> imageCoords = new ArrayList<float[]>();
        List<float[]> normals = new ArrayList<float[]>();
        Map<VertexDataIndices, Integer> vertexDataVertexIndices = new HashMap<VertexDataIndices, Integer>();
        MeshRawData meshRawData = new MeshRawData();
        readAllData(pis, positionColors, imageCoords, normals, vertexDataVertexIndices, meshRawData);
        buildVertexData(meshRawData, positionColors, imageCoords, normals, vertexDataVertexIndices);
        return meshRawData;
    }

    @Override
    protected void write(MeshRawData meshRawData, PrimitiveOutputStream pos) throws IOException, MeshIOException {
        Set<VertexType> vertexTypes = meshRawData.getVertexTypes();
        boolean isColors = vertexTypes.contains(VertexType.Color_R) && vertexTypes.contains(VertexType.Color_G) && vertexTypes.contains(VertexType.Color_B);
        boolean isImageCoords = vertexTypes.contains(VertexType.ImageCoord_X) && vertexTypes.contains(VertexType.ImageCoord_Y);
        boolean isNormals = vertexTypes.contains(VertexType.Normal_X) && vertexTypes.contains(VertexType.Normal_Y) && vertexTypes.contains(VertexType.Normal_Z);
        writeVertices(meshRawData, pos, isColors, isImageCoords, isNormals);
        writeFaces(meshRawData, pos, isImageCoords, isNormals);
    }

    private static void readAllData(PrimitiveInputStream pis, List<float[]> positionColors, List<float[]> imageCoords, List<float[]> normals, Map<VertexDataIndices, Integer> vertexDataVertexIndices, MeshRawData meshRawData) throws MeshIOException {
        try {
            for (int next = pis.peek(); next != -1; next = pis.peek()) {
                String line = pis.readLine();
                List<String> parts = StringSplitter.splitChar(line, ' ');
                String lineType = parts.get(0);
                if ("#".equals(lineType)) {
                    // empty line or comment - ignore
                } else if ("f".equals(lineType)) {
                    appendFace(parts, meshRawData, vertexDataVertexIndices, imageCoords.size(), normals.size());
                } else {
                    if ("v".equals(lineType)) {
                        positionColors.add(toFloatArrayFromIndex1(parts));
                    } else if ("vn".equals(lineType)) {
                        normals.add(toFloatArrayFromIndex1(parts));
                    } else if ("vt".equals(lineType)) {
                        imageCoords.add(toFloatArrayFromIndex1(parts));
                    }
                }
            }
        } catch (IOException e) {
            // end of file
        }
    }

    private static float[] toFloatArrayFromIndex1(List<String> parts) throws MeshIOException {
        float[] floatArray = new float[3];
        for (int i = 1; i <= 3; i++) {
            String part = parts.get(i);
            try {
                floatArray[i] = Float.parseFloat(part);
            } catch (NumberFormatException e) {
                throw new MeshIOException("Could not parse value from: \"" + part + '\"');
            }
        }
        return floatArray;
    }

    private static void appendFace(List<String> parts, MeshRawData meshRawData, Map<VertexDataIndices, Integer> vertexDataVertexIndices, int currentImageCoordsCount, int currentNormalCount) throws MeshIOException {
        int[] face = new int[3];
        for (int i = 1; i <= 3; i++) {
            String part = parts.get(i);
            List<String> indexParts = StringSplitter.splitChar(part, '/');
            int positionIndex = parseInt(indexParts.get(0));
            int imageCoordIndex = (indexParts.size() < 2) ? 0 : parseInt(indexParts.get(1));
            int normalIndex = (indexParts.size() < 3) ? 0 : parseInt(indexParts.get(2));
            if (imageCoordIndex < 0)
                imageCoordIndex += currentImageCoordsCount;
            if (normalIndex < 0)
                normalIndex += currentNormalCount;
            VertexDataIndices vertexDataIndices = new VertexDataIndices(positionIndex - 1, imageCoordIndex - 1, normalIndex - 1);
            Integer index = vertexDataVertexIndices.get(vertexDataIndices);
            if (index == null) {
                index = vertexDataVertexIndices.size();
                vertexDataVertexIndices.put(vertexDataIndices, index);
            }
            face[i - 1] = index;
        }
        meshRawData.appendFace(face[0], face[1], face[2]);
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

    private static void buildVertexData(MeshRawData meshRawData, List<float[]> positionColors, List<float[]> imageCoords, List<float[]> normals, Map<VertexDataIndices, Integer> vertexDataVertexIndices) {
        for (Map.Entry<VertexDataIndices, Integer> entry : vertexDataVertexIndices.entrySet()) {
            VertexDataIndices vertexDataIndices = entry.getKey();
            int vertexIndex = entry.getValue();
            int positionIndex = vertexDataIndices.positionIndex();
            int imageCoordIndex = vertexDataIndices.imageCoordIndex();
            int normalIndex = vertexDataIndices.normalIndex();
            float[] positionColorData = arrayOrNull(positionIndex, positionColors);
            float[] imageCoordData = arrayOrNull(imageCoordIndex, imageCoords);
            float[] normalData = arrayOrNull(normalIndex, normals);
            if ((positionColorData != null) && (positionColorData.length >= 3)) {
                meshRawData.setVertexTypeDatum(VertexType.Position_X, vertexIndex, positionColorData[0]);
                meshRawData.setVertexTypeDatum(VertexType.Position_Y, vertexIndex, positionColorData[1]);
                meshRawData.setVertexTypeDatum(VertexType.Position_Z, vertexIndex, positionColorData[2]);
                if (positionColorData.length == 6) {
                    meshRawData.setVertexTypeDatum(VertexType.Color_R, vertexIndex, positionColorData[3]);
                    meshRawData.setVertexTypeDatum(VertexType.Color_G, vertexIndex, positionColorData[4]);
                    meshRawData.setVertexTypeDatum(VertexType.Color_B, vertexIndex, positionColorData[5]);
                }
            }
            if ((imageCoordData != null) && (imageCoordData.length == 2)) {
                meshRawData.setVertexTypeDatum(VertexType.ImageCoord_X, vertexIndex, imageCoordData[0]);
                meshRawData.setVertexTypeDatum(VertexType.ImageCoord_Y, vertexIndex, imageCoordData[1]);
            }
            if ((normalData != null) && (normalData.length == 3)) {
                meshRawData.setVertexTypeDatum(VertexType.Normal_X, vertexIndex, normalData[0]);
                meshRawData.setVertexTypeDatum(VertexType.Normal_Y, vertexIndex, normalData[1]);
                meshRawData.setVertexTypeDatum(VertexType.Normal_Z, vertexIndex, normalData[2]);
            }
        }
    }

    private static float[] arrayOrNull(int index, List<float[]> list) {
        return (index < 0)
                ? null
                : list.get(index);
    }

    private static void writeVertices(MeshRawData meshRawData, PrimitiveOutputStream pos, boolean isColors, boolean isImageCoords, boolean isNormals) throws IOException {
        int vertexCount = meshRawData.getVertexCount();
        List<VertexType> positionColorsList = isColors
                ? Arrays.asList(VertexType.Position_X, VertexType.Position_Y, VertexType.Position_Z, VertexType.Color_R, VertexType.Color_G, VertexType.Color_B)
                : Arrays.asList(VertexType.Position_X, VertexType.Position_Y, VertexType.Position_Z);
        writeVertexDataLine(meshRawData, pos, "v", vertexCount, positionColorsList);
        if (isImageCoords)
            writeVertexDataLine(meshRawData, pos, "vt", vertexCount, Arrays.asList(VertexType.ImageCoord_X, VertexType.ImageCoord_Y));
        if (isNormals)
            writeVertexDataLine(meshRawData, pos, "vn", vertexCount, Arrays.asList(VertexType.Normal_X, VertexType.Normal_Y, VertexType.Normal_Z));
    }

    private static void writeVertexDataLine(MeshRawData meshRawData, PrimitiveOutputStream pos, String id, int vertexCount, List<VertexType> vertexTypes) throws IOException {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            StringBuilder sb = new StringBuilder();
            sb.append(id);
            for (VertexType vertexType : vertexTypes) {
                sb.append(' ');
                float datum = meshRawData.getVertexTypeDatum(vertexType, vertexIndex);
                sb.append(datum);
            }
            String line = sb.toString();
            pos.writeLine(line);
        }
    }

    private static void writeFaces(MeshRawData meshRawData, PrimitiveOutputStream pos, boolean isImageCoords, boolean isNormals) throws IOException {
        int faceCount = meshRawData.getFaceCount();
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            StringBuilder sb = new StringBuilder();
            sb.append("f");
            int[] face = meshRawData.getFace(faceIndex);
            for (int faceIndice : face) {
                int faceIndice1Index = faceIndice + 1;
                sb.append(' ');
                sb.append(faceIndice1Index);
                sb.append('/');
                if (isImageCoords)
                    sb.append(faceIndice1Index);
                sb.append('/');
                if (isNormals)
                    sb.append(faceIndice1Index);
            }
            String line = sb.toString();
            pos.writeLine(line);
        }
    }
}
