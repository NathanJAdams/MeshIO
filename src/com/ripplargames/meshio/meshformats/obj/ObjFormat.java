package com.ripplargames.meshio.meshformats.obj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.Mesh;
import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.meshformats.AMeshFormat;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.util.StringSplitter;
import com.ripplargames.meshio.vertices.VertexType;

public class ObjFormat extends AMeshFormat {
    @Override
    public String getFileExtension() {
        return "obj";
    }

    @Override
    protected Mesh read(PrimitiveInputStream pis) throws IOException, MeshIOException {
        List<float[]> positionColors = new ArrayList<float[]>();
        List<float[]> imageCoords = new ArrayList<float[]>();
        List<float[]> normals = new ArrayList<float[]>();
        Map<VertexDataIndices, Integer> vertexDataVertexIndices = new HashMap<VertexDataIndices, Integer>();
        Mesh mesh = new Mesh();
        readAllDataAndAddFaces(pis, positionColors, imageCoords, normals, vertexDataVertexIndices, mesh);
        addVertices(mesh, positionColors, imageCoords, normals, vertexDataVertexIndices);
        return mesh;
    }

    @Override
    protected void write(Mesh mesh, PrimitiveOutputStream pos) throws IOException, MeshIOException {
        Set<VertexType> vertexTypes = mesh.vertexTypes();
        boolean isColors = vertexTypes.contains(VertexType.Color_R) && vertexTypes.contains(VertexType.Color_G) && vertexTypes.contains(VertexType.Color_B);
        boolean isImageCoords = vertexTypes.contains(VertexType.ImageCoord_X) && vertexTypes.contains(VertexType.ImageCoord_Y);
        boolean isNormals = vertexTypes.contains(VertexType.Normal_X) && vertexTypes.contains(VertexType.Normal_Y) && vertexTypes.contains(VertexType.Normal_Z);
        writeVertices(mesh, pos, isColors, isImageCoords, isNormals);
        writeFaces(mesh, pos, isImageCoords, isNormals);
    }

