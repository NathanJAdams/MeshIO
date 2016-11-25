package util;

import java.util.EnumMap;

import meshio.MeshVertexType;

public class FormatIndexes {
   public static EnumMap<MeshVertexType, Integer> createTypeIndexes(MeshVertexType[] format) {
      EnumMap<MeshVertexType, Integer> typeIndexes = new EnumMap<>(MeshVertexType.class);
      if (format != null)
         for (int i = 0; i < format.length; i++)
            typeIndexes.put(format[i], i);
      return typeIndexes;
   }
}
