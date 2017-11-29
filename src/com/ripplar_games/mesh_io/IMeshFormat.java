package com.ripplar_games.mesh_io;

import java.io.InputStream;
import java.io.OutputStream;

import com.ripplar_games.mesh_io.mesh.IMesh;

public interface IMeshFormat {
    String getFileExtension();

    <T extends IMesh> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException;

    void write(IMeshSaver saver, OutputStream os) throws MeshIOException;
}
