package com.ripplar_games.mesh_io.formats.mbmsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshFormat;
import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.MeshIOException;
import com.ripplar_games.mesh_io.MeshVertexType;
import com.ripplar_games.mesh_io.io.PrimitiveInputStream;
import com.ripplar_games.mesh_io.io.PrimitiveOutputStream;
import com.ripplar_games.mesh_io.mesh.IMesh;

public class MbMshFormat implements IMeshFormat {
    private static final boolean IS_BIG_ENDIAN = true;
    private static final byte[] MAGIC = {'M', 'B', 'M', 'S', 'H'};
    private static final short MAX_VERSION = 1;
    private static final int IS_3D_MASK = 1 << 15;
    private static final int IS_NORMALS_MASK = 1 << 14;
    private static final int IS_IMAGE_COORDS_MASK = 1 << 13;
    private static final int IS_COLORS_MASK = 1 << 12;
    private static final int IS_COLOR_ALPHA_MASK = 1 << 11;

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

    private static <T extends IMesh> T readWithVersion(IMeshBuilder<T> builder, PrimitiveInputStream pis, short version) throws IOException {
        short metadata = pis.readShort(IS_BIG_ENDIAN);
        readVertices(builder, pis, metadata);
        readFaces(builder, pis, metadata);
        return builder.build();
    }

