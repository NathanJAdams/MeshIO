package com.ripplargames.meshio.formats;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.ripplargames.meshio.IMeshBuilder;
import com.ripplargames.meshio.IMeshFormat;
import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.meshformats.mbmsh.MbMshFormat;
import com.ripplargames.meshio.meshformats.obj.ObjFormat;
import com.ripplargames.meshio.meshformats.ply.PlyFormatAscii_1_0;
import com.ripplargames.meshio.meshformats.ply.PlyFormatBinaryBigEndian_1_0;
import com.ripplargames.meshio.meshformats.ply.PlyFormatBinaryLittleEndian_1_0;
import com.ripplargames.meshio.index.IndicesDataType;
import com.ripplargames.meshio.index.IndicesDataTypes;
import com.ripplargames.meshio.util.PrimitiveInputStream;
import com.ripplargames.meshio.util.PrimitiveOutputStream;
import com.ripplargames.meshio.mesh.EditableMesh;
import com.ripplargames.meshio.IMesh;
import com.ripplargames.meshio.mesh.ImmutableMesh;
import com.ripplargames.meshio.mesh.ImmutableMeshBuilder;
import com.ripplargames.meshio.mesh.MeshType;
import com.ripplargames.meshio.vertex.VertexDataType;
import com.ripplargames.meshio.vertex.VertexFormat;
import com.ripplargames.meshio.vertex.VertexFormatPart;
import com.ripplargames.meshio.vertex.VertexType;
import org.junit.Assert;
import org.junit.Test;

public class FormatTest {
    private static final Random RANDOM = new Random();

    @Test
    public void testFormats() throws MeshIOException {
        List<IMeshFormat> formats = new ArrayList<IMeshFormat>();
        formats.add(new PlyFormatAscii_1_0());
        formats.add(new PlyFormatBinaryBigEndian_1_0());
        formats.add(new PlyFormatBinaryLittleEndian_1_0());
        formats.add(new MbMshFormat());
        formats.add(new ObjFormat());
        testFormats(formats);
    }

    private void testFormats(List<IMeshFormat> formats) throws MeshIOException {
        for (int i = 0; i < 100; i++) {
            List<VertexFormatPart> formatParts = new ArrayList<VertexFormatPart>();
            VertexDataType[] vertexDataTypes = VertexDataType.values();
            for (VertexType vertexType : VertexType.valuesList()) {
                VertexDataType vertexDataType = vertexDataTypes[RANDOM.nextInt(vertexDataTypes.length)];
                formatParts.add(new VertexFormatPart(vertexType, vertexDataType));
            }
            VertexFormat format = new VertexFormat(formatParts);
            for (MeshType meshType : MeshType.values()) {
                for (IndicesDataType<?> indicesDataType : IndicesDataTypes.valuesList()) {
                    EditableMesh mesh = createRandomMesh(meshType, indicesDataType, format);
                    for (IMeshFormat meshFormat : formats) {
                        testFormatWithMesh(meshFormat, mesh, meshType, indicesDataType, format);
                    }
                }
            }
        }
    }

    private void testFormatWithMesh(IMeshFormat meshFormat, EditableMesh mesh, MeshType meshType, IndicesDataType<?> indicesDataType, VertexFormat format) throws MeshIOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrimitiveOutputStream pos = new PrimitiveOutputStream(baos);
        meshFormat.write(mesh, pos);
        byte[] buffer = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        PrimitiveInputStream pis = new PrimitiveInputStream(bais);
        Set<VertexFormat> formats = new HashSet<VertexFormat>();
        formats.add(format);
        IMeshBuilder<ImmutableMesh> meshBuilder = new ImmutableMeshBuilder(meshType, indicesDataType, formats);
        meshFormat.read(meshBuilder, pis);
        ImmutableMesh translatedMesh = meshBuilder.build();
        checkMeshes(meshFormat, mesh, translatedMesh, format);
    }

    private EditableMesh createRandomMesh(MeshType meshType, IndicesDataType<?> indicesDataType, VertexFormat format) {
        EditableMesh mesh = new EditableMesh();
        mesh.setMeshType(meshType);
        mesh.setIndicesDataType(indicesDataType);
        mesh.addVertexFormat(format);
        mesh.setFaceCount(2);
        int vertices = 4;
        mesh.setVertexCount(vertices);
        mesh.setFaceIndices(0, new int[]{0, 1, 2});
        mesh.setFaceIndices(1, new int[]{1, 2, 3});
        for (int i = 0; i < vertices; i++) {
            for (VertexType vertexType : format.getVertexTypes()) {
                int randomInt = RANDOM.nextInt(3);
                float set;
                // finer grained fractional parts aren't exactly represented by some formats
                if (vertexType.isSignedData()) {
                    set = randomInt - 1; // -1, 0, 1
                } else {
                    set = randomInt * 0.5f; // 0, 0.5, 1
                }
                mesh.setVertexDatum(i, vertexType, set);
                float get = mesh.getVertexDatum(i, vertexType);
                Assert.assertEquals(set, get, 0.0f);
            }
        }
        return mesh;
    }

    private void checkMeshes(IMeshFormat meshFormat, IMesh mesh, IMesh translatedMesh, VertexFormat format) {
        Assert.assertTrue(mesh.isValid());
        Assert.assertTrue(translatedMesh.isValid());
        Assert.assertEquals(mesh.getVertexCount(), translatedMesh.getVertexCount());
        Assert.assertEquals(mesh.getFaceCount(), translatedMesh.getFaceCount());
        Assert.assertEquals(mesh.getIndices(), translatedMesh.getIndices());
        ByteBuffer expectedVertices = mesh.getVertices(format);
        ByteBuffer actualVertices = translatedMesh.getVertices(format);
        Assert.assertEquals(expectedVertices.position(), actualVertices.position());
        Assert.assertEquals(expectedVertices.limit(), actualVertices.limit());
        Assert.assertEquals(expectedVertices.capacity(), actualVertices.capacity());
        Assert.assertEquals(format.getByteCount() * translatedMesh.getVertexCount(), expectedVertices.capacity());
        for (int i = 0; i < expectedVertices.capacity(); i++) {
            byte expected = expectedVertices.get(i);
            byte actual = actualVertices.get(i);
            Assert.assertEquals("Mesh format: " + meshFormat.getFileExtension() + ", Vertex index: " + i + ", Byte position: " + i, expected, actual);
        }
    }
}
