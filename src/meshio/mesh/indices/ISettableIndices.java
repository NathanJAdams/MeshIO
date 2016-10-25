package meshio.mesh.indices;

public interface ISettableIndices {
   void setFaceCount(int faceCount);

   void setFaceIndices(int faceIndex, int[] faceIndices);
}
