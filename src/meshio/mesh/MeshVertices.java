package meshio.mesh;

import java.util.Arrays;

import collections.EnumIntMap;
import meshio.MeshVertexType;

public class MeshVertices {
   private MeshVertexType[]           format            = new MeshVertexType[0];
   private EnumIntMap<MeshVertexType> formatTypeIndexes = new EnumIntMap<>(MeshVertexType.getValues());
   private float[]                    vertexData        = new float[0];
   private int                        vertexCount;

   public MeshVertices(MeshVertexType... format) {
      this.format = format;
   }

   public MeshVertexType[] getFormat() {
      return format;
   }

   public int getVertexCount() {
      return vertexCount;
   }

   public void setVertexCount(int vertexCount) {
      this.vertexCount = vertexCount;
      int newLength = vertexCount * format.length;
      boolean isLarger = (newLength > vertexData.length);
      if (newLength * 2 <= vertexData.length || isLarger) {
         if (isLarger && newLength < 2 * vertexData.length)
            newLength = 2 * vertexData.length;
         this.vertexData = Arrays.copyOf(vertexData, newLength);
      }
   }

   public float[] getVerticesAsFormat(MeshVertexType... format) {
      // TODO do this properly
      return vertexData;
   }

   public float getVertexDatum(int vertexIndex, MeshVertexType type) {
      int typeIndex = formatTypeIndexes.get(type);
      if (typeIndex == -1)
         return 0;
      int offset = typeIndex + vertexIndex * format.length;
      return vertexData[offset];
   }

   public void setVertexDatum(int vertexIndex, MeshVertexType type, float datum) {
      int typeIndex = formatTypeIndexes.get(type);
      if (typeIndex == -1)
         return;
      int offset = typeIndex + vertexIndex * format.length;
      vertexData[offset] = datum;
   }

   public void getVertexData(int vertexIndex, float[] vertexData) {
      int offset = vertexIndex * format.length;
      for (int i = 0; i < format.length; i++)
         vertexData[i] = this.vertexData[offset + i];
   }

   public void setVertexData(int vertexIndex, float[] vertexData) {
      int offset = vertexIndex * format.length;
      for (int i = 0; i < vertexData.length; i++)
         this.vertexData[offset + i] = vertexData[i];
   }
}
