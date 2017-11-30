package com.ripplar_games.mesh_io.mesh;

import com.ripplar_games.mesh_io.index.IndicesDataType;
import com.ripplar_games.mesh_io.index.IndicesDataTypes;
import com.ripplar_games.mesh_io.vertex.VertexDataType;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexSubFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EditableMeshTest {
    private static final float DELTA = 1E-9f;
    private static final Random RANDOM = new Random();
    private static final MeshType[] MESH_TYPES = MeshType.values();
    private static final VertexType[] VERTEX_TYPES = VertexType.getValues();
    private static final List<IndicesDataType<?>> INDICES_DATA_TYPES = IndicesDataTypes.getAllTypes();
    private static final VertexDataType[] VERTEX_DATA_TYPES = VertexDataType.values();

    @Test
    public void testRandom() {
        EditableMesh mesh = new EditableMesh();
        for (int i = 0; i < 10000; i++) {
            int testIndex = RANDOM.nextInt(6);
            switch (testIndex) {
                case 0:
                    testFaceCount(mesh);
                    break;
                case 1:
                    testVertexCount(mesh);
                    break;
                case 2:
                    testMeshAndIndicesTypes(mesh);
                    break;
                case 3:
                    testVertexFormat(mesh);
                    break;
                case 4:
                    testFaceIndices(mesh);
                    break;
                case 5:
                    testVertexData(mesh);
                    break;
                default:
                    Assert.fail();
            }
        }
    }

    private void testFaceCount(EditableMesh mesh) {
        int faces = RANDOM.nextInt(10);
        mesh.setFaceCount(faces);
        Assert.assertEquals(faces, mesh.getFaceCount());
    }

    private void testVertexCount(EditableMesh mesh) {
        int vertices = RANDOM.nextInt(10);
        mesh.setVertexCount(vertices);
        Assert.assertEquals(vertices, mesh.getVertexCount());
    }

    private void testMeshAndIndicesTypes(EditableMesh mesh) {
        MeshType meshType = randomMeshType();
        IndicesDataType<?> indicesDataType = randomIndicesDataType();
        mesh.setMeshType(meshType);
        mesh.setIndicesDataType(indicesDataType);
        ByteBuffer indices = mesh.getIndices();
        int expectedIndicesCount = meshType.getOffsetsLength() * mesh.getFaceCount();
        int actualIndicesCount = indices.capacity() / indicesDataType.bytesPerDatum();
        Assert.assertEquals(expectedIndicesCount, actualIndicesCount);
    }

    private void testVertexFormat(EditableMesh mesh) {
        VertexFormat format = randomVertexFormat();
        mesh.addVertexFormat(format);
        ByteBuffer vertices = mesh.getVertices(format);
        int expectedByteCount = mesh.getVertexCount() * format.getByteCount();
        Assert.assertEquals(expectedByteCount, vertices.capacity());
    }

    private void testFaceIndices(EditableMesh mesh) {
        if (mesh.getFaceCount() > 0 && mesh.getVertexCount() > 0) {
            int faceIndex = RANDOM.nextInt(mesh.getFaceCount());
            int indicesIndex = RANDOM.nextInt(3);
            int vertexIndex = RANDOM.nextInt(mesh.getVertexCount());
            mesh.setFaceIndicesIndex(faceIndex, indicesIndex, vertexIndex);
            int actualVertexIndex = mesh.getFaceIndices(faceIndex)[indicesIndex];
            Assert.assertEquals(vertexIndex, actualVertexIndex);
        }
    }

    private void testVertexData(EditableMesh mesh) {
        if (mesh.getVertexCount() > 0 && mesh.getVertexCount() > 0) {
            int vertexIndex = RANDOM.nextInt(mesh.getVertexCount());
            VertexType vertexType = randomVertexType();
            float vertexDatum = RANDOM.nextFloat();
            mesh.setVertexDatum(vertexIndex, vertexType, vertexDatum);
            float actualVertexDatum = mesh.getVertexDatum(vertexIndex, vertexType);
            Assert.assertEquals(vertexDatum, actualVertexDatum, DELTA);
        }
    }

    private VertexFormat randomVertexFormat() {
        int numSubFormats = RANDOM.nextInt(5);
        List<VertexSubFormat> subFormats = new ArrayList<VertexSubFormat>();
        for (int i = 0; i < numSubFormats; i++) {
            subFormats.add(randomVertexSubFormat());
        }
        return new VertexFormat(subFormats);
    }

    private VertexSubFormat randomVertexSubFormat() {
        VertexType vertexType = randomVertexType();
        VertexDataType vertexDataType = randomVertexDataType();
        return new VertexSubFormat(vertexType, vertexDataType);
    }

    private MeshType randomMeshType() {
        return MESH_TYPES[RANDOM.nextInt(MESH_TYPES.length)];
    }

    private VertexType randomVertexType() {
        return VERTEX_TYPES[RANDOM.nextInt(VERTEX_TYPES.length)];
    }

    private IndicesDataType<?> randomIndicesDataType() {
        return INDICES_DATA_TYPES.get(RANDOM.nextInt(INDICES_DATA_TYPES.size()));
    }

    private VertexDataType randomVertexDataType() {
        return VERTEX_DATA_TYPES[RANDOM.nextInt(VERTEX_DATA_TYPES.length)];
    }
}
