package com.ripplar_games.mesh_io.formats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ripplar_games.mesh_io.IMeshBuilder;
import com.ripplar_games.mesh_io.IMeshFormat;
import com.ripplar_games.mesh_io.MeshIOException;
import com.ripplar_games.mesh_io.formats.mbmsh.MbMshFormat;
import com.ripplar_games.mesh_io.formats.ply.PlyFormatAscii_1_0;
import com.ripplar_games.mesh_io.formats.ply.PlyFormatBinaryBigEndian_1_0;
import com.ripplar_games.mesh_io.formats.ply.PlyFormatBinaryLittleEndian_1_0;
import com.ripplar_games.mesh_io.index.IndicesDataType;
import com.ripplar_games.mesh_io.index.IndicesDataTypes;
import com.ripplar_games.mesh_io.mesh.EditableMesh;
import com.ripplar_games.mesh_io.mesh.IMesh;
import com.ripplar_games.mesh_io.mesh.ImmutableMesh;
import com.ripplar_games.mesh_io.mesh.ImmutableMeshBuilder;
import com.ripplar_games.mesh_io.mesh.MeshType;
import com.ripplar_games.mesh_io.vertex.VertexDataType;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexSubFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;
import org.junit.Assert;
import org.junit.Test;

public class FormatTest {
    private static final Random RANDOM = new Random();

    @Test
    public void testFormats() throws MeshIOException {
        testFormat(new PlyFormatAscii_1_0());
        testFormat(new PlyFormatBinaryBigEndian_1_0());
        testFormat(new PlyFormatBinaryLittleEndian_1_0());
        testFormat(new MbMshFormat());
//        testFormat(new ObjFormat());
    }

    private void testFormat(IMeshFormat meshFormat) throws MeshIOException {
        List<VertexSubFormat> subFormats = new ArrayList<VertexSubFormat>();
        VertexDataType[] vertexDataTypes = VertexDataType.values();
        for (VertexType vertexType : VertexType.getValues()) {
            VertexDataType vertexDataType = vertexDataTypes[RANDOM.nextInt(vertexDataTypes.length)];
            subFormats.add(new VertexSubFormat(vertexType, vertexDataType));
        }
        VertexFormat format = new VertexFormat(subFormats);
        for (MeshType meshType : MeshType.values()) {
            for (IndicesDataType<?> indicesDataType : IndicesDataTypes.getAllTypes()) {
                testFormatWithMeshIndicesTypes(meshFormat, meshType, indicesDataType, format);
            }
        }
    }

    private void testFormatWithMeshIndicesTypes(IMeshFormat meshFormat, MeshType meshType, IndicesDataType<?> indicesDataType, VertexFormat format) throws MeshIOException {
        EditableMesh mesh = createRandomMesh(meshType, indicesDataType, format);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        meshFormat.write(mesh, baos);
        byte[] buffer = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        Set<VertexFormat> formats = new HashSet<VertexFormat>();
        formats.add(format);
        IMeshBuilder<ImmutableMesh> meshBuilder = new ImmutableMeshBuilder(meshType, indicesDataType, formats);
        ImmutableMesh translatedMesh = meshFormat.read(meshBuilder, bais);
        checkMeshes(mesh, translatedMesh, format);
    }

    private EditableMesh createRandomMesh(MeshType meshType, IndicesDataType<?> indicesDataType, VertexFormat format) {
        EditableMesh mesh = new EditableMesh();
        int faces = 3;
        int vertices = 3;
        mesh.setFaceCount(faces);
        mesh.setVertexCount(vertices);
        mesh.setMeshType(meshType);
        mesh.setIndicesDataType(indicesDataType);
        mesh.addVertexFormat(format);
        for (int i = 0; i < faces; i++) {
            int[] faceIndices = new int[3];
            for (int j = 0; j < 3; j++) {
                faceIndices[j] = RANDOM.nextInt(vertices);
            }
            mesh.setFaceIndices(i, faceIndices);
        }
        for (int i = 0; i < vertices; i++) {
            for (VertexType vertexType : format.getVertexTypes()) {
                // random numbers are either 0 or 1
                // negative numbers aren't supported for all vertex types
                // fractional parts aren't exactly represented by some formats
                float set = RANDOM.nextInt(2);
                mesh.setVertexDatum(i, vertexType, set);
                float get = mesh.getVertexDatum(i, vertexType);
                Assert.assertEquals(set, get, 0);
            }
        }
        return mesh;
    }

    private void checkMeshes(IMesh mesh, IMesh translatedMesh, VertexFormat format) {
        Assert.assertTrue(mesh.isValid());
        Assert.assertTrue(translatedMesh.isValid());
        Assert.assertEquals(mesh.getIndices(), translatedMesh.getIndices());
        ByteBuffer expectedVertices = mesh.getVertices(format);
        ByteBuffer actualVertices = translatedMesh.getVertices(format);
        Assert.assertEquals(expectedVertices.position(), actualVertices.position());
        Assert.assertEquals(expectedVertices.limit(), actualVertices.limit());
        Assert.assertEquals(expectedVertices.capacity(), actualVertices.capacity());
        for (int i = 0; i < expectedVertices.capacity(); i++) {
            byte expected = expectedVertices.get(i);
            byte actual = actualVertices.get(i);
            Assert.assertEquals("Byte position: " + i, expected, actual);
        }
    }
}
