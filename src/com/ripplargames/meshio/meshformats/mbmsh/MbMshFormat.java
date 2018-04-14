package com.ripplargames.meshio.meshformats.mbmsh;

import java.io.IOException;
import java.util.Arrays;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.Mesh;
import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.meshformats.AMeshFormat;
import com.ripplargames.meshio.util.DatumEnDecoder;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.util.ResizableFloatArray;
import com.ripplargames.meshio.vertices.VertexType;

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
    protected Mesh read(PrimitiveInputStream pis) throws IOException, MeshIOException {
        readMagic(pis);
        short version = pis.readShort(IS_BIG_ENDIAN);
        short metadata = pis.readShort(IS_BIG_ENDIAN);
        Mesh mesh = new Mesh();
        readVertices(mesh, pis, version, metadata);
        readFaces(mesh, pis, version, metadata);
        return mesh;
    }

    @Override
    protected void write(Mesh mesh, PrimitiveOutputStream pos) throws IOException, MeshIOException {
        short metadata = createMetadata(mesh);
        writeHeader(pos, metadata);
        writeVertices(mesh, pos, metadata);
        writeFaces(mesh, pos);
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

    private static void readVertices(Mesh mesh, PrimitiveInputStream pis, short version, short metadata) throws IOException {
        int vertexCount = pis.readInt(IS_BIG_ENDIAN);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImageCoords = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isAlpha = (metadata & IS_ALPHA_MASK) != 0;
        readSignedShorts(mesh, vertexCount, pis, VertexType.Position_X);
        readSignedShorts(mesh, vertexCount, pis, VertexType.Position_Y);
        if (is3D)
            readSignedShorts(mesh, vertexCount, pis, VertexType.Position_Z);
        if (isNormals) {
            readSignedShorts(mesh, vertexCount, pis, VertexType.Normal_X);
            readSignedShorts(mesh, vertexCount, pis, VertexType.Normal_Y);
            if (is3D)
                readSignedShorts(mesh, vertexCount, pis, VertexType.Normal_Z);
        }
        if (isImageCoords) {
            readUnsignedBytes(mesh, vertexCount, pis, VertexType.ImageCoord_X);
            readUnsignedBytes(mesh, vertexCount, pis, VertexType.ImageCoord_Y);
        }
        if (isColors) {
            readUnsignedBytes(mesh, vertexCount, pis, VertexType.Color_R);
            readUnsignedBytes(mesh, vertexCount, pis, VertexType.Color_G);
            readUnsignedBytes(mesh, vertexCount, pis, VertexType.Color_B);
            if (isAlpha)
                readUnsignedBytes(mesh, vertexCount, pis, VertexType.Color_A);
        }
    }

    private static void readSignedShorts(Mesh mesh, int vertexCount, PrimitiveInputStream pis, VertexType type) throws IOException {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            float value = (float) DatumEnDecoder.decodeShort(pis.readShort(IS_BIG_ENDIAN), true);
            mesh.setVertexTypeDatum(type, vertexIndex, value);
        }
    }

    private static void readUnsignedBytes(Mesh mesh, int vertexCount, PrimitiveInputStream pis, VertexType type) throws IOException {
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            float value = (float) DatumEnDecoder.decodeByte(pis.readByte(), false);
            mesh.setVertexTypeDatum(type, vertexIndex, value);
        }
    }

    private static void readFaces(Mesh mesh, PrimitiveInputStream pis, short version, int metadata) throws IOException {
        int faceCount = pis.readInt(IS_BIG_ENDIAN);
        int numBytes = calculateNumBytes(faceCount);
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            int v0 = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
            int v1 = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
            int v2 = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
            mesh.appendFace(new Face(v0, v1, v2));
        }
    }

    private static short createMetadata(Mesh mesh) throws MeshIOException {
        if (!mesh.hasVertexTypeData(VertexType.Position_X) || !mesh.hasVertexTypeData(VertexType.Position_Y))
            throw new MeshIOException("No position data found");
        short metaData = 0;
        boolean is3D = mesh.hasVertexTypeData(VertexType.Position_Z);
        if (is3D)
            metaData |= IS_3D_MASK;
        if (mesh.hasVertexTypeData(VertexType.Normal_X) && mesh.hasVertexTypeData(VertexType.Normal_Y)
                && (!is3D || mesh.hasVertexTypeData(VertexType.Normal_Z)))
            metaData |= IS_NORMALS_MASK;
        if (mesh.hasVertexTypeData(VertexType.ImageCoord_X) && mesh.hasVertexTypeData(VertexType.ImageCoord_Y))
            metaData |= IS_IMAGE_COORDS_MASK;
        if (mesh.hasVertexTypeData(VertexType.Color_R) && mesh.hasVertexTypeData(VertexType.Color_G) && mesh.hasVertexTypeData(VertexType.Color_B)) {
            metaData |= IS_COLORS_MASK;
            boolean isAlpha = mesh.hasVertexTypeData(VertexType.Color_A);
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

    private static void writeVertices(Mesh mesh, PrimitiveOutputStream pos, short metadata) throws IOException, MeshIOException {
        int vertexCount = mesh.vertexCount();
        pos.writeInt(vertexCount, IS_BIG_ENDIAN);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImages = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isAlpha = (metadata & IS_ALPHA_MASK) != 0;
        writeVertexData(mesh, pos, VertexType.Position_X);
        writeVertexData(mesh, pos, VertexType.Position_Y);
        if (is3D) {
            writeVertexData(mesh, pos, VertexType.Position_Z);
        }
        if (isNormals) {
            writeVertexData(mesh, pos, VertexType.Normal_X);
            writeVertexData(mesh, pos, VertexType.Normal_Y);
            if (is3D)
                writeVertexData(mesh, pos, VertexType.Normal_Z);
        }
        if (isImages)
            writeVertexData(mesh, pos, VertexType.ImageCoord_X);
        writeVertexData(mesh, pos, VertexType.ImageCoord_Y);
        if (isColors) {
            writeVertexData(mesh, pos, VertexType.Color_R);
            writeVertexData(mesh, pos, VertexType.Color_G);
            writeVertexData(mesh, pos, VertexType.Color_B);
            if (isAlpha)
                writeVertexData(mesh, pos, VertexType.Color_A);
        }
    }

    private static void writeVertexData(Mesh mesh, PrimitiveOutputStream pos, VertexType type) throws IOException, MeshIOException {
        ResizableFloatArray data = mesh.vertexTypeData(type);
        switch (type) {
            case Position_X:
            case Position_Y:
            case Position_Z:
            case Normal_X:
            case Normal_Y:
            case Normal_Z:
                for (int i = 0; i < data.length(); i++)
                    pos.writeShort(DatumEnDecoder.encodeAsShort(data.getAt(i), true), IS_BIG_ENDIAN);
                break;
            case Color_R:
            case Color_G:
            case Color_B:
            case Color_A:
            case ImageCoord_X:
            case ImageCoord_Y:
                for (int i = 0; i < data.length(); i++)
                    pos.writeByte(DatumEnDecoder.encodeAsByte(data.getAt(i), false));
                break;
            default:
                throw new MeshIOException("Unknown vertex type: " + type.name());
        }
    }

    private static void writeFaces(Mesh mesh, PrimitiveOutputStream pos) throws IOException {
        int faceCount = mesh.faceCount();
        pos.writeInt(faceCount, IS_BIG_ENDIAN);
        int numBytes = calculateNumBytes(faceCount);
        for (Face face : mesh.faces()) {
            pos.writeLong(face.getV0(), IS_BIG_ENDIAN, numBytes);
            pos.writeLong(face.getV1(), IS_BIG_ENDIAN, numBytes);
            pos.writeLong(face.getV2(), IS_BIG_ENDIAN, numBytes);
        }
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
