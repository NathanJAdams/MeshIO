package com.ripplar_games.mesh_io;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.ripplar_games.mesh_io.formats.mbmsh.MbMshFormat;
import com.ripplar_games.mesh_io.formats.obj.ObjFormat;
import com.ripplar_games.mesh_io.formats.ply.PlyFormatAscii_1_0;
import com.ripplar_games.mesh_io.index.IndicesDataType;
import com.ripplar_games.mesh_io.mesh.IMesh;
import com.ripplar_games.mesh_io.mesh.ImmutableMeshBuilder;
import com.ripplar_games.mesh_io.mesh.MeshType;
import com.ripplar_games.mesh_io.vertex.VertexFormat;

public class MeshIO {
    private final Map<String, IMeshFormat> extensionFormats = new HashMap<String, IMeshFormat>();

    public MeshIO() {
        registerMeshFormat(new PlyFormatAscii_1_0());
        registerMeshFormat(new ObjFormat());
        registerMeshFormat(new MbMshFormat());
    }

    public void registerMeshFormat(IMeshFormat meshFormat) {
        extensionFormats.put(meshFormat.getFileExtension(), meshFormat);
    }

    public IMesh readQuietly(String filePath, MeshType meshType, IndicesDataType<?> indicesDataType, Set<VertexFormat> formats) {
        ImmutableMeshBuilder builder = new ImmutableMeshBuilder(meshType, indicesDataType, formats);
        return readQuietly(filePath, builder);
    }

    public <T extends IMesh> T readQuietly(String filePath, IMeshBuilder<T> builder) {
        try {
            read(filePath, builder);
            return builder.build();
        } catch (MeshIOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T extends IMesh> T read(String filePath, IMeshBuilder<T> builder) throws MeshIOException {
        IMeshFormat format = getFormatFromFilePath(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            return format.read(builder, fis);
        } catch (FileNotFoundException e) {
            throw new MeshIOException("Cannot read from file at path: " + filePath);
        } finally {
            closeQuietly(fis);
        }
    }

    public void write(IMeshSaver saver, String filePath) throws MeshIOException {
        IMeshFormat format = getFormatFromFilePath(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            format.write(saver, fos);
        } catch (FileNotFoundException e) {
            throw new MeshIOException("Cannot write to file at path: " + filePath);
        } finally {
            closeQuietly(fos);
        }
    }

    public IMeshFormat getFormatFromFilePath(String filePath) throws MeshIOException {
        if (filePath == null)
            throw new MeshIOException("Cannot find mesh format from null path");
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1)
            throw new MeshIOException("Cannot find mesh extension in path: " + filePath);
        String extension = filePath.substring(lastDotIndex + 1);
        return getFormatFromExtension(extension);
    }

    public IMeshFormat getFormatFromExtension(String extension) throws MeshIOException {
        IMeshFormat format = extensionFormats.get(extension);
        if (format == null)
            throw new MeshIOException("Cannot find mesh format from extension: " + extension);
        return format;
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
