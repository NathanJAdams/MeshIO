package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import com.ripplar_games.mesh_io.DatumEnDecoder;
import com.ripplar_games.mesh_io.TestUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class EditableVerticesTest {
    private static final float DELTA = 0;
    private static final Random RANDOM = new Random();

    @Ignore
    @Test
    public void testFloatPositionX() {
        EditableVertices vertices = new EditableVertices();
        VertexFormat format = new VertexFormat(new VertexSubFormat(VertexType.Position_X, VertexDataType.Float));
        vertices.setVertexCount(1);
        vertices.setVertexCount(2);
        vertices.setVertexDatum(0, VertexType.Position_X, 1);
        vertices.setVertexDatum(1, VertexType.Position_X, 11);
        ByteBuffer bb = vertices.getVerticesBuffer(format);
        FloatBuffer fb = bb.asFloatBuffer();
        Assert.assertEquals(1, fb.get(0), DELTA);
        Assert.assertEquals(11, fb.get(1), DELTA);
    }

    //    @Ignore
    @Test
    public void testVertices() {
        for (int i = 0; i < 100; i++) {
            EditableVertices vertices = new EditableVertices();
            VertexFormat format = TestUtil.randomVertexFormat();
            testVertices(vertices, format);
        }
    }

    private void testVertices(EditableVertices vertices, VertexFormat format) {
        int vertexCount = 2 + RANDOM.nextInt(2);
        vertices.setVertexCount(vertexCount);
        Assert.assertEquals(vertexCount, vertices.getVertexCount());
        int datumCounter = 0;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.getValues()) {
                float datum = (datumCounter % 3) / 2.0f;
                vertices.setVertexDatum(i, vertexType, datum);
                datumCounter++;
            }
        }
        ByteBuffer bb = vertices.getVerticesBuffer(format);
        Assert.assertEquals(vertexCount * format.getByteCount(), bb.limit());
        datumCounter = 0;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.getValues()) {
                float expected = (datumCounter % 3) / 2.0f;
                VertexAlignedSubFormat subFormat = format.getAlignedSubFormat(vertexType);
                if (subFormat != null) {
                    System.out.println("SubFormat: Offset: " + subFormat.getOffset() + ", DataType: " + subFormat.getDataType());
                    int offset = subFormat.getOffset();
                    System.out.println("Offset: " + offset);
                    int index = (i * format.getByteCount()) + offset;
                    float actual = subFormat.getDataType().getDatum(bb, index);
                    System.out.println("Index: " + index + ", Datum: " + actual);
                    System.out.println("Expected: " + expected);
                    System.out.println("Actual: " + actual);
                    Assert.assertEquals(expected, actual, DELTA);
                }
                datumCounter++;
            }
        }
    }
}
