package meshio.mesh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import meshio.MeshVertexType;

public class EditableMeshVertices {
   private final List<EditableVertex>              vertexList            = new ArrayList<EditableVertex>();
   private final Map<MeshVertexType, Set<Integer>> meshVertexTypeIndexes = new HashMap<MeshVertexType, Set<Integer>>();
   private MeshVertexType[]                        format                = new MeshVertexType[0];
   private float[]                                 vertices              = new float[0];
   private int                                     vertexCount;

   public void clear() {
      this.vertexList.clear();
      this.meshVertexTypeIndexes.clear();
      this.format = new MeshVertexType[0];
      this.vertices = new float[0];
      this.vertexCount = 0;
   }

   public ByteBuffer toByteBuffer() {
      return BufferUtil.with(vertices, 0, vertexCount * format.length);
   }

   public MeshVertexType[] getFormat() {
      return format;
   }

   public void setFormat(MeshVertexType... format) {
      if (format != null && format.length > 0) {
         this.format = format;
         meshVertexTypeIndexes.clear();
         for (int i = 0; i < format.length; i++)
            getValidIndexSet(format[i]).add(i);
         updateVertices();
      }
   }

   public int getVertexCount() {
      return vertexCount;
   }

   public void setVertexCount(int vertexCount) {
      int previousVertexCount = this.vertexCount;
      this.vertexCount = vertexCount;
      if (previousVertexCount > vertexCount) {
         for (int i = vertexCount; i < vertexList.size(); i++)
            vertexList.get(i).clear();
      } else if (previousVertexCount < vertexCount) {
         for (int i = vertexList.size(); i < vertexCount; i++)
            vertexList.add(new EditableVertex());
         updateVertices();
      }
   }

   public float getVertexDatum(int vertexIndex, MeshVertexType meshVertexType) {
      return vertexList.get(vertexIndex).getDatum(meshVertexType);
   }

   public void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float datum) {
      vertexList.get(vertexIndex).setDatum(meshVertexType, datum);
      int offsetIndex = vertexIndex * format.length;
      for (int i = 0; i < format.length; i++)
         if (meshVertexType == format[i])
            vertices[offsetIndex + i] = datum;
   }

   public void getVertexData(int vertexIndex, float[] vertexData) {
      EditableVertex vertex = vertexList.get(vertexIndex);
      for (int i = 0; i < format.length && i < vertexData.length; i++)
         vertexData[i] = vertex.getDatum(format[i]);
   }

   public void setVertexData(int vertexIndex, float[] vertexData) {
      EditableVertex vertex = vertexList.get(vertexIndex);
      for (int i = 0; i < format.length && i < vertexData.length; i++)
         vertex.setDatum(format[i], vertexData[i]);
   }

   private void updateVertices() {
      this.vertices = new float[vertexCount * format.length];
      for (int vertexIndex = 0; vertexIndex < vertexCount; vertexIndex++) {
         int offsetIndex = vertexIndex * format.length;
         EditableVertex vertex = vertexList.get(vertexIndex);
         for (int formatIndex = 0; formatIndex < format.length; formatIndex++)
            vertices[offsetIndex + formatIndex] = vertex.getDatum(format[formatIndex]);
      }
   }

   private Set<Integer> getValidIndexSet(MeshVertexType meshVertexType) {
      Set<Integer> indexes = meshVertexTypeIndexes.get(meshVertexType);
      if (indexes == null) {
         indexes = new HashSet<Integer>();
         meshVertexTypeIndexes.put(meshVertexType, indexes);
      }
      return indexes;
   }
}
