package tests.meshio.mesh;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import meshio.MeshVertexType;
import meshio.mesh.BufferExt;
import meshio.mesh.EditableMesh;
import meshio.mesh.IndicesDataType;
import meshio.mesh.IndicesDataTypes;
import meshio.mesh.MeshIndexType;

public class MeshTest {
   private static final float DELTA = (float) 1E-12;
   private EditableMesh       mesh;

   @Before
   public void createMesh() {
      float[] v0 = new float[] { -0.5f, -0.5f, 0, 11, 12, 13, 1, 0, 0, 1, 101, 102 };
      float[] v1 = new float[] { -0.5f, 0.5f, 0, 21, 22, 23, 0, 1, 0, 1, 201, 202 };
      float[] v2 = new float[] { 0.5f, -0.5f, 0, 31, 32, 33, 0, 0, 1, 1, 301, 302 };
      float[] v3 = new float[] { 0.5f, 0.5f, 0, 41, 42, 43, 1, 1, 1, 1, 401, 402 };
      int[] f0 = new int[] { 0, 1, 2 };
      int[] f1 = new int[] { 1, 2, 3 };
      this.mesh = new EditableMesh();
      this.mesh.setVertexFormat(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z, MeshVertexType.Normal_X,
            MeshVertexType.Normal_Y, MeshVertexType.Normal_Z, MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B,
            MeshVertexType.Color_A, MeshVertexType.ImageCoord_X, MeshVertexType.ImageCoord_Y);
      this.mesh.setVertexCount(4);
      this.mesh.setVertexData(0, v0);
      this.mesh.setVertexData(1, v1);
      this.mesh.setVertexData(2, v2);
      this.mesh.setVertexData(3, v3);
      this.mesh.setFaceCount(2);
      this.mesh.setFaceIndices(0, f0);
      this.mesh.setFaceIndices(1, f1);
   }

   @Test
   public void testIndices() {
      testIndices(new short[] { 0, 1, 2, 1, 2, 3 }, MeshIndexType.Mesh, IndicesDataTypes.Short);
      testIndices(new short[] { 0, 1, 1, 2, 2, 0, 1, 2, 2, 3, 3, 1 }, MeshIndexType.Outline, IndicesDataTypes.Short);
   }

   private void testIndices(short[] expectedIndices, MeshIndexType meshIndexType, IndicesDataType<?> indicesDataType) {
      mesh.setMeshIndexType(meshIndexType);
      ByteBuffer expectedModelIndices = BufferExt.with(expectedIndices);
      ByteBuffer modelIndices = mesh.getIndices();
      Assert.assertEquals(0, expectedModelIndices.compareTo(modelIndices));
   }

   @Test
   public void testVertices() {
      testVertices(0, new float[] { -0.5f, -0.5f, 0, 11, 12, 13, 1, 0, 0, 1, 101, 102 });
      testVertices(1, new float[] { -0.5f, 0.5f, 0, 21, 22, 23, 0, 1, 0, 1, 201, 202 });
      testVertices(2, new float[] { 0.5f, -0.5f, 0, 31, 32, 33, 0, 0, 1, 1, 301, 302 });
      testVertices(3, new float[] { 0.5f, 0.5f, 0, 41, 42, 43, 1, 1, 1, 1, 401, 402 });
   }

   private void testVertices(int vertexIndex, float[] expectedVertices) {
      float[] vertexData = new float[12];
      mesh.getVertexData(vertexIndex, vertexData);
      Assert.assertArrayEquals(expectedVertices, vertexData, DELTA);
   }
}
