package com.ripplargames.meshio.meshformats.mbmsh;

import java.io.IOException;
import java.util.Arrays;

import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.MeshRawData;
import com.ripplargames.meshio.meshformats.AMeshFormat;
import com.ripplargames.meshio.util.DatumEnDecoder;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.vertex.VertexType;

public class MbMshFormat extends AMeshFormat {
    private static final boolean IS_BIG_ENDIAN = true;
    private static final byte[] MAGIC = {'M', 'B', 'M', 'S', 'H'};
    private static final short MAX_VERSION = 1;
    private static final int IS_3D_MASK = 1 << 15;
    private static final int IS_NORMALS_MASK = 1 << 14;
    private static final int IS_IMAGE_COORDS_MASK = 1 << 13;
    private static final int IS_COLORS_MASK = 1 << 12;
    private static final int IS_ALPHA_MASK = 1 << 11;

    @Override
    public String getFileExtension() {
        return "mbmsh";
    }

    @Override
    protected MeshRawData read(PrimitiveInputStream pis) throws IOException, MeshIOException {
        readMagic(pis);
        short version = pis.readShort(IS_BIG_ENDIAN);
        short metadata = pis.readShort(IS_BIG_ENDIAN);
        MeshRawData meshRawData = new MeshRawData();
        readVertices(meshRawData, pis, version, metadata);
        readFaces(meshRawData, pis, version, metadata);
        return meshRawData;
    }

    @Override
    protected void write(MeshRawData meshRawData, PrimitiveOutputStream pos) throws IOException, MeshIOException {
        short metadata = createMetadata(meshRawData);
        writeHeader(pos, metadata);
        writeVertices(meshRawData, pos, metadata);
        writeFaces(meshRawData, pos);
    }

    private static void readMagic(PrimitiveInputStream pis) throws IOException, MeshIOException {
        byte[] magicBytes = new byte[MAGIC.length];
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

    private static void readVertices(MeshRawData meshRawData, PrimitiveInputStream pis, short version, short metadata) throws IOException {
        int vertexCount = pis.readInt(IS_BIG_ENDIAN);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImageCoords = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isAlpha = (metadata & IS_ALPHA_MASK) != 0;
        readSignedShorts(meshRawData, vertexCount, pis, VertexType.Position_X);
        readSignedShorts(meshRawData, vertexCount, pis, VertexType.Position_Y);
        if (is3D)
            readSignedShorts(meshRawData, vertexCount, pis, VertexType.Position_Z);
        if (isNormals) {
            readSignedShorts(meshRawData, vertexCount, pis, VertexType.Normal_X);
            readSignedShorts(meshRawData, vertexCount, pis, VertexType.Normal_Y);
            if (is3D)
                readSignedShorts(meshRawData, vertexCount, pis, VertexType.Normal_Z);
        }
        if (isImageCoords) {
            readUnsignedBytes(meshRawData, vertexCount, pis, VertexType.ImageCoord_X);
            readUnsignedBytes(meshRawData, vertexCount, pis, VertexType.ImageCoord_Y);
        }
        if (isColors) {
            readUnsignedBytes(meshRawData, vertexCount, pis, VertexType.Color_R);
            readUnsignedBytes(meshRawData, vertexCount, pis, VertexType.Color_G);
            readUnsignedBytes(meshRawData, vertexCount, pis, VertexType.Color_B);
            if (isAlpha)
                readUnsignedBytes(meshRawData, vertexCount, pis, VertexType.Color_A);
        }
    }

    private static void readSignedShorts(MeshRawData meshRawData, int vertexCount, PrimitiveInputStream pis, VertexType type) throws IOException {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            float value = (float) DatumEnDecoder.decodeShort(pis.readShort(IS_BIG_ENDIAN), true);
            meshRawData.setVertexTypeDatum(type, vertexIndex, value);
        }
    }

