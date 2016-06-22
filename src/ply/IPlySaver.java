package ply;

import java.util.List;

public interface IPlySaver {
   void onSuccess();

   void onFailure(int errorCode);

   PlyFormat getFormat();

   List<PlyElementHeader> getElementHeaders();

   int getInt(String elementName, int elementIndex, String propertyName);

   float getFloat(String elementName, int elementIndex, String propertyName);

   int[] getIntArray(String elementName, int elementIndex, String propertyName);

   float[] getFloatArray(String elementName, int elementIndex, String propertyName);
}
