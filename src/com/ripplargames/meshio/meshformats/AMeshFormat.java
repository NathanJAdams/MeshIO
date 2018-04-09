package com.ripplargames.meshio.meshformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ripplargames.meshio.IMeshFormat;
import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.MeshRawData;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;

public abstract class AMeshFormat implements IMeshFormat {
    @Override
    public final MeshRawData read(InputStream is) throws MeshIOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        PrimitiveInputStream pis = new PrimitiveInputStream(bis);
        try {
            return read(pis);
        } catch (IOException e) {
            throw new MeshIOException("Failed to read mesh", e);
        }
    }

    @Override
    public final void write(MeshRawData meshRawData, OutputStream os) throws MeshIOException {
        BufferedOutputStream bos = new BufferedOutputStream(os);
        PrimitiveOutputStream pos = new PrimitiveOutputStream(bos);
        try {
            write(meshRawData, pos);
        } catch (IOException e) {
            throw new MeshIOException("Failed to write mesh", e);
        }
        try {
            pos.flush();
        } catch (IOException e) {
            throw new MeshIOException("Failed to flush data, some data may be missing", e);
        }
    }

    protected abstract MeshRawData read(PrimitiveInputStream pis) throws IOException, MeshIOException;

    protected abstract void write(MeshRawData meshRawData, PrimitiveOutputStream pos) throws IOException, MeshIOException;
}
