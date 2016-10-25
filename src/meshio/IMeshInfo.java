package meshio;

public interface IMeshInfo {
   MeshVertexType[] getVertexFormat();

   int getVertexCount();

   int getFaceCount();
}
