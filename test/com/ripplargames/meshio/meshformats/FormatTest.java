package com.ripplargames.meshio.meshformats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.IMeshFormat;
import com.ripplargames.meshio.Mesh;
import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.meshformats.mbmsh.MbMshFormat;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.vertices.VertexType;
import org.junit.Assert;
import org.junit.Test;

public class FormatTest {
    @Test
    public void testFormats() throws MeshIOException {
        List<IMeshFormat> formats = new ArrayList<IMeshFormat>();
//        meshformats.add(new PlyFormatAscii_1_0());
//        meshformats.add(new PlyFormatBinaryBigEndian_1_0());
//        meshformats.add(new PlyFormatBinaryLittleEndian_1_0());
        formats.add(new MbMshFormat());
//        meshformats.add(new ObjFormat());
        testFormats(formats);
    }

    private void testFormats(List<IMeshFormat> formats) throws MeshIOException {
        Mesh meshWritten = createRandomMesh();
        for (IMeshFormat meshFormat : formats) {
            testFormatIndices(meshWritten, meshFormat);
        }
    }

    private Mesh createRandomMesh() {
        Random random = new Random();
        Mesh mesh = new Mesh();
        int vertices = 4;
        mesh.appendFace(new Face(0, 1, 2));
        mesh.appendFace(new Face(1, 2, 3));
        for (int i = 0; i < vertices; i++) {
            for (VertexType vertexType : VertexType.valuesList()) {
                int randomInt = random.nextInt(3);
                float set;
                // finer grained fractional parts aren't exactly represented by some meshformats
                if (vertexType.isSignedData()) {
                    set = randomInt - 1; // -1, 0, 1
                } else {
                    set = randomInt * 0.5f; // 0, 0.5, 1
                }
                mesh.setVertexTypeDatum(vertexType, i, set);
                float get = mesh.vertexTypeDatum(vertexType, i);
                Assert.assertEquals(set, get, 0.0f);
            }
        }
        return mesh;
    }

    private void testFormatIndices(Mesh meshWritten, IMeshFormat meshFormat) throws MeshIOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrimitiveOutputStream pos = new PrimitiveOutputStream(baos);
        meshFormat.write(meshWritten, pos);
        byte[] buffer = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        PrimitiveInputStream pis = new PrimitiveInputStream(bais);
        Mesh meshRead = meshFormat.read(pis);
        checkMeshes(meshFormat, meshWritten, meshRead);
    }

    private void checkMeshes(IMeshFormat meshFormat, Mesh meshWritten, Mesh meshRead) throws MeshIOException {
        String extension = meshFormat.getFileExtension();
        Assert.assertTrue(extension, meshWritten.isValid());
        Assert.assertTrue(extension, meshRead.isValid());
        Assert.assertEquals(extension, meshWritten.vertexCount(), meshRead.vertexCount());
        Assert.assertEquals(extension, meshWritten.faceCount(), meshRead.faceCount());
        List<Face> facesWritten = meshWritten.faces();
        List<Face> facesRead = meshRead.faces();
        Assert.assertEquals(extension, facesWritten, facesRead);

        for (VertexType vertexType : meshWritten.vertexTypes()) {
            Assert.assertTrue(extension + ":" + vertexType.name(), meshRead.hasVertexTypeData(vertexType));
        }
        for (VertexType vertexType : meshRead.vertexTypes()) {
            Assert.assertTrue(extension + ":" + vertexType.name(), meshWritten.hasVertexTypeData(vertexType));
        }
        for (VertexType vertexType : meshWritten.vertexTypes()) {
            float[] verticesWritten = meshWritten.vertexTypeData(vertexType).copyArray();
            float[] verticesRead = meshRead.vertexTypeData(vertexType).copyArray();
            Assert.assertArrayEquals(extension + ":" + vertexType, verticesWritten, verticesRead, 0.0f);
        }
    }
}
