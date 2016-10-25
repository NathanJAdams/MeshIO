package meshio.mesh.indices;

public interface IndicesDataType<T> {
   T createEmptyArray();

   T createNewArray(T previousArray, int newLength);

   void setValue(T array, int index, int value);
}
