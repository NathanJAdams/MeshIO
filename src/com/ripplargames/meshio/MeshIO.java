package com.ripplargames.meshio;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ripplargames.meshio.meshformats.mbmsh.MbMshFormat;
import com.ripplargames.meshio.meshformats.obj.ObjFormat;
import com.ripplargames.meshio.meshformats.ply.PlyFormatAscii_1_0;
import com.ripplargames.meshio.index.IndicesDataType;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.mesh.ImmutableMeshBuilder;
import com.ripplargames.meshio.mesh.MeshType;
import com.ripplargames.meshio.bufferformats.BufferFormat;

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

    public IMesh readAs(String filePath, MeshType meshType, IndicesDataType<?> indicesDataType, Set<BufferFormat> formats) throws MeshIOException {
        ImmutableMeshBuilder builder = new ImmutableMeshBuilder(meshType, indicesDataType, formats);
        return read(filePath, builder);
    }

    public <T extends IMesh> T read(String filePath, IMeshBuilder<T> builder) throws MeshIOException {
        requireNotNull(filePath, "file path");
        requireNotNull(builder, "mesh builder");
        IMeshFormat format = getFormatFromFilePath(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fis);
            PrimitiveInputStream pis = new PrimitiveInputStream(bis);
            builder.clear();
            format.read(builder, pis);
            return builder.build();
        } catch (FileNotFoundException e) {
            throwBecause("Cannot read from file at path: " + filePath, e);
            return null;
        } finally {
            closeQuietly(fis);
        }
    }

    public void write(IMeshSaver saver, String filePath) throws MeshIOException {
        requireNotNull(saver, "mesh saver");
        requireNotNull(filePath, "file path");
        IMeshFormat format = getFormatFromFilePath(filePath);
        FileOutputStream fos = null;
        PrimitiveOutputStream pos = null;
        try {
            fos = new FileOutputStream(filePath);
            pos = new PrimitiveOutputStream(fos);
            format.write(saver, pos);
        } catch (FileNotFoundException e) {
            throwBecause("Cannot write to file at path: " + filePath, e);
        } finally {
            closeQuietly(pos);
            closeQuietly(fos);
        }
    }

    public IMeshFormat getFormatFromFilePath(String filePath) throws MeshIOException {
        requireNotNull(filePath, "file path");
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

    private void requireNotNull(Object object, String name) throws MeshIOException {
        if (object == null)
            throwBecause("A " + name + " is required", new NullPointerException());
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
