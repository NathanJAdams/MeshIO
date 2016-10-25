package meshio.mesh;

import meshio.mesh.indices.IndicesDataType;

public interface IMeshData {
   float[] getVertexData();

   IndicesDataType<?> getIndicesDataType();

   byte[] getIndicesAsByteArray();

   short[] getIndicesAsShortArray();

   int[] getIndicesAsIntArray();
}