    private static void readAllDataAndAddFaces(PrimitiveInputStream pis, List<float[]> positionColors, List<float[]> imageCoords, List<float[]> normals, Map<VertexDataIndices, Integer> vertexDataVertexIndices, Mesh mesh) throws MeshIOException {
        try {
            for (int next = pis.peek(); next != -1; next = pis.peek()) {
                String line = pis.readLine();
                List<String> parts = StringSplitter.splitChar(line, ' ');
                String lineType = parts.get(0);
                if ("#".equals(lineType)) {
                    // empty line or comment - ignore
                } else if ("f".equals(lineType)) {
                    appendFace(parts, mesh, vertexDataVertexIndices, imageCoords.size(), normals.size());
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
        int numParts = parts.size() - 1;
        float[] floatArray = new float[numParts];
        for (int i = 0; i < numParts; i++) {
            String part = parts.get(i + 1);
            try {
                floatArray[i] = Float.parseFloat(part);
            } catch (NumberFormatException e) {
                throw new MeshIOException("Could not parse value from: \"" + part + '\"');
            }
        }
        return floatArray;
    }

    private static void appendFace(List<String> parts, Mesh mesh, Map<VertexDataIndices, Integer> vertexDataVertexIndices, int currentImageCoordsCount, int currentNormalCount) throws MeshIOException {
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
        mesh.appendFace(new Face(face[0], face[1], face[2]));
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

    private static void addVertices(Mesh mesh, List<float[]> positionColors, List<float[]> imageCoords, List<float[]> normals, Map<VertexDataIndices, Integer> vertexDataVertexIndices) {
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
                mesh.setVertexTypeDatum(VertexType.Position_X, vertexIndex, positionColorData[0]);
                mesh.setVertexTypeDatum(VertexType.Position_Y, vertexIndex, positionColorData[1]);
                mesh.setVertexTypeDatum(VertexType.Position_Z, vertexIndex, positionColorData[2]);
                if (positionColorData.length >= 6) {
                    mesh.setVertexTypeDatum(VertexType.Color_R, vertexIndex, positionColorData[3]);
                    mesh.setVertexTypeDatum(VertexType.Color_G, vertexIndex, positionColorData[4]);
                    mesh.setVertexTypeDatum(VertexType.Color_B, vertexIndex, positionColorData[5]);
                    if (positionColorData.length == 7) {
                        mesh.setVertexTypeDatum(VertexType.Color_A, vertexIndex, positionColorData[6]);
                    }
                }
            }
            if ((imageCoordData != null) && (imageCoordData.length == 2)) {
                mesh.setVertexTypeDatum(VertexType.ImageCoord_X, vertexIndex, imageCoordData[0]);
                mesh.setVertexTypeDatum(VertexType.ImageCoord_Y, vertexIndex, imageCoordData[1]);
            }
            if ((normalData != null) && (normalData.length == 3)) {
                mesh.setVertexTypeDatum(VertexType.Normal_X, vertexIndex, normalData[0]);
                mesh.setVertexTypeDatum(VertexType.Normal_Y, vertexIndex, normalData[1]);
                mesh.setVertexTypeDatum(VertexType.Normal_Z, vertexIndex, normalData[2]);
            }
        }
    }

    private static float[] arrayOrNull(int index, List<float[]> list) {
        return (index < 0)
                ? null
                : list.get(index);
    }

    private static void writeVertices(Mesh mesh, PrimitiveOutputStream pos, boolean isColors, boolean isImageCoords, boolean isNormals) throws IOException {
        int vertexCount = mesh.vertexCount();
        List<VertexType> positionColorsList = new ArrayList<VertexType>();
        positionColorsList.add(VertexType.Position_X);
        positionColorsList.add(VertexType.Position_Y);
        positionColorsList.add(VertexType.Position_Z);
        if (isColors) {
            positionColorsList.add(VertexType.Color_R);
            positionColorsList.add(VertexType.Color_G);
            positionColorsList.add(VertexType.Color_B);
            if (mesh.hasVertexTypeData(VertexType.Color_A)) {
                positionColorsList.add(VertexType.Color_A);
            }
        }
        writeVertexDataLine(mesh, pos, "v", vertexCount, positionColorsList);
        if (isImageCoords)
            writeVertexDataLine(mesh, pos, "vt", vertexCount, Arrays.asList(VertexType.ImageCoord_X, VertexType.ImageCoord_Y));
        if (isNormals)
            writeVertexDataLine(mesh, pos, "vn", vertexCount, Arrays.asList(VertexType.Normal_X, VertexType.Normal_Y, VertexType.Normal_Z));
    }

    private static void writeVertexDataLine(Mesh mesh, PrimitiveOutputStream pos, String id, int vertexCount, List<VertexType> vertexTypes) throws IOException {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            StringBuilder sb = new StringBuilder();
            sb.append(id);
            for (VertexType vertexType : vertexTypes) {
                sb.append(' ');
                float datum = mesh.vertexTypeDatum(vertexType, vertexIndex);
                sb.append(datum);
            }
            String line = sb.toString();
            pos.writeLine(line);
        }
    }

    private static void writeFaces(Mesh mesh, PrimitiveOutputStream pos, boolean isImageCoords, boolean isNormals) throws IOException {
        for (Face face : mesh.faces()) {
            StringBuilder sb = new StringBuilder();
            sb.append("f");
            writeFaceIndice(sb, face.getV0(), isImageCoords, isNormals);
            writeFaceIndice(sb, face.getV1(), isImageCoords, isNormals);
            writeFaceIndice(sb, face.getV2(), isImageCoords, isNormals);
            String line = sb.toString();
            pos.writeLine(line);
        }
    }

    private static void writeFaceIndice(StringBuilder sb, int faceIndice, boolean isImageCoords, boolean isNormals) {
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
}