    private static void readVertices(IMeshBuilder<?> builder, PrimitiveInputStream pis, short metadata) throws IOException {
        int vertexCount = pis.readInt(IS_BIG_ENDIAN);
        builder.setVertexCount(vertexCount);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImageCoords = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isColorAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            readSignedShorts(builder, vertexIndex, pis, MeshVertexType.Position_X, MeshVertexType.Position_Y);
            if (is3D)
                readSignedShorts(builder, vertexIndex, pis, MeshVertexType.Position_Z);
            if (isNormals) {
                readSignedShorts(builder, vertexIndex, pis, MeshVertexType.Normal_X, MeshVertexType.Normal_Y);
                if (is3D)
                    readSignedShorts(builder, vertexIndex, pis, MeshVertexType.Normal_Z);
            }
            if (isImageCoords) {
                readUnsignedBytes(builder, vertexIndex, pis, MeshVertexType.ImageCoord_X, MeshVertexType.ImageCoord_Y);
            }
            if (isColors) {
                readUnsignedBytes(builder, vertexIndex, pis, MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B);
                if (isColorAlpha)
                    readUnsignedBytes(builder, vertexIndex, pis, MeshVertexType.Color_A);
            }
        }
    }

    private static void readSignedShorts(IMeshBuilder<?> builder, int vertexIndex, PrimitiveInputStream pis, MeshVertexType... types) throws IOException {
        for (int i = 0; i < types.length; i++) {
            float value = DatumEnDecode.decodeShort(pis.readShort(IS_BIG_ENDIAN), true);
            builder.setVertexDatum(vertexIndex, types[i], value);
        }
    }

    private static void readUnsignedBytes(IMeshBuilder<?> builder, int vertexIndex, PrimitiveInputStream pis, MeshVertexType... types) throws IOException {
        for (int i = 0; i < types.length; i++) {
            float value = DatumEnDecode.decodeByte(pis.readByte(), false);
            builder.setVertexDatum(vertexIndex, types[i], value);
        }
    }

    private static void readFaces(IMeshBuilder<?> builder, PrimitiveInputStream pis, int metadata) throws IOException {
        int faceCount = pis.readInt(IS_BIG_ENDIAN);
        builder.setFaceCount(faceCount);
        int numBytes = calculateNumBytes(faceCount);
        int[] faceIndices = new int[3];
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            for (int vertexIndex = 0; vertexIndex < faceIndices.length; vertexIndex++)
                faceIndices[vertexIndex] = (int) pis.readLong(IS_BIG_ENDIAN, numBytes);
            builder.setFaceIndices(faceIndex, faceIndices);
        }
    }

    private static short createMetadata(EnumMap<MeshVertexType, Integer> typeIndexes) throws MeshIOException {
        if (!typeIndexes.containsKey(MeshVertexType.Position_X) || !typeIndexes.containsKey(MeshVertexType.Position_Y))
            throw new MeshIOException("No position data found");
        short metaData = 0;
        boolean is3D = typeIndexes.containsKey(MeshVertexType.Position_Z);
        if (is3D)
            metaData |= IS_3D_MASK;
        if (typeIndexes.containsKey(MeshVertexType.Normal_X) && typeIndexes.containsKey(MeshVertexType.Normal_Y)
                && (!is3D || typeIndexes.containsKey(MeshVertexType.Normal_Z)))
            metaData |= IS_NORMALS_MASK;
        if (typeIndexes.containsKey(MeshVertexType.ImageCoord_X) && typeIndexes.containsKey(MeshVertexType.ImageCoord_Y))
            metaData |= IS_IMAGE_COORDS_MASK;
        if (typeIndexes.containsKey(MeshVertexType.Color_R) && typeIndexes.containsKey(MeshVertexType.Color_G)
                && typeIndexes.containsKey(MeshVertexType.Color_B)) {
            metaData |= IS_COLORS_MASK;
            if (typeIndexes.containsKey(MeshVertexType.Color_A))
                metaData |= IS_COLOR_ALPHA_MASK;
        }
        return metaData;
    }

    private static void writeHeader(PrimitiveOutputStream pos, short metadata) throws IOException {
        pos.write(MAGIC);
        pos.writeShort(MAX_VERSION, IS_BIG_ENDIAN);
        pos.writeShort(metadata, IS_BIG_ENDIAN);
    }

    private static void writeVertices(IMeshSaver saver, PrimitiveOutputStream pos, short metadata, EnumMap<MeshVertexType, Integer> typeIndexes)
            throws IOException, MeshIOException {
        int vertexCount = saver.getVertexCount();
        pos.writeInt(vertexCount, IS_BIG_ENDIAN);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImages = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
        float[] vertexData = new float[typeIndexes.size()];
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            saver.getVertexData(vertexIndex, vertexData);
            writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Position_X, MeshVertexType.Position_Y);
            if (is3D)
                writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Position_Z);
            if (isNormals) {
                writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Normal_X, MeshVertexType.Normal_Y);
                if (is3D)
                    writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Normal_Z);
            }
            if (isImages)
                writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.ImageCoord_X, MeshVertexType.ImageCoord_Y);
            if (isColors) {
                writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B);
                if (isAlpha)
                    writeVertexData(pos, vertexData, typeIndexes, MeshVertexType.ImageCoord_X, MeshVertexType.Color_A);
            }
        }
    }

    private static void writeVertexData(PrimitiveOutputStream pos, float[] vertexData, EnumMap<MeshVertexType, Integer> typeIndexes,
                                        MeshVertexType... types) throws IOException, MeshIOException {
        for (int i = 0; i < types.length; i++) {
            MeshVertexType type = types[i];
            int index = typeIndexes.get(type);
            float value = vertexData[index];
            switch (type) {
                case Position_X:
                case Position_Y:
                case Position_Z:
                case Normal_X:
                case Normal_Y:
                case Normal_Z:
                    pos.writeShort(DatumEnDecode.encodeAsShort(value, true), IS_BIG_ENDIAN);
                    break;
                case Color_R:
                case Color_G:
                case Color_B:
                case Color_A:
                case ImageCoord_X:
                case ImageCoord_Y:
                    pos.writeByte(DatumEnDecode.encodeAsByte(value, false));
                    break;
                default:
                    throw new MeshIOException("Unknown vertex type: " + type.name());
            }
        }
    }

    private static void writeFaces(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException, MeshIOException {
        int faceCount = saver.getFaceCount();
        pos.writeInt(faceCount, IS_BIG_ENDIAN);
        int numBytes = calculateNumBytes(faceCount);
        int[] faceIndices = new int[3];
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            saver.getFaceIndices(faceIndex, faceIndices);
            for (int vertexIndex = 0; vertexIndex < faceIndices.length; vertexIndex++)
                pos.writeLong(faceIndices[vertexIndex], IS_BIG_ENDIAN, numBytes);
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

    @Override
    public String getFileExtension() {
        return "mbmsh";
    }

    @Override
    public <T extends IMesh> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
        builder.clear();
        PrimitiveInputStream pis;
        try {
            pis = new PrimitiveInputStream(is);
            readMagic(pis);
            short version = pis.readShort(IS_BIG_ENDIAN);
            return readWithVersion(builder, pis, version);
        } catch (IOException ioe) {
            throw new MeshIOException("Exception when reading from stream", ioe);
        }
    }

    @Override
    public void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
        if (saver == null)
            throw new MeshIOException("A mesh saver is required", new NullPointerException());
        if (os == null)
            throw new MeshIOException("An output stream is required", new NullPointerException());
        try {
            PrimitiveOutputStream pos = new PrimitiveOutputStream(os);
            List<MeshVertexType> format = saver.getVertexFormat();
            EnumMap<MeshVertexType, Integer> typeIndexes = MeshVertexType.createTypeIndexes(format);
            short metadata = createMetadata(typeIndexes);
            writeHeader(pos, metadata);
            writeVertices(saver, pos, metadata, typeIndexes);
            writeFaces(saver, pos);
        } catch (IOException ioe) {
            throw new MeshIOException("Exception when writing to stream", ioe);
        }
    }
}
