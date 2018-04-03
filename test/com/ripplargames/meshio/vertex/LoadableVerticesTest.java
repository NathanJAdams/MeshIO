package com.ripplargames.meshio.vertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.ripplargames.meshio.TestUtil;
import com.ripplargames.meshio.bufferformats.AlignedBufferFormatPart;
import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.bufferformats.BufferFormatPart;
import org.junit.Assert;
import org.junit.Test;

public class LoadableVerticesTest {
    private static final Random RANDOM = new Random();

    @Test
    public void testFloatPositionX() {
        BufferFormat format = new BufferFormat(new BufferFormatPart(VertexType.Position_X, VertexDataType.Float));
        Set<BufferFormat> formats = new HashSet<BufferFormat>();
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
    public void testResize() {
        for (int vertexCount = 1; vertexCount < 5; vertexCount++) {
            for (VertexDataType vertexDataType : VertexDataType.valuesList()) {
                int totalByteCount = vertexCount * vertexDataType.getByteCount();
                BufferFormat format = new BufferFormat(new BufferFormatPart(VertexType.Position_X, vertexDataType));
                Set<BufferFormat> formats = new HashSet<BufferFormat>();
                formats.add(format);
                LoadableVertices vertices = new LoadableVertices(formats);
                Map<BufferFormat, ByteBuffer> formatVertices = vertices.getFormatVertices();
                for (ByteBuffer bb : formatVertices.values()) {
                    Assert.assertEquals(0, bb.position());
                    Assert.assertEquals(0, bb.limit());
                    Assert.assertEquals(0, bb.capacity());
                }
                vertices.setVertexCount(vertexCount);
                formatVertices = vertices.getFormatVertices();
                for (ByteBuffer bb : formatVertices.values()) {
                    Assert.assertEquals(0, bb.position());
                    Assert.assertEquals(totalByteCount, bb.limit());
                    Assert.assertEquals(totalByteCount, bb.capacity());
                    for (int i = 0; i < vertexCount; i++) {
                        float datum = vertexDataType.getDatum(bb, i * vertexDataType.getByteCount());
                        Assert.assertEquals(0.0f, datum, 0.0f);
                    }
                }
                for (int i = 0; i < vertexCount; i++) {
                    vertices.setVertexDatum(i, VertexType.Position_X, 1.0f);
                }
                formatVertices = vertices.getFormatVertices();
                for (ByteBuffer bb : formatVertices.values()) {
                    Assert.assertEquals(0, bb.position());
                    Assert.assertEquals(totalByteCount, bb.limit());
                    Assert.assertEquals(totalByteCount, bb.capacity());
                    for (int i = 0; i < vertexCount; i++) {
                        float actual = vertexDataType.getDatum(bb, i * vertexDataType.getByteCount());
                        Assert.assertEquals(1.0f, actual, 0.0f);
                    }
                }
            }
        }
    }

    @Test
    public void testVertices() {
        for (int i = 0; i < 1000; i++) {
            BufferFormat format = TestUtil.randomVertexFormat();
            Set<BufferFormat> formats = new HashSet<BufferFormat>();
            formats.add(format);
            LoadableVertices vertices = new LoadableVertices(formats);
            testVertices(vertices, format);
        }
    }

    private void testVertices(LoadableVertices vertices, BufferFormat format) {
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
        ByteBuffer bb = vertices.getFormatVertices().get(format);
        Assert.assertEquals(vertexCount * format.getByteCount(), bb.limit());
        float expected = startDatum;
        for (int i = 0; i < vertexCount; i++) {
            for (VertexType vertexType : VertexType.valuesList()) {
                AlignedBufferFormatPart alignedFormatPart = format.getAlignedFormatPart(vertexType);
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
