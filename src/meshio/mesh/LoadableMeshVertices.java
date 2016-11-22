package meshio.mesh;

import java.nio.ByteBuffer;

import collections.EnumIntMap;
import io.BufferExt;
import meshio.MeshVertexType;

public class LoadableMeshVertices {
   private final MeshVertexType[]     format;
   private EnumIntMap<MeshVertexType> formatTypeIndexes = new EnumIntMap<>(MeshVertexType.getValues());
   private float[]                    vertices;

   public LoadableMeshVertices(MeshVertexType... format) {
      this.format = format;
      for (int i = 0; i < format.length; i++)
         formatTypeIndexes.set(format[i], i);
   }

   public ByteBuffer toByteBuffer() {
      return BufferExt.with(vertices);
   }

   public MeshVertexType[] getFormat() {
      return format;
   }

   public int getVertexCount() {
      return (format.length == 0)
            ? 0
            : vertices.length / format.length;
   }

   public void setVertexCount(int vertexCount) {
      this.vertices = new float[vertexCount * format.length];
   }

   public void setVertexData(int vertexIndex, float[] vertexData) {
      int offset = vertexIndex * format.length;
      for (int i = 0; i < vertexData.length && i < format.length; i++) {
         int typeIndex = formatTypeIndexes.get(format[i]);
         vertices[offset + typeIndex] = vertexData[i];
      }
   }
}
