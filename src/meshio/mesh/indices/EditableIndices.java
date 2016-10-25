package meshio.mesh.indices;

import java.lang.reflect.Array;

public class EditableIndices<T> implements IIndicesData<T>, IGettableIndices, ISettableIndices {
   private final LoadableIndices<T> indices;

   public EditableIndices(IndicesDataType<T> indicesDataType, MeshIndexType meshIndexType) {
      this.indices = new LoadableIndices<>(indicesDataType, meshIndexType);
   }

   @Override
   public IndicesDataType<T> getIndicesDataType() {
      return indices.getIndicesDataType();
   }

   @Override
   public MeshIndexType getMeshIndexType() {
      return indices.getMeshIndexType();
   }

   @Override
   public T getIndicesData() {
      return indices.getIndicesData();
   }

   @Override
   public int getFaceCount() {
      return Array.getLength(getIndicesData()) / getMeshIndexType().getOffsetsLength();
   }

   @Override
   public void getFaceIndices(int faceIndex, int[] faceIndices) {
      int offsetsLength = getMeshIndexType().getOffsetsLength();
      int offset = faceIndex * offsetsLength;
      for (int i = 0; i < offsetsLength; i++)
         faceIndices[i] = Array.getInt(getIndicesData(), offset + i);
   }

   @Override
   public void setFaceCount(int faceCount) {
      indices.setFaceCount(faceCount);
   }

   @Override
   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      indices.setFaceIndices(faceIndex, faceIndices);
   }
}
