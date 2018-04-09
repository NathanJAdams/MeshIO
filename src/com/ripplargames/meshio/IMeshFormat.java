package com.ripplargames.meshio;

import java.io.InputStream;
import java.io.OutputStream;

public interface IMeshFormat {
    String getFileExtension();

    MeshRawData read(InputStream is) throws MeshIOException;

    void write(MeshRawData meshRawData, OutputStream os) throws MeshIOException;
}
