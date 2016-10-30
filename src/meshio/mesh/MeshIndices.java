package meshio.mesh;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class MeshIndices<T> {
   private final IndicesDataType<T>    indicesDataType;
   private final Map<MeshIndexType, T> meshIndexTypeIndices = new HashMap<>();

   public MeshIndices(IndicesDataType<T> indicesDataType) {
      this.indicesDataType = indicesDataType;
      for (MeshIndexType meshIndexType : MeshIndexType.values())
         meshIndexTypeIndices.put(meshIndexType, indicesDataType.createEmptyArray());
   }

   public T getIndicesData(MeshIndexType meshIndexType) {
      return meshIndexTypeIndices.get(meshIndexType);
   }

   public int getFaceCount() {
      T meshIndices = meshIndexTypeIndices.get(MeshIndexType.Mesh);
      return Array.getLength(meshIndices) / MeshIndexType.Mesh.getOffsetsLength();
   }

   public void setFaceCount(int faceCount) {
      for (MeshIndexType meshIndexType : MeshIndexType.values()) {
         T indices = meshIndexTypeIndices.get(meshIndexType);
         indices = indicesDataType.createNewArray(indices, faceCount * meshIndexType.getOffsetsLength());
         meshIndexTypeIndices.put(meshIndexType, indices);
      }
   }

   public int getFaceIndex(int faceIndex, int faceCornerIndex) {
      T meshIndices = meshIndexTypeIndices.get(MeshIndexType.Mesh);
      int offset = faceCornerIndex + faceIndex * MeshIndexType.Mesh.getOffsetsLength();
      return Array.getInt(meshIndices, offset);
   }

   public void setFaceIndex(int faceIndex, int faceCornerIndex, int index) {
      for (MeshIndexType meshIndexType : MeshIndexType.values()) {
         T indices = meshIndexTypeIndices.get(meshIndexType);
         int meshOffset = faceCornerIndex + faceIndex * meshIndexType.getOffsetsLength();
         // TODO mesh will set 1 value, outline will set 2 values
         indicesDataType.setValue(indices, meshOffset, index);
      }
   }

   public void getFaceIndices(int faceIndex, int[] faceIndices) {
      T meshIndices = meshIndexTypeIndices.get(MeshIndexType.Mesh);
      int offsetsLength = MeshIndexType.Mesh.getOffsetsLength();
      int offset = faceIndex * offsetsLength;
      for (int i = 0; i < offsetsLength; i++)
         faceIndices[i] = Array.getInt(meshIndices, offset + i);
   }

   public void setFaceIndices(int faceIndex, int[] faceIndices) {
      for (MeshIndexType meshIndexType : MeshIndexType.values()) {
         T indices = meshIndexTypeIndices.get(meshIndexType);
         int offset = faceIndex * MeshIndexType.Mesh.getOffsetsLength();
         // TODO mesh will set 3 values, outline will set 6 values
         for (int i = 0; i < faceIndices.length; i++)
            indicesDataType.setValue(indices, offset + i, faceIndices[i]);
      }
   }
}
