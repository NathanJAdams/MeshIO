package meshio;

import java.util.List;

import ply.PlyElementHeader;
import ply.PlyFormat;

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
