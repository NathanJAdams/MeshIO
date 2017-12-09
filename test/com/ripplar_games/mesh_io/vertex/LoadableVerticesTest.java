package com.ripplar_games.mesh_io.vertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.ripplar_games.mesh_io.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class LoadableVerticesTest {
    private static final Random RANDOM = new Random();

    @Test
    public void testFloatPositionX() {
        VertexFormat format = new VertexFormat(new VertexSubFormat(VertexType.Position_X, VertexDataType.Float));
        Set<VertexFormat> formats = new HashSet<VertexFormat>();
        formats.add(format);
        LoadableVertices vertices = new LoadableVertices(formats);
        vertices.setVertexCount(1);
        vertices.setVertexCount(2);
        vertices.setVertexCount(3);
        vertices.setVertexDatum(0, VertexType.Position_X, 0.0f);
        vertices.setVertexDatum(1, VertexType.Position_X, 0.5f);
        vertices.setVertexDatum(2, VertexType.Position_X, 1.0f);
        ByteBuffer bb = vertices.getFormatVertices().get(format);
        FloatBuffer fb = bb.asFloatBuffer();
        Assert.assertEquals(0.0f, fb.get(0), 0);
        Assert.assertEquals(0.5f, fb.get(1), 0);
        Assert.assertEquals(1.0f, fb.get(2), 0);
    }

    @Test
    public void testVertices() {
        for (int i = 0; i < 1000; i++) {
            VertexFormat format = TestUtil.randomVertexFormat();
            Set<VertexFormat> formats = new HashSet<VertexFormat>();
            formats.add(format);
            LoadableVertices vertices = new LoadableVertices(formats);
            testVertices(vertices, format);
        }
    }

    private void testVertices(LoadableVertices vertices, VertexFormat format) {
        int vertexCount = 2 + RANDOM.nextInt(2);
        vertices.setVertexCount(vertexCount);
        Assert.assertEquals(vertexCount, vertices.getVertexCount());
        float startDatum = RANDOM.nextInt(128) / 128.0f;
        float datum = startDatum;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.getValues()) {
                vertices.setVertexDatum(i, vertexType, datum);
                datum += (1.0f / 128.0f);
                if (datum > 1) {
                    datum = 0;
                }
            }
        }
        ByteBuffer bb = vertices.getFormatVertices().get(format);
        Assert.assertEquals(vertexCount * format.getByteCount(), bb.limit());
        float expected = startDatum;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.getValues()) {
                VertexAlignedSubFormat subFormat = format.getAlignedSubFormat(vertexType);
                if (subFormat != null) {
                    int index = format.getVertexDatumIndex(i, vertexType);
                    float actual = subFormat.getDataType().getDatum(bb, index);
                    float delta;
                    switch (subFormat.getDataType()) {
                        case Float:
                            delta = 0;
                            break;
                        case NormalisedSignedByte:
                            delta = 2.0f / 256.0f;
                            break;
                        case NormalisedUnsignedByte:
                            delta = 1.0f / 256.0f;
                            break;
                        case NormalisedSignedShort:
                            delta = 2.0f / 256.0f / 256.0f;
                            break;
                        case NormalisedUnsignedShort:
                            delta = 1.0f / 256.0f / 256.0f;
                            break;
                        case NormalisedSignedInt:
                            delta = 2.0f / 256.0f / 256.0f / 256.0f / 256.0f;
                            break;
                        case NormalisedUnsignedInt:
                            delta = 1.0f / 256.0f / 256.0f / 256.0f / 256.0f;
                            break;
                        default:
                            delta = -1;
                    }
                    Assert.assertEquals(expected, actual, delta);
                }
                expected += (1.0f / 128.0f);
                if (expected > 1) {
                    expected = 0;
                }
            }
        }
    }
}
