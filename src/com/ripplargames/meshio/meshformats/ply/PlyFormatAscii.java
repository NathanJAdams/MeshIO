package com.ripplargames.meshio.meshformats.ply;

import java.io.IOException;
import java.util.List;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;

public class PlyFormatAscii extends PlyFormat {
    public PlyFormatAscii(String version) {
        super("ascii", version);
    }

    @Override
    public float[] readVertexData(PrimitiveInputStream pis, List<PlyVertexDataType> plyVertexDataTypes) throws IOException {
        float[] vertexData = new float[plyVertexDataTypes.size()];
        String line = pis.readLine();
        String[] parts = line.split(" ");
        int minSize = Math.min(parts.length, vertexData.length);
        try {
            for (int i = 0; i < minSize; i++)
                vertexData[i] = Float.parseFloat(parts[i]);
        } catch (NumberFormatException e) {
            throw new IOException("Failed to read number");
        }
        return vertexData;
    }

    @Override
    public void writeVertexData(PrimitiveOutputStream pos, PlyDataType vertexType, float[] vertexData) throws IOException {
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
            throw new IOException("Failed to read number");
        }
    }

    @Override
    public void writeFaceIndices(PrimitiveOutputStream pos, Face face, PlyDataType countType, PlyDataType indicesType) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(3);
        sb.append(' ');
        sb.append(face.getV0());
        sb.append(' ');
        sb.append(face.getV1());
        sb.append(' ');
        sb.append(face.getV2());
        pos.writeLine(sb.toString());
    }
}
