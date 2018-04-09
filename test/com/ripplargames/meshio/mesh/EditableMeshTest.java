package com.ripplargames.meshio.mesh;

import java.nio.ByteBuffer;
import java.util.Random;

import com.ripplargames.meshio.TestUtil;
import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.index.IndicesDataType;
import com.ripplargames.meshio.index.IndicesDataTypes;
import com.ripplargames.meshio.vertex.VertexType;
import org.junit.Assert;
import org.junit.Test;

public class EditableMeshTest {
    private static final Random RANDOM = new Random();

    @Test
    public void testRandom() {
        EditableMesh mesh = new EditableMesh();
        for (int i = 0; i < 25000; i++) {
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
        MeshType meshType = TestUtil.randomValue(MeshType.valuesList());
        IndicesDataType<?> indicesDataType = TestUtil.randomValue(IndicesDataTypes.valuesList());
        mesh.setMeshType(meshType);
        mesh.setIndicesDataType(indicesDataType);
        ByteBuffer indices = mesh.getIndices();
        int expectedIndicesCount = meshType.getOffsetsLength() * mesh.getFaceCount();
        int actualIndicesCount = indices.capacity() / indicesDataType.bytesPerDatum();
        Assert.assertEquals(expectedIndicesCount, actualIndicesCount);
    }

    private void testVertexFormat(EditableMesh mesh) {
        BufferFormat format = TestUtil.randomVertexFormat();
        mesh.addBufferFormat(format);
        ByteBuffer vertices = mesh.getVertices(format);
        int expectedByteCount = mesh.getVertexCount() * format.getByteCount();
        int actualByteCount = vertices.limit() - vertices.position();
        Assert.assertEquals(expectedByteCount, actualByteCount);
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
            VertexType vertexType = TestUtil.randomValue(VertexType.valuesList());
            float vertexDatum = RANDOM.nextFloat();
            mesh.setVertexDatum(vertexIndex, vertexType, vertexDatum);
            float actualVertexDatum = mesh.getVertexDatum(vertexIndex, vertexType);
            Assert.assertEquals(vertexDatum, actualVertexDatum, 0);
        }
    }
}
