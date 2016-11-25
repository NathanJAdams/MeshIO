package meshio.mesh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import collections.EnumFloatMap;
import collections.MultiSet;
import io.BufferExt;
import meshio.MeshVertexType;

public class EditableMeshVertices {
   private final List<EnumFloatMap<MeshVertexType>> vertexList            = new ArrayList<>();
   private final MultiSet<MeshVertexType, Integer>  meshVertexTypeIndexes = new MultiSet<>();
   private MeshVertexType[]                         format                = new MeshVertexType[] {};
   private float[]                                  vertices              = new float[0];
   private int                                      vertexCount;

   public ByteBuffer toByteBuffer() {
      return BufferExt.with(vertices);
   }

   public MeshVertexType[] getFormat() {
      return format;
   }

   public void setFormat(MeshVertexType... format) {
      if (format != null && format.length > 0) {
         this.format = format;
         meshVertexTypeIndexes.clear();
         for (int i = 0; i < format.length; i++)
            meshVertexTypeIndexes.add(format[i], i);
         updateVertices();
      }
   }

   public int getVertexCount() {
      return vertexCount;
   }

   public void setVertexCount(int vertexCount) {
      if (this.vertexCount > vertexCount)
         for (int i = vertexCount; i < vertexList.size(); i++)
            vertexList.get(i).clear();
      else if (this.vertexCount < vertexCount)
         for (int i = vertexList.size(); i < vertexCount; i++)
            vertexList.add(new EnumFloatMap<>(MeshVertexType.getValues()));
      this.vertexCount = vertexCount;
   }

   public float getVertexDatum(int vertexIndex, MeshVertexType meshVertexType) {
      return vertexList.get(vertexIndex).get(meshVertexType);
   }

   public void setVertexDatum(int vertexIndex, MeshVertexType meshVertexType, float datum) {
      vertexList.get(vertexIndex).set(meshVertexType, datum);
      int offsetIndex = vertexIndex * format.length;
      for (int i = 0; i < format.length; i++)
         if (meshVertexType == format[i])
            vertices[offsetIndex + i] = datum;
   }

   public void getVertexData(int vertexIndex, float[] vertexData) {
      EnumFloatMap<MeshVertexType> vertex = vertexList.get(vertexIndex);
      for (int i = 0; i < format.length && i < vertexData.length; i++)
         vertexData[i] = vertex.get(format[i]);
   }

   public void setVertexData(int vertexIndex, float[] vertexData) {
      EnumFloatMap<MeshVertexType> vertex = vertexList.get(vertexIndex);
      for (int i = 0; i < format.length && i < vertexData.length; i++)
         vertex.set(format[i], vertexData[i]);
   }

   private void updateVertices() {
      this.vertices = new float[vertexList.size() * format.length];
      for (int vertexIndex = 0; vertexIndex < vertexList.size(); vertexIndex++) {
         int offsetIndex = vertexIndex * format.length;
         EnumFloatMap<MeshVertexType> vertex = vertexList.get(vertexIndex);
         for (int formatIndex = 0; formatIndex < format.length; formatIndex++)
            vertices[offsetIndex + formatIndex] = vertex.get(format[formatIndex]);
      }
   }
}