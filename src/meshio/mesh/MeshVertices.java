package meshio.mesh;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import meshio.MeshVertexType;

public class MeshVertices {
   private MeshVertexType[]             format            = new MeshVertexType[0];
   private Map<MeshVertexType, Integer> formatTypeIndexes = new HashMap<>();
   private float[]                      vertexData        = new float[0];
   private int                          vertexCount;

   public float[] getVerticesData() {
      return vertexData;
   }

   public MeshVertexType[] getFormat() {
      return format;
   }

   public void setFormat(MeshVertexType... format) {
      Map<MeshVertexType, Integer> newFormatTypeIndexes = createNewFormatTypeIndexes(format);
      int numOldFormatTypes = formatTypeIndexes.size();
      int numNewFormatTypes = newFormatTypeIndexes.size();
      float[] newVertexData = new float[vertexCount * numNewFormatTypes];
      for (Entry<MeshVertexType, Integer> entry : newFormatTypeIndexes.entrySet()) {
         Integer oldIndexObj = formatTypeIndexes.get(entry.getKey());
         if (oldIndexObj != null) {
            int oldFormatTypeIndex = oldIndexObj;
            int newFormatTypeIndex = entry.getValue();
            for (int i = 0; i < vertexCount; i++) {
               int oldOffset = oldFormatTypeIndex + i * numOldFormatTypes;
               int newOffset = newFormatTypeIndex + i * numNewFormatTypes;
               newVertexData[newOffset] = vertexData[oldOffset];
            }
         }
      }
      this.format = format;
      this.formatTypeIndexes = newFormatTypeIndexes;
      this.vertexData = newVertexData;
   }

   public int getVertexCount() {
      return vertexCount;
   }

   public void setVertexCount(int vertexCount) {
      this.vertexCount = vertexCount;
      int newLength = vertexCount * formatTypeIndexes.size();
      boolean isLarger = (newLength > vertexData.length);
      if (newLength * 2 <= vertexData.length || isLarger) {
         if (isLarger && newLength < 2 * vertexData.length)
            newLength = 2 * vertexData.length;
         this.vertexData = Arrays.copyOf(vertexData, newLength);
      }
   }

   public float getVertexDatum(int vertexIndex, MeshVertexType type) {
      Integer typeIndexObj = formatTypeIndexes.get(type);
      if (typeIndexObj == null)
         return 0;
      int offset = typeIndexObj + vertexIndex * formatTypeIndexes.size();
      return vertexData[offset];
   }

   public void setVertexDatum(int vertexIndex, MeshVertexType type, float datum) {
      Integer typeIndexObj = formatTypeIndexes.get(type);
      if (typeIndexObj == null)
         return;
      int offset = typeIndexObj + vertexIndex * formatTypeIndexes.size();
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
         vertexData[offset + i] = vertexData[i];
   }

   private Map<MeshVertexType, Integer> createNewFormatTypeIndexes(MeshVertexType[] format) {
      Map<MeshVertexType, Integer> formatTypeIndexes = new HashMap<>();
      for (int i = 0; i < format.length; i++)
         formatTypeIndexes.put(format[i], i);
      return formatTypeIndexes;
   }
}
