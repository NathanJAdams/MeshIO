package meshio.mesh.indices;

public interface IGettableIndices {
   int getFaceCount();

   void getFaceIndices(int faceIndex, int[] faceIndices);
}