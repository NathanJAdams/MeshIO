package com.ripplargames.meshio;

import java.io.InputStream;
import java.io.OutputStream;

public interface IMeshFormat {
    String getFileExtension();

    Mesh read(InputStream is) throws MeshIOException;

    void write(Mesh mesh, OutputStream os) throws MeshIOException;
}
