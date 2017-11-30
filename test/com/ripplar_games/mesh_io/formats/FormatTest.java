package com.ripplar_games.mesh_io.formats;

import com.ripplar_games.mesh_io.IMeshFormat;
import com.ripplar_games.mesh_io.MeshIOException;
import com.ripplar_games.mesh_io.formats.mbmsh.MbMshFormat;
import com.ripplar_games.mesh_io.formats.obj.ObjFormat;
import com.ripplar_games.mesh_io.formats.ply.PlyFormatAscii_1_0;
import com.ripplar_games.mesh_io.formats.ply.PlyFormatBinaryBigEndian_1_0;
import com.ripplar_games.mesh_io.formats.ply.PlyFormatBinaryLittleEndian_1_0;
import com.ripplar_games.mesh_io.mesh.EditableMesh;
import com.ripplar_games.mesh_io.mesh.IMesh;
import com.ripplar_games.mesh_io.vertex.VertexDataType;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexSubFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FormatTest {
    private static final Random RANDOM = new Random();

    @Ignore
    @Test
    public void testFormats() throws MeshIOException {
//        testFormat(new PlyFormatAscii_1_0());
//        testFormat(new PlyFormatBinaryBigEndian_1_0());
//        testFormat(new PlyFormatBinaryLittleEndian_1_0());
//        testFormat(new ObjFormat());
        testFormat(new MbMshFormat());
    }

    private void testFormat(IMeshFormat meshFormat) throws MeshIOException {
        List<VertexType> vertexTypes = createVertexTypes();
        VertexFormat format = createFormat(vertexTypes);
        EditableMesh mesh = createRandomMesh(vertexTypes, format);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        meshFormat.write(mesh, baos);
        byte[] buffer = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        IMesh translatedMesh = meshFormat.read(new EditableMesh(), bais);
        checkMeshes(mesh, translatedMesh, format);
    }

    private List<VertexType> createVertexTypes() {
        return Arrays.asList(VertexType.getValues());
    }

    private VertexFormat createFormat(List<VertexType> vertexTypes) {
        List<VertexSubFormat> subFormats = new ArrayList<VertexSubFormat>();
        for (VertexType vertexType : vertexTypes) {
            subFormats.add(new VertexSubFormat(vertexType, VertexDataType.Float));
        }
        return new VertexFormat(subFormats);
    }

    private EditableMesh createRandomMesh(List<VertexType> vertexTypes, VertexFormat format) {
        EditableMesh mesh = new EditableMesh();
        int faces = 5 + RANDOM.nextInt(5);
        int vertices = 5 + RANDOM.nextInt(5);
        mesh.setFaceCount(faces);
        mesh.setVertexCount(vertices);
        mesh.addVertexFormat(format);
        for (int i = 0; i < faces; i++) {
            for (int j = 0; j < 3; j++) {
                mesh.setFaceIndicesIndex(i, j, RANDOM.nextInt(vertices));
            }
        }
        for (int i = 0; i < vertices; i++) {
            VertexType vertexType = vertexTypes.get(RANDOM.nextInt(vertexTypes.size()));
            mesh.setVertexDatum(i, vertexType, RANDOM.nextFloat());
        }
        return mesh;
    }

    private void checkMeshes(IMesh mesh, IMesh translatedMesh, VertexFormat format) {
        Assert.assertTrue(mesh.isValid());
        Assert.assertTrue(translatedMesh.isValid());
        Assert.assertEquals(mesh.getIndices(), translatedMesh.getIndices());
        Assert.assertEquals(mesh.getVertices(format), translatedMesh.getVertices(format));
    }
}
