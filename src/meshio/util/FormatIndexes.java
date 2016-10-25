package meshio.util;

import java.util.HashMap;
import java.util.Map;

import meshio.MeshVertexType;

public class FormatIndexes {
   public static Map<MeshVertexType, Integer> createTypeIndexes(MeshVertexType[] format) {
      Map<MeshVertexType, Integer> typeIndexes = new HashMap<>();
      if (format != null)
         for (int i = 0; i < format.length; i++)
            typeIndexes.put(format[i], i);
      return typeIndexes;
   }
}
