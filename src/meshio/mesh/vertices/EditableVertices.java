package meshio.mesh.vertices;

import meshio.MeshVertexType;

public class EditableVertices implements IVerticesData, IGettableVertices, ISettableVertices {
   private final LoadableVertices vertices;

   public EditableVertices(MeshVertexType... format) {
      this.vertices = new LoadableVertices(format);
   }

   @Override
   public float[] getVerticesData() {
      return vertices.getVerticesData();
   }

   @Override
   public MeshVertexType[] getFormat() {
      return vertices.getFormat();
   }

   @Override
   public int getVertexCount() {
      MeshVertexType[] format = getFormat();
      return (format.length == 0)
            ? 0
            : getVerticesData().length / format.length;
   }

   @Override
   public void getVertexData(int vertexIndex, float[] vertexData) {
      MeshVertexType[] format = getFormat();
      int offset = vertexIndex * format.length;
      float[] verticesData = getVerticesData();
      for (int i = 0; i < format.length; i++)
         vertexData[i] = verticesData[offset + i];
   }

   @Override
   public void setVertexCount(int vertexCount) {
      vertices.setVertexCount(vertexCount);
   }

   @Override
   public void setVertexData(int vertexIndex, float[] verticesData) {
      vertices.setVertexData(vertexIndex, verticesData);
   }
}