    private static void readUnsignedBytes(MeshRawData meshRawData, int vertexCount, PrimitiveInputStream pis, VertexType type) throws IOException {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            float value = (float) DatumEnDecoder.decodeByte(pis.readByte(), false);
            meshRawData.setVertexTypeDatum(type, vertexIndex, value);
        }
    }

    private static void readFaces(MeshRawData meshRawData, PrimitiveInputStream pis, short version, int metadata) throws IOException {
        int faceCount = pis.readInt(IS_BIG_ENDIAN);
        int numBytes = calculateNumBytes(faceCount);
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            int vertex0 = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
            int vertex1 = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
            int vertex2 = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
            meshRawData.setFace(faceIndex, vertex0, vertex1, vertex2);
        }
    }

    private static short createMetadata(MeshRawData meshRawData) throws MeshIOException {
        if (!meshRawData.hasVertexTypeData(VertexType.Position_X) || !meshRawData.hasVertexTypeData(VertexType.Position_Y))
            throw new MeshIOException("No position data found");
        short metaData = 0;
        boolean is3D = meshRawData.hasVertexTypeData(VertexType.Position_Z);
        if (is3D)
            metaData |= IS_3D_MASK;
        if (meshRawData.hasVertexTypeData(VertexType.Normal_X) && meshRawData.hasVertexTypeData(VertexType.Normal_Y)
                && (!is3D || meshRawData.hasVertexTypeData(VertexType.Normal_Z)))
            metaData |= IS_NORMALS_MASK;
        if (meshRawData.hasVertexTypeData(VertexType.ImageCoord_X) && meshRawData.hasVertexTypeData(VertexType.ImageCoord_Y))
            metaData |= IS_IMAGE_COORDS_MASK;
        if (meshRawData.hasVertexTypeData(VertexType.Color_R) && meshRawData.hasVertexTypeData(VertexType.Color_G) && meshRawData.hasVertexTypeData(VertexType.Color_B)) {
            metaData |= IS_COLORS_MASK;
            boolean isAlpha = meshRawData.hasVertexTypeData(VertexType.Color_A);
            if (isAlpha)
                metaData |= IS_ALPHA_MASK;
        }
        return metaData;
    }

    private static void writeHeader(PrimitiveOutputStream pos, short metadata) throws IOException {
        pos.write(MAGIC);
        pos.writeShort(MAX_VERSION, IS_BIG_ENDIAN);
        pos.writeShort(metadata, IS_BIG_ENDIAN);
    }

    private static void writeVertices(MeshRawData meshRawData, PrimitiveOutputStream pos, short metadata) throws IOException, MeshIOException {
        int vertexCount = meshRawData.getVertexCount();
        pos.writeInt(vertexCount, IS_BIG_ENDIAN);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImages = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isAlpha = (metadata & IS_ALPHA_MASK) != 0;
        writeVertexData(meshRawData, pos, VertexType.Position_X);
        writeVertexData(meshRawData, pos, VertexType.Position_Y);
        if (is3D) {
            writeVertexData(meshRawData, pos, VertexType.Position_Z);
        }
        if (isNormals) {
            writeVertexData(meshRawData, pos, VertexType.Normal_X);
            writeVertexData(meshRawData, pos, VertexType.Normal_Y);
            if (is3D)
                writeVertexData(meshRawData, pos, VertexType.Normal_Z);
        }
        if (isImages)
            writeVertexData(meshRawData, pos, VertexType.ImageCoord_X);
        writeVertexData(meshRawData, pos, VertexType.ImageCoord_Y);
        if (isColors) {
            writeVertexData(meshRawData, pos, VertexType.Color_R);
            writeVertexData(meshRawData, pos, VertexType.Color_G);
            writeVertexData(meshRawData, pos, VertexType.Color_B);
            if (isAlpha)
                writeVertexData(meshRawData, pos, VertexType.Color_A);
        }
    }

    private static void writeVertexData(MeshRawData meshRawData, PrimitiveOutputStream pos, VertexType type) throws IOException, MeshIOException {
        float[] data = meshRawData.getVertexTypeData(type);
        switch (type) {
            case Position_X:
            case Position_Y:
            case Position_Z:
            case Normal_X:
            case Normal_Y:
            case Normal_Z:
                for (float datum : data)
                    pos.writeShort(DatumEnDecoder.encodeAsShort(datum, true), IS_BIG_ENDIAN);
                break;
            case Color_R:
            case Color_G:
            case Color_B:
            case ImageCoord_X:
            case ImageCoord_Y:
                for (float datum : data)
                    pos.writeByte(DatumEnDecoder.encodeAsByte(datum, false));
                break;
            default:
                throw new MeshIOException("Unknown vertex type: " + type.name());
        }
    }

    private static void writeFaces(MeshRawData meshRawData, PrimitiveOutputStream pos) throws IOException {
        int faceCount = meshRawData.getFaceCount();
        pos.writeInt(faceCount, IS_BIG_ENDIAN);
        int numBytes = calculateNumBytes(faceCount);
        int[] indices = meshRawData.getIndices();
        for (int indice : indices)
            pos.writeLong(indice, IS_BIG_ENDIAN, numBytes);
    }

    private static int calculateNumBytes(int faceCount) {
        if (faceCount <= 256)
            return 1;
        else if (faceCount <= 256 * 256)
            return 2;
        else if (faceCount <= 256 * 256 * 256)
            return 3;
        else
            return 4;
    }
}
