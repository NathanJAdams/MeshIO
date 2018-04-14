package com.ripplargames.meshio.indices;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.Mesh;
import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.util.BufferUtil;
import org.junit.Assert;
import org.junit.Test;

public class IndicesTest {
    @Test
    public void testTrianglesBytes() throws MeshIOException {
        test(new ByteIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new byte[0]), Collections.<Face>emptyList());
        test(new ByteIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new byte[3]), Arrays.asList(new Face(0, 0, 0)));
        test(new ByteIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new byte[]{0, 1, 2}), Arrays.asList(new Face(0, 1, 2)));
        test(new ByteIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new byte[]{0, 1, 2, 10, 11, 12}), Arrays.asList(new Face(0, 1, 2), new Face(10, 11, 12)));
    }

    @Test
    public void testTrianglesShorts() throws MeshIOException {
        test(new ShortIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new short[0]), Collections.<Face>emptyList());
        test(new ShortIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new short[3]), Arrays.asList(new Face(0, 0, 0)));
        test(new ShortIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new short[]{0, 1, 2}), Arrays.asList(new Face(0, 1, 2)));
        test(new ShortIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new short[]{0, 1, 2, 10, 11, 12}), Arrays.asList(new Face(0, 1, 2), new Face(10, 11, 12)));
    }

    @Test
    public void testTrianglesInts() throws MeshIOException {
        test(new IntIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new int[0]), Collections.<Face>emptyList());
        test(new IntIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new int[3]), Arrays.asList(new Face(0, 0, 0)));
        test(new IntIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new int[]{0, 1, 2}), Arrays.asList(new Face(0, 1, 2)));
        test(new IntIndicesDataType(), new TrianglesMeshType(), BufferUtil.with(new int[]{0, 1, 2, 10, 11, 12}), Arrays.asList(new Face(0, 1, 2), new Face(10, 11, 12)));
    }

    @Test
    public void testLinesBytes() throws MeshIOException {
        test(new ByteIndicesDataType(), new LinesMeshType(), BufferUtil.with(new byte[0]), Collections.<Face>emptyList());
        test(new ByteIndicesDataType(), new LinesMeshType(), BufferUtil.with(new byte[2]), Arrays.asList(new Face(0, 0, 0), new Face(0, 0, 0)));
        test(new ByteIndicesDataType(), new LinesMeshType(), BufferUtil.with(new byte[]{0, 1, 1, 2, 2, 0}), Arrays.asList(new Face(0, 1, 2)));
        test(new ByteIndicesDataType(), new LinesMeshType(), BufferUtil.with(new byte[]{0, 1, 1, 2, 2, 0, 10, 11, 11, 12, 12, 10}), Arrays.asList(new Face(0, 1, 2), new Face(10, 11, 12)));
    }

    @Test
    public void testLinesShorts() throws MeshIOException {
        test(new ShortIndicesDataType(), new LinesMeshType(), BufferUtil.with(new short[0]), Collections.<Face>emptyList());
        test(new ShortIndicesDataType(), new LinesMeshType(), BufferUtil.with(new short[2]), Arrays.asList(new Face(0, 0, 0), new Face(0, 0, 0)));
        test(new ShortIndicesDataType(), new LinesMeshType(), BufferUtil.with(new short[]{0, 1, 1, 2, 2, 0}), Arrays.asList(new Face(0, 1, 2)));
        test(new ShortIndicesDataType(), new LinesMeshType(), BufferUtil.with(new short[]{0, 1, 1, 2, 2, 0, 10, 11, 11, 12, 12, 10}), Arrays.asList(new Face(0, 1, 2), new Face(10, 11, 12)));
    }

    @Test
    public void testLinesInts() throws MeshIOException {
        test(new IntIndicesDataType(), new LinesMeshType(), BufferUtil.with(new int[0]), Collections.<Face>emptyList());
        test(new IntIndicesDataType(), new LinesMeshType(), BufferUtil.with(new int[2]), Arrays.asList(new Face(0, 0, 0), new Face(0, 0, 0)));
        test(new IntIndicesDataType(), new LinesMeshType(), BufferUtil.with(new int[]{0, 1, 1, 2, 2, 0}), Arrays.asList(new Face(0, 1, 2)));
        test(new IntIndicesDataType(), new LinesMeshType(), BufferUtil.with(new int[]{0, 1, 1, 2, 2, 0, 10, 11, 11, 12, 12, 10}), Arrays.asList(new Face(0, 1, 2), new Face(10, 11, 12)));
    }

    private void test(IndicesDataType dataType, IMeshType meshType, ByteBuffer expectedBuffer, List<Face> facesIndices) throws MeshIOException {
        Mesh mesh = new Mesh();
        for (Face face : facesIndices) {
            mesh.appendFace(face);
        }
        ByteBuffer actualBuffer = mesh.indices(meshType, dataType);
        Assert.assertEquals(expectedBuffer, actualBuffer);
    }
}
