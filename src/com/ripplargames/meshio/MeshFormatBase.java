package com.ripplargames.meshio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;

public abstract class MeshFormatBase implements IMeshFormat {
    @Override
    public final void read(IMeshBuilder<?> builder, InputStream is) throws MeshIOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        PrimitiveInputStream pis = new PrimitiveInputStream(bis);
        try {
            read(builder, pis);
        } catch (IOException e) {
            throw new MeshIOException("Failed to read mesh", e);
        }
    }

    @Override
    public final void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        PrimitiveOutputStream pos = new PrimitiveOutputStream(bos);
        try {
            write(saver, pos);
        } catch (IOException e) {
            throw new MeshIOException("Failed to write mesh", e);
        }
        try {
            pos.flush();
        } catch (IOException e) {
            throw new MeshIOException("Failed to flush data, some data may be missing", e);
        }
    }

    protected abstract void read(IMeshBuilder<?> builder, PrimitiveInputStream pis) throws IOException, MeshIOException;

    protected abstract void write(IMeshSaver saver, PrimitiveOutputStream pos) throws IOException, MeshIOException;
}
