package ply;

import java.util.List;

public interface IPlySavable {
   public static final int ERROR_FORMAT_NOT_FOUND                = 100;
   public static final int ERROR_HEADER_NOT_FOUND                = 200;
   public static final int ERROR_HEADER_NOT_RECOGNISED           = 201;
   public static final int ERROR_HEADER_UNEXPECTED               = 202;
   public static final int ERROR_HEADER_ELEMENT_COUNT_UNREADABLE = 210;
   public static final int ERROR_DATA_NOT_FOUND                  = 300;
   public static final int ERROR_DATA_INSUFFICIENT               = 301;
   public static final int ERROR_DATA_NOT_PARSED                 = 302;

   void onSuccess();

   void onFailure(int errorCode);

   PlyFormat getFormat();

   List<PlyElementHeader> getElementHeaders();

   int getInt(String elementName, int elementIndex, String propertyName);

   float getFloat(String elementName, int elementIndex, String propertyName);

   int[] getIntArray(String elementName, int elementIndex, String propertyName);

   float[] getFloatArray(String elementName, int elementIndex, String propertyName);
}
