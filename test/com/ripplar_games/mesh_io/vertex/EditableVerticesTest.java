package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import com.ripplar_games.mesh_io.TestUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class EditableVerticesTest {
    private static final float DELTA = 1E-9f;
    private static final Random RANDOM = new Random();

    @Test
    public void testFloatPositionX() {
        EditableVertices vertices = new EditableVertices();
        VertexFormat format = new VertexFormat(new VertexSubFormat(VertexType.Position_X, VertexDataType.Float));
        vertices.setVertexCount(1);
        vertices.addFormat(format);
        vertices.setVertexCount(2);
        vertices.setVertexDatum(0, VertexType.Position_X, 1);
        ByteBuffer bb = vertices.getVerticesBuffer(format);
        FloatBuffer fb = bb.asFloatBuffer();
        float datum = fb.get(0);
        Assert.assertEquals(1, datum, DELTA);
    }

    @Ignore
    @Test
    public void testVertices() {
        EditableVertices vertices = new EditableVertices();
        VertexFormat format = TestUtil.randomVertexFormat();
        vertices.addFormat(format);
        for (int i = 0; i < 1; i++) {
            testVertices(vertices, format);
        }
    }

    private void testVertices(EditableVertices vertices, VertexFormat format) {
        int vertexCount = 5 + RANDOM.nextInt(5);
        vertices.setVertexCount(vertexCount);
        float datum = 0;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.getValues()) {
                vertices.setVertexDatum(i, vertexType, datum);
                datum++;
            }
        }
        ByteBuffer bb = vertices.getVerticesBuffer(format);
        Assert.assertEquals(vertexCount * format.getByteCount(), bb.limit());
        System.out.println("NumVertexTypes:" + format.getVertexTypeCount());
        float expected = 0;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : format.getVertexTypes()) {
                VertexAlignedSubFormat subFormat = format.getAlignedSubFormat(vertexType);
                int offset = subFormat.getOffset();
                System.out.println("Offset: " + offset);
                int index = i * format.getVertexTypeCount() + offset;
                float actual = subFormat.getDataType().getDatum(bb, index);
                System.out.println(expected);
                System.out.println(actual);
                Assert.assertEquals(expected, actual, DELTA);
                expected++;
            }
        }
    }
}
