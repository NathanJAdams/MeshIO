package com.ripplar_games.mesh_io.formats.obj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshFormat;
import com.ripplar_games.mesh_io.IMeshSaver;
import com.ripplar_games.mesh_io.MeshIOException;
import com.ripplar_games.mesh_io.io.PrimitiveInputStream;
import com.ripplar_games.mesh_io.mesh.IMesh;

// TODO in progress
public class ObjFormat implements IMeshFormat {
    private static final Pattern SPLITTER_PATTERN = Pattern.compile(" ");


    @Override
    public String getFileExtension() {
        return "obj";
    }

    @Override
    public <T extends IMesh> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException {
        builder.clear();
        try {
            PrimitiveInputStream pis = new PrimitiveInputStream(is);
            int currentVertexCount = 0;
            int currentFaceCount = 0;
            int requestedVertexCount = 16;
            int requestedFaceCount = 16;
            for (String line = pis.readLine(); line != null; line = pis.readLine()) {
                String[] parts = SPLITTER_PATTERN.split(line);
                String firstPart = parts[0];
                if ("#".equals(firstPart)) {
                    //comment
                } else {
                    float[] floatArray = toFloatArrayIndex1(parts);
                    if ("f".equals(firstPart)) {
                        appendFace(builder, floatArray);
                    } else {
                        boolean isVertex = "v".equals(firstPart);
                        boolean isTexCoord = !isVertex && "vt".equals(firstPart);
                        boolean isNormal = !isVertex && !isTexCoord && "vn".equals(firstPart);
                        if (isVertex || isTexCoord || isNormal) {
                            currentVertexCount++;
                            if (currentVertexCount > requestedVertexCount) {
                                requestedVertexCount *= 2;
                                builder.setVertexCount(requestedVertexCount);
                            }
                        }
                        if (isVertex) {
                            appendVertex(builder, floatArray);
                        } else if (isTexCoord) {
                            appendTexCoord(builder, floatArray);
                        } else if (isNormal) {
                            appendNormal(builder, floatArray);
                        } else {
                            throw new MeshIOException("Unrecognised element type: " + line + ". Expected a comment, \"v\", \"vt\", \"vn\" or \"f\"");
                        }
                    }
                }
            }
            return builder.build();
        } catch (IOException e) {
            throw new MeshIOException("Exception when reading from stream", e);
        }
    }

    @Override
    public void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
        // TODO
    }

    private static float[] toFloatArrayIndex1(String[] parts) throws MeshIOException {
        float[] floatArray = new float[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            try {
                floatArray[i - 1] = Float.parseFloat(part);
            } catch (NumberFormatException e) {
                throw new MeshIOException("Could not parse value from: \"" + part + '\"');
            }
        }
        return floatArray;
    }

    private static <T extends IMesh> void appendVertex(IMeshBuilder<T> builder, float[] parts) {
        
    }

    private static <T extends IMesh> void appendTexCoord(IMeshBuilder<T> builder, float[] parts) {

    }

    private static <T extends IMesh> void appendNormal(IMeshBuilder<T> builder, float[] parts) {

    }

    private static <T extends IMesh> void appendFace(IMeshBuilder<T> builder, float[] parts) {

    }
}
