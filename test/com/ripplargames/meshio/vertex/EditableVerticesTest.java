package com.ripplargames.meshio.vertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import com.ripplargames.meshio.TestUtil;
import org.junit.Assert;
import org.junit.Test;

public class EditableVerticesTest {
    private static final Random RANDOM = new Random();

    @Test
    public void testFloatPositionX() {
        EditableVertices vertices = new EditableVertices();
        VertexFormat format = new VertexFormat(new VertexFormatPart(VertexType.Position_X, VertexDataType.Float));
        vertices.setVertexCount(1);
        vertices.setVertexCount(2);
        vertices.setVertexDatum(0, VertexType.Position_X, 1);
        vertices.setVertexDatum(1, VertexType.Position_X, 11);
        ByteBuffer bb = vertices.getVerticesBuffer(format);
        FloatBuffer fb = bb.asFloatBuffer();
        Assert.assertEquals(1, fb.get(0), 0);
        Assert.assertEquals(11, fb.get(1), 0);
    }

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
        float startDatum = RANDOM.nextInt(128) / 128.0f;
        float datum = startDatum;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.valuesList()) {
                vertices.setVertexDatum(i, vertexType, datum);
                datum += (1.0f / 128.0f);
                if (datum > 1) {
                    datum = 0;
                }
            }
        }
        ByteBuffer bb = vertices.getVerticesBuffer(format);
        Assert.assertEquals(vertexCount * format.getByteCount(), bb.limit());
        float expected = startDatum;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.valuesList()) {
                AlignedVertexFormatPart alignedFormatPart = format.getAlignedFormatPart(vertexType);
                if (alignedFormatPart != null) {
                    int index = format.getVertexDatumIndex(i, vertexType);
                    float actual = alignedFormatPart.getDataType().getDatum(bb, index);
                    float delta;
                    switch (alignedFormatPart.getDataType()) {
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
