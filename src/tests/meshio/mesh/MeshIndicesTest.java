package tests.meshio.mesh;

import org.junit.Assert;
import org.junit.Test;

import meshio.mesh.IndicesDataTypes;
import meshio.mesh.MeshIndexType;
import meshio.mesh.MeshIndices;

public class MeshIndicesTest {
   @Test
   public void testFaceCount() {
      MeshIndices<byte[]> indices = new MeshIndices<>(IndicesDataTypes.Byte, MeshIndexType.Mesh);
      assertIndicesDataLength(indices, MeshIndexType.Mesh, 0);
      indices.setFaceCount(3);
      assertIndicesDataLength(indices, MeshIndexType.Mesh, 3);
   }

   @Test
   public void testMeshIndexTypes() {
      for (MeshIndexType meshIndexType : MeshIndexType.values())
         testMeshIndexType(meshIndexType);
   }

   private void testMeshIndexType(MeshIndexType meshIndexType) {
      MeshIndices<byte[]> indices = new MeshIndices<>(IndicesDataTypes.Byte, meshIndexType);
      indices.setFaceCount(3);
      assertIndicesDataLength(indices, meshIndexType, 3);
   }

   private void assertIndicesDataLength(MeshIndices<byte[]> indices, MeshIndexType meshIndexType, int expectedLength) {
      Assert.assertEquals(expectedLength, indices.getFaceCount());
      Assert.assertEquals(expectedLength * meshIndexType.getOffsetsLength(), indices.getIndicesBuffer().capacity());
   }

   @Test
   public void testFaceIndices() {
      MeshIndices<byte[]> indices = new MeshIndices<>(IndicesDataTypes.Byte, MeshIndexType.Mesh);
      indices.setFaceCount(5);
      for (int faceIndex = 0; faceIndex < 5; faceIndex++)
         for (int faceCornerIndex = 0; faceIndex < 3; faceIndex++)
            for (int index = 0; index < 10; index++)
               testFaceIndex(indices, faceIndex, faceCornerIndex, index);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testFaceIndexLow() {
      testFaceIndexArrayBounds(0, -1, 0);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testFaceIndexHigh() {
      testFaceIndexArrayBounds(1, 2, 0);
   }

   @Test
   public void testFaceCornerIndexLow() {
      testFaceIndexArrayBounds(1, 0, -1);
   }

   @Test
   public void testFaceCornerIndexHigh() {
      testFaceIndexArrayBounds(1, 0, 3);
   }

   public void testFaceIndexArrayBounds(int faceCount, int faceIndex, int faceCornerIndex) {
      MeshIndices<byte[]> indices = new MeshIndices<>(IndicesDataTypes.Byte, MeshIndexType.Mesh);
      indices.setFaceCount(faceCount);
      indices.setFaceIndex(faceIndex, faceCornerIndex, 0);
   }

   private void testFaceIndex(MeshIndices<byte[]> indices, int faceIndex, int faceCornerIndex, int expectedIndex) {
      indices.setFaceIndex(faceIndex, faceCornerIndex, expectedIndex);
      int[] faceIndices = new int[3];
      indices.getFaceIndices(faceIndex, faceIndices);
      Assert.assertEquals(expectedIndex, faceIndices[faceCornerIndex]);
   }
}
