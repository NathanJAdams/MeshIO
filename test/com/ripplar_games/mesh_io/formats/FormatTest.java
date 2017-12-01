package com.ripplar_games.mesh_io.formats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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
        testFormat(new PlyFormatAscii_1_0(), false);
        testFormat(new PlyFormatBinaryBigEndian_1_0(), false);
        testFormat(new PlyFormatBinaryLittleEndian_1_0(), false);
//        testFormat(new ObjFormat(),false);
        testFormat(new MbMshFormat(), false);
    }

    private void testFormat(IMeshFormat meshFormat, boolean checkVertices) throws MeshIOException {
        for (MeshType meshType : MeshType.values()) {
            for (IndicesDataType<?> indicesDataType : IndicesDataTypes.getAllTypes()) {
                List<VertexType> vertexTypes = createVertexTypes();
                VertexFormat format = createFormat(vertexTypes);
                EditableMesh mesh = createRandomMesh(meshType, indicesDataType, vertexTypes, format);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                meshFormat.write(mesh, baos);
                byte[] buffer = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                Set<VertexFormat> formats = new HashSet<VertexFormat>();
                formats.add(format);
                IMeshBuilder<ImmutableMesh> meshBuilder = new ImmutableMeshBuilder(meshType, indicesDataType, formats);
                ImmutableMesh translatedMesh = meshFormat.read(meshBuilder, bais);
                checkMeshes(mesh, translatedMesh, format, checkVertices);
            }
        }
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

    private EditableMesh createRandomMesh(MeshType meshType, IndicesDataType<?> indicesDataType, List<VertexType> vertexTypes, VertexFormat format) {
        EditableMesh mesh = new EditableMesh();
        int faces = 5 + RANDOM.nextInt(5);
        int vertices = 5 + RANDOM.nextInt(5);
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
            VertexType vertexType = vertexTypes.get(RANDOM.nextInt(vertexTypes.size()));
            mesh.setVertexDatum(i, vertexType, RANDOM.nextFloat());
        }
        return mesh;
    }

    private void checkMeshes(IMesh mesh, IMesh translatedMesh, VertexFormat format, boolean checkVertices) {
        Assert.assertTrue(mesh.isValid());
        Assert.assertTrue(translatedMesh.isValid());
        Assert.assertEquals(mesh.getIndices(), translatedMesh.getIndices());
        ByteBuffer expectedVertices = mesh.getVertices(format);
        ByteBuffer actualVertices = translatedMesh.getVertices(format);
        Assert.assertEquals(expectedVertices.position(), actualVertices.position());
        Assert.assertEquals(expectedVertices.limit(), actualVertices.limit());
        Assert.assertEquals(expectedVertices.capacity(), actualVertices.capacity());
        if (checkVertices) {
            Assert.assertEquals(expectedVertices, actualVertices);
        }
    }
}
