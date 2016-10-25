package meshio.mesh.indices;

public class LoadableIndices<T> implements IIndicesData<T>, ISettableIndices {
   private final IndicesDataType<T> indicesDataType;
   private final MeshIndexType      meshIndexType;
   private T                        faceIndices;

   public LoadableIndices(IndicesDataType<T> indicesDataType, MeshIndexType meshIndexType) {
      this.indicesDataType = indicesDataType;
      this.meshIndexType = meshIndexType;
      this.faceIndices = indicesDataType.createEmptyArray();
   }

   @Override
   public IndicesDataType<T> getIndicesDataType() {
      return indicesDataType;
   }

   @Override
   public MeshIndexType getMeshIndexType() {
      return meshIndexType;
   }

   @Override
   public T getIndicesData() {
      return faceIndices;
   }

   @Override
   public void setFaceCount(int faceCount) {
      this.faceIndices = indicesDataType.createNewArray(faceIndices, faceCount * meshIndexType.getOffsetsLength());
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      int offset = faceIndex * meshIndexType.getOffsetsLength();
      for (int i = 0; i < faceIndices.length; i++)
         indicesDataType.setValue(this.faceIndices, offset, faceIndices[i]);
   }
}
