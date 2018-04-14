package com.ripplargames.meshio.meshformats.ply;

import java.io.IOException;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;

public class PlyFormatBinary extends PlyFormat {
    private final boolean isBigEndian;

    public PlyFormatBinary(boolean isBigEndian, String version) {
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
    public void writeFaceIndices(PrimitiveOutputStream pos, Face face, PlyDataType countType, PlyDataType indicesType) throws IOException {
        countType.writeInteger(pos, isBigEndian, 3);
        indicesType.writeInteger(pos, isBigEndian, face.getV0());
        indicesType.writeInteger(pos, isBigEndian, face.getV1());
        indicesType.writeInteger(pos, isBigEndian, face.getV2());
    }
}
