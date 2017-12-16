package com.ripplar_games.mesh_io;

import com.ripplar_games.mesh_io.io.PrimitiveInputStream;
import com.ripplar_games.mesh_io.io.PrimitiveOutputStream;

public interface IMeshFormat {
    String getFileExtension();

    void read(IMeshBuilder<?> builder, PrimitiveInputStream pis) throws MeshIOException;

    void write(IMeshSaver saver, PrimitiveOutputStream pos) throws MeshIOException;
}
