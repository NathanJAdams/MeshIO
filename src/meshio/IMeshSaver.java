package meshio;

import java.util.List;

import ply.PlyElementHeader;

public interface IMeshSaver {
   void onFailure(int errorCode);

   List<PlyElementHeader> getElementHeaders();

   int getInt(String elementName, int elementIndex, String propertyName);

   float getFloat(String elementName, int elementIndex, String propertyName);

   int[] getIntArray(String elementName, int elementIndex, String propertyName);

   float[] getFloatArray(String elementName, int elementIndex, String propertyName);
}
