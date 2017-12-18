package com.ripplargames.mesh_io.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ripplargames.mesh_io.mesh.MeshType;
import org.junit.Assert;
import org.junit.Test;

public class EditableIndicesTest {
    @Test
    public void testMeshBytes() {
        test(IndicesDataTypes.Byte, MeshType.Mesh, new byte[0], Collections.<int[]>emptyList());
        test(IndicesDataTypes.Byte, MeshType.Mesh, new byte[3], Arrays.asList(new int[]{0, 0, 0}));
        test(IndicesDataTypes.Byte, MeshType.Mesh, new byte[]{0, 1, 2}, Arrays.asList(new int[]{0, 1, 2}));
        test(IndicesDataTypes.Byte, MeshType.Mesh, new byte[]{0, 1, 2, 10, 11, 12}, Arrays.asList(new int[]{0, 1, 2}, new int[]{10, 11, 12}));
    }

    @Test
    public void testMeshShorts() {
        test(IndicesDataTypes.Short, MeshType.Mesh, new short[0], Collections.<int[]>emptyList());
        test(IndicesDataTypes.Short, MeshType.Mesh, new short[3], Arrays.asList(new int[]{0, 0, 0}));
        test(IndicesDataTypes.Short, MeshType.Mesh, new short[]{0, 1, 2}, Arrays.asList(new int[]{0, 1, 2}));
        test(IndicesDataTypes.Short, MeshType.Mesh, new short[]{0, 1, 2, 10, 11, 12}, Arrays.asList(new int[]{0, 1, 2}, new int[]{10, 11, 12}));
    }

    @Test
    public void testMeshInts() {
        test(IndicesDataTypes.Int, MeshType.Mesh, new int[0], Collections.<int[]>emptyList());
        test(IndicesDataTypes.Int, MeshType.Mesh, new int[3], Arrays.asList(new int[]{0, 0, 0}));
        test(IndicesDataTypes.Int, MeshType.Mesh, new int[]{0, 1, 2}, Arrays.asList(new int[]{0, 1, 2}));
        test(IndicesDataTypes.Int, MeshType.Mesh, new int[]{0, 1, 2, 10, 11, 12}, Arrays.asList(new int[]{0, 1, 2}, new int[]{10, 11, 12}));
    }

    @Test
    public void testOutlineBytes() {
        test(IndicesDataTypes.Byte, MeshType.Outline, new byte[0], Collections.<int[]>emptyList());
        test(IndicesDataTypes.Byte, MeshType.Outline, new byte[6], Arrays.asList(new int[]{0, 0, 0, 0, 0, 0}));
        test(IndicesDataTypes.Byte, MeshType.Outline, new byte[]{0, 1, 1, 2, 2, 0}, Arrays.asList(new int[]{0, 1, 2}));
        test(IndicesDataTypes.Byte, MeshType.Outline, new byte[]{0, 1, 1, 2, 2, 0, 10, 11, 11, 12, 12, 10}, Arrays.asList(new int[]{0, 1, 2}, new int[]{10, 11, 12}));
    }

    @Test
    public void testOutlineShorts() {
        test(IndicesDataTypes.Short, MeshType.Outline, new short[0], Collections.<int[]>emptyList());
        test(IndicesDataTypes.Short, MeshType.Outline, new short[6], Arrays.asList(new int[]{0, 0, 0, 0, 0, 0}));
        test(IndicesDataTypes.Short, MeshType.Outline, new short[]{0, 1, 1, 2, 2, 0}, Arrays.asList(new int[]{0, 1, 2}));
        test(IndicesDataTypes.Short, MeshType.Outline, new short[]{0, 1, 1, 2, 2, 0, 10, 11, 11, 12, 12, 10}, Arrays.asList(new int[]{0, 1, 2}, new int[]{10, 11, 12}));
    }

    @Test
    public void testOutlineInts() {
        test(IndicesDataTypes.Int, MeshType.Outline, new int[0], Collections.<int[]>emptyList());
        test(IndicesDataTypes.Int, MeshType.Outline, new int[6], Arrays.asList(new int[]{0, 0, 0, 0, 0, 0}));
        test(IndicesDataTypes.Int, MeshType.Outline, new int[]{0, 1, 1, 2, 2, 0}, Arrays.asList(new int[]{0, 1, 2}));
        test(IndicesDataTypes.Int, MeshType.Outline, new int[]{0, 1, 1, 2, 2, 0, 10, 11, 11, 12, 12, 10}, Arrays.asList(new int[]{0, 1, 2}, new int[]{10, 11, 12}));
    }

    private <T> void test(IndicesDataType<T> dataType, MeshType meshType, T array, List<int[]> facesIndices) {
        EditableIndices<T> indices = new EditableIndices<T>(dataType, meshType);
        indices.clear();
        indices.setFaceCount(facesIndices.size());
        for (int i = 0; i < facesIndices.size(); i++) {
            indices.setFaceIndices(i, facesIndices.get(i));
        }
        Assert.assertEquals(facesIndices.size(), indices.getFaceCount());
        Assert.assertEquals(dataType.toByteBuffer(array), indices.getIndicesBuffer());
    }
}
