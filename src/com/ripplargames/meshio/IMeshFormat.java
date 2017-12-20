package com.ripplargames.meshio;

import java.io.InputStream;
import java.io.OutputStream;

public interface IMeshFormat {
    String getFileExtension();

    void read(IMeshBuilder<?> builder, InputStream is) throws MeshIOException;

    void write(IMeshSaver saver, OutputStream os) throws MeshIOException;
}
