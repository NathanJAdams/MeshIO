package meshio.mesh;

import java.nio.ByteBuffer;

import meshio.IMeshInfo;

public interface IMesh extends IMeshInfo {
   void clear();

   boolean isValid();

   ByteBuffer getVertices();

   ByteBuffer getIndices();
}
