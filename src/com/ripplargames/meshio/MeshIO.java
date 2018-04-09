package com.ripplargames.meshio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.index.IndicesDataType;
import com.ripplargames.meshio.mesh.ImmutableMesh;
import com.ripplargames.meshio.mesh.ImmutableMeshBuilder;
import com.ripplargames.meshio.mesh.MeshType;
import com.ripplargames.meshio.meshformats.mbmsh.MbMshFormat;
import com.ripplargames.meshio.meshformats.obj.ObjFormat;
import com.ripplargames.meshio.meshformats.ply.PlyFormatAscii_1_0;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;

public class MeshIO {
    private static final Logger LOGGER = Logger.getLogger(MeshIO.class.getName());

    private final Map<String, IMeshFormat> extensionFormats = new HashMap<String, IMeshFormat>();

    public MeshIO() {
        registerMeshFormat(new PlyFormatAscii_1_0());
        registerMeshFormat(new ObjFormat());
        registerMeshFormat(new MbMshFormat());
    }

    public void registerMeshFormat(IMeshFormat meshFormat) {
        extensionFormats.put(meshFormat.getFileExtension(), meshFormat);
    }

    public <T> ImmutableMesh readImmutable(String filePath, MeshType meshType, IndicesDataType<T> indicesDataType, Set<BufferFormat> formats) throws MeshIOException {
        ImmutableMeshBuilder<T> builder = new ImmutableMeshBuilder<T>(meshType, indicesDataType, formats);
        return read(builder, filePath);
    }

    public <T extends IMesh> T read(IMeshBuilder<T> builder, String filePath) throws MeshIOException {
        IMeshFormat format = getFormatFromFilePath(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            return read(builder, fis, format);
        } catch (FileNotFoundException e) {
            throwBecause("Cannot read from file at path: " + filePath, e);
            return null;
        } finally {
            closeQuietly(fis);
        }
    }

    public <T extends IMesh> T read(IMeshBuilder<T> builder, InputStream inputStream, IMeshFormat format) throws MeshIOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        PrimitiveInputStream pis = new PrimitiveInputStream(bis);
        MeshRawData meshRawData = format.read(pis);
        meshRawData.isValid();
        return builder.build(meshRawData);
    }

    public void write(IMesh mesh, String filePath) throws MeshIOException {
        IMeshFormat format = getFormatFromFilePath(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            write(mesh, fos, format);
        } catch (FileNotFoundException e) {
            throwBecause("Cannot write to file at path: " + filePath, e);
        } finally {
            closeQuietly(fos);
        }
    }

    public void write(IMesh mesh, OutputStream outputStream, IMeshFormat format) throws MeshIOException {
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        PrimitiveOutputStream pos = new PrimitiveOutputStream(bos);
        MeshRawData meshRawData = mesh.toRawData();
        meshRawData.isValid();
        format.write(meshRawData, pos);
    }

    public IMeshFormat getFormatFromFilePath(String filePath) throws MeshIOException {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1)
            throwBecause("Cannot find mesh extension in path: " + filePath);
        String extension = filePath.substring(lastDotIndex + 1);
        return getFormatFromExtension(extension);
    }

    public IMeshFormat getFormatFromExtension(String extension) throws MeshIOException {
        IMeshFormat format = extensionFormats.get(extension);
        if (format == null)
            throwBecause("Cannot find mesh format from extension: " + extension);
        return format;
    }

    private void throwBecause(String reason) throws MeshIOException {
        throw new MeshIOException(reason);
    }

    private void throwBecause(String reason, Exception nestedException) throws MeshIOException {
        throw new MeshIOException(reason, nestedException);
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to close file stream");
            }
        }
    }
}
