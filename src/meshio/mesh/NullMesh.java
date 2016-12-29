package meshio.mesh;

import java.nio.ByteBuffer;

import meshio.MeshVertexType;

public class NullMesh implements IMesh {
   private static final MeshVertexType[] FORMAT          = new MeshVertexType[0];
   private static final ByteBuffer       INDICES_BUFFER  = BufferUtil.with(new short[0]);
   private static final ByteBuffer       VERTICES_BUFFER = BufferUtil.with(new float[0]);

   @Override
   public MeshVertexType[] getVertexFormat() {
      return FORMAT;
   }

   @Override
   public int getVertexCount() {
      return 0;
   }

   @Override
   public int getFaceCount() {
      return 0;
   }

   @Override
   public void clear() {
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public ByteBuffer getVertices() {
      return VERTICES_BUFFER;
   }

   @Override
   public ByteBuffer getIndices() {
      return INDICES_BUFFER;
   }
}
