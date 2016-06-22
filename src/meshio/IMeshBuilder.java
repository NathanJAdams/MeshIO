package meshio;

public interface IMeshBuilder<T> {
   T build();

   void onFailure(int errorCode, int lineNumber);

   void declareElementCount(String elementName, int count);

   void declarePropertyType(String elementName, String propertyName, MeshDataType type);

   void declarePropertyListType(String elementName, String propertyListName, MeshDataType type);

   void addInt(String elementName, int elementIndex, String propertyName, int i);

   void addFloat(String elementName, int elementIndex, String propertyName, float f);

   void addIntList(String elementName, int elementIndex, String propertyListName, int[] iList);

   void addFloatList(String elementName, int elementIndex, String propertyListName, float[] fList);
}
