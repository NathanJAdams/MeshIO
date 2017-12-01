package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.Random;

import com.ripplar_games.mesh_io.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class EditableVerticesTest {
    private static final float DELTA = 1E-9f;
    private static final Random RANDOM = new Random();

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
        EnumMap<VertexType, Float>[] vertexDatumMaps = new EnumMap[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            EnumMap<VertexType, Float> vertexDatumMap = new EnumMap<VertexType, Float>(VertexType.class);
            vertexDatumMaps[i] = vertexDatumMap;
            for (VertexType vertexType : VertexType.getValues()) {
                vertexDatumMap.put(vertexType, datum);
                datum += 1;
            }
        }
        for (int i = 0; i < vertexCount; i++) {
            EnumMap<VertexType, Float> vertexDatumMap = vertexDatumMaps[i];
            for (VertexType vertexType : VertexType.getValues()) {
                datum = vertexDatumMap.get(vertexType);
                vertices.setVertexDatum(i, vertexType, datum);
            }
        }
        ByteBuffer bb = vertices.getVerticesBuffer(format);
        FloatBuffer fb = bb.asFloatBuffer();
        Assert.assertEquals(vertexCount * format.getByteCount(), bb.limit());
        System.out.println("NumVertexTypes:" + format.getVertexTypeCount());
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : format.getVertexTypes()) {
                VertexAlignedSubFormat subFormat = format.getAlignedSubFormat(vertexType);
                int offset = subFormat.getOffset();
                System.out.println("Offset: " + offset);
                int position = i * format.getVertexTypeCount() + offset;
                float expected = vertexDatumMaps[i].get(vertexType);
                float actual = fb.get(position);
                System.out.println(expected);
                System.out.println(actual);
//                Assert.assertEquals(expected, actual, DELTA);
            }
        }
    }
}
