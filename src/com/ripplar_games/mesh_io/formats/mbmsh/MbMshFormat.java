package com.ripplar_games.mesh_io.formats.mbmsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;

import com.ripplar_games.mesh_io.DatumEnDecoder;
import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshFormat;
import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.MeshIOException;
import com.ripplar_games.mesh_io.io.PrimitiveInputStream;
import com.ripplar_games.mesh_io.io.PrimitiveOutputStream;
import com.ripplar_games.mesh_io.mesh.IMesh;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

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

    private static void readVertices(IMeshBuilder<?> builder, PrimitiveInputStream pis, short version, short metadata) throws IOException {
        int vertexCount = pis.readInt(IS_BIG_ENDIAN);
        builder.setVertexCount(vertexCount);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImageCoords = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isColorAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            readSignedShorts(builder, vertexIndex, pis, VertexType.Position_X, VertexType.Position_Y);
            if (is3D)
                readSignedShorts(builder, vertexIndex, pis, VertexType.Position_Z);
            if (isNormals) {
                readSignedShorts(builder, vertexIndex, pis, VertexType.Normal_X, VertexType.Normal_Y);
                if (is3D)
                    readSignedShorts(builder, vertexIndex, pis, VertexType.Normal_Z);
            }
            if (isImageCoords) {
                readUnsignedBytes(builder, vertexIndex, pis, VertexType.ImageCoord_X, VertexType.ImageCoord_Y);
            }
            if (isColors) {
                readUnsignedBytes(builder, vertexIndex, pis, VertexType.Color_R, VertexType.Color_G, VertexType.Color_B);
                if (isColorAlpha)
                    readUnsignedBytes(builder, vertexIndex, pis, VertexType.Color_A);
            }
        }
    }

    private static void readSignedShorts(IMeshBuilder<?> builder, int vertexIndex, PrimitiveInputStream pis, VertexType... types) throws IOException {
        for (int i = 0; i < types.length; i++) {
            float value = (float) DatumEnDecoder.decodeShort(pis.readShort(IS_BIG_ENDIAN), true);
            builder.setVertexDatum(vertexIndex, types[i], value);
        }
    }

    private static void readUnsignedBytes(IMeshBuilder<?> builder, int vertexIndex, PrimitiveInputStream pis, VertexType... types) throws IOException {
        for (int i = 0; i < types.length; i++) {
            float value = (float) DatumEnDecoder.decodeByte(pis.readByte(), false);
            builder.setVertexDatum(vertexIndex, types[i], value);
        }
    }

    private static void readFaces(IMeshBuilder<?> builder, PrimitiveInputStream pis, short version, int metadata) throws IOException {
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

    private static short createMetadata(Set<VertexFormat> formats) throws MeshIOException {
        if (!containsVertexType(formats, VertexType.Position_X) || !containsVertexType(formats, VertexType.Position_Y))
            throw new MeshIOException("No position data found");
        short metaData = 0;
        boolean is3D = containsVertexType(formats, VertexType.Position_Z);
        if (is3D)
            metaData |= IS_3D_MASK;
        if (containsVertexType(formats, VertexType.Normal_X) && containsVertexType(formats, VertexType.Normal_Y)
                && (!is3D || containsVertexType(formats, VertexType.Normal_Z)))
            metaData |= IS_NORMALS_MASK;
        if (containsVertexType(formats, VertexType.ImageCoord_X) && containsVertexType(formats, VertexType.ImageCoord_Y))
            metaData |= IS_IMAGE_COORDS_MASK;
        if (containsVertexType(formats, VertexType.Color_R) && containsVertexType(formats, VertexType.Color_G) && containsVertexType(formats, VertexType.Color_B)) {
            metaData |= IS_COLORS_MASK;
            if (containsVertexType(formats, VertexType.Color_A))
                metaData |= IS_COLOR_ALPHA_MASK;
        }
        return metaData;
    }

    private static boolean containsVertexType(Set<VertexFormat> formats, VertexType vertexType) {
        for (VertexFormat format : formats) {
            if (format.containsVertexType(vertexType)) {
                return true;
            }
        }
        return false;
    }

    private static void writeHeader(PrimitiveOutputStream pos, short metadata) throws IOException {
        pos.write(MAGIC);
        pos.writeShort(MAX_VERSION, IS_BIG_ENDIAN);
        pos.writeShort(metadata, IS_BIG_ENDIAN);
    }

    private static void writeVertices(IMeshSaver saver, PrimitiveOutputStream pos, short metadata)
            throws IOException, MeshIOException {
        int vertexCount = saver.getVertexCount();
        pos.writeInt(vertexCount, IS_BIG_ENDIAN);
        boolean is3D = (metadata & IS_3D_MASK) != 0;
        boolean isNormals = (metadata & IS_NORMALS_MASK) != 0;
        boolean isImages = (metadata & IS_IMAGE_COORDS_MASK) != 0;
        boolean isColors = (metadata & IS_COLORS_MASK) != 0;
        boolean isAlpha = (metadata & IS_COLOR_ALPHA_MASK) != 0;
        for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
            writeVertexData(saver, vertexIndex, pos, VertexType.Position_X, VertexType.Position_Y);
            if (is3D) {
                writeVertexData(saver, vertexIndex, pos, VertexType.Position_Z);
            }
            if (isNormals) {
                writeVertexData(saver, vertexIndex, pos, VertexType.Normal_X, VertexType.Normal_Y);
                if (is3D)
                    writeVertexData(saver, vertexIndex, pos, VertexType.Normal_Z);
            }
            if (isImages)
                writeVertexData(saver, vertexIndex, pos, VertexType.ImageCoord_X, VertexType.ImageCoord_Y);
            if (isColors) {
                writeVertexData(saver, vertexIndex, pos, VertexType.Color_R, VertexType.Color_G, VertexType.Color_B);
                if (isAlpha)
                    writeVertexData(saver, vertexIndex, pos, VertexType.Color_A);
            }
        }
    }

    private static void writeVertexData(IMeshSaver saver, int vertexIndex, PrimitiveOutputStream pos, VertexType... types) throws IOException, MeshIOException {
        for (VertexType type : types) {
            float datum = saver.getVertexDatum(vertexIndex, type);
            switch (type) {
                case Position_X:
                case Position_Y:
                case Position_Z:
                case Normal_X:
                case Normal_Y:
                case Normal_Z:
                    pos.writeShort(DatumEnDecoder.encodeAsShort(datum, true), IS_BIG_ENDIAN);
                    break;
                case Color_R:
                case Color_G:
                case Color_B:
                case Color_A:
                case ImageCoord_X:
                case ImageCoord_Y:
                    pos.writeByte(DatumEnDecoder.encodeAsByte(datum, false));
                    break;
                default:
                    throw new MeshIOException("Unknown vertex type: " + type.name());
            }
        }
    }

    private static void writeFaces(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException {
        int faceCount = saver.getFaceCount();
        pos.writeInt(faceCount, IS_BIG_ENDIAN);
        int numBytes = calculateNumBytes(faceCount);
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            int[] faceIndices = saver.getFaceIndices(faceIndex);
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
            short metadata = pis.readShort(IS_BIG_ENDIAN);
            readVertices(builder, pis, version, metadata);
            readFaces(builder, pis, version, metadata);
            return builder.build();
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
        PrimitiveOutputStream pos = new PrimitiveOutputStream(os);
        try {
            Set<VertexFormat> formats = saver.getVertexFormats();
            short metadata = createMetadata(formats);
            writeHeader(pos, metadata);
            writeVertices(saver, pos, metadata);
            writeFaces(saver, pos);
        } catch (IOException ioe) {
            throw new MeshIOException("Exception when writing to stream", ioe);
        } finally {
            try {
                pos.flush();
            } catch (IOException e) {
                //ignore error
            }
        }
    }
}
