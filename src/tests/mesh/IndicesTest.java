package tests.mesh;

import org.junit.Assert;
import org.junit.Test;

import meshio.mesh.IndicesDataTypes;
import meshio.mesh.MeshIndexType;
import meshio.mesh.MeshIndices;

public class IndicesTest {
   @Test
   public void testFaceCount() {
      MeshIndices<byte[]> indices = new MeshIndices<>(IndicesDataTypes.Byte);
      assertIndicesDataLength(indices, 0);
      indices.setFaceCount(3);
      assertIndicesDataLength(indices, 3);
   }

   private void assertIndicesDataLength(MeshIndices<byte[]> indices, int expectedLength) {
      Assert.assertEquals(expectedLength, indices.getFaceCount());
      Assert.assertEquals(expectedLength * MeshIndexType.Mesh.getOffsetsLength(), indices.getIndicesData(MeshIndexType.Mesh).length);
      Assert.assertEquals(expectedLength * MeshIndexType.Outline.getOffsetsLength(), indices.getIndicesData(MeshIndexType.Outline).length);
   }

   @Test
   public void testFaceIndices() {
      MeshIndices<byte[]> indices = new MeshIndices<>(IndicesDataTypes.Byte);
      indices.setFaceCount(5);
      for (int faceIndex = 0; faceIndex < 5; faceIndex++)
         for (int faceCornerIndex = 0; faceIndex < 3; faceIndex++)
            for (int index = 0; index < 10; index++)
               testFaceIndex(indices, faceIndex, faceCornerIndex, index);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testFaceIndexLow() {
      testArrayIndex(0, -1, 0);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testFaceIndexHigh() {
      testArrayIndex(1, 2, 0);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testFaceCornerIndexLow() {
      testArrayIndex(1, 0, -1);
   }

   @Test(expected = ArrayIndexOutOfBoundsException.class)
   public void testFaceCornerIndexHigh() {
      testArrayIndex(1, 0, 3);
   }

   public void testArrayIndex(int faceCount, int faceIndex, int faceCornerIndex) {
      MeshIndices<byte[]> indices = new MeshIndices<>(IndicesDataTypes.Byte);
      indices.setFaceCount(faceCount);
      indices.getFaceIndex(faceIndex, faceCornerIndex);
   }

   private void testFaceIndex(MeshIndices<byte[]> indices, int faceIndex, int faceCornerIndex, int expectedIndex) {
      indices.setFaceIndex(faceIndex, faceCornerIndex, expectedIndex);
      int[] faceIndices = new int[3];
      indices.getFaceIndices(faceIndex, faceIndices);
      Assert.assertEquals(expectedIndex, faceIndices[faceCornerIndex]);
   }
}
