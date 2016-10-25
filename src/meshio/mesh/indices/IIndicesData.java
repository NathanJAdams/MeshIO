package meshio.mesh.indices;

public interface IIndicesData<T> {
   IndicesDataType<T> getIndicesDataType();

   MeshIndexType getMeshIndexType();

   T getIndicesData();
}
