package meshio;

import java.util.EnumMap;

public enum MeshVertexType {
   Position_X,
   Position_Y,
   Position_Z,
   Normal_X,
   Normal_Y,
   Normal_Z,
   Color_R,
   Color_G,
   Color_B,
   Color_A,
   ImageCoord_X,
   ImageCoord_Y;
   private static final MeshVertexType[] VALUES = values();

   public static MeshVertexType[] getValues() {
      return VALUES;
   }

   public static EnumMap<MeshVertexType, Integer> createTypeIndexes(MeshVertexType[] format) {
      EnumMap<MeshVertexType, Integer> typeIndexes = new EnumMap<MeshVertexType, Integer>(MeshVertexType.class);
      if (format != null)
         for (int i = 0; i < format.length; i++)
            typeIndexes.put(format[i], i);
      return typeIndexes;
   }
}
