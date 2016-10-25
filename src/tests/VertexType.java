package tests;

public enum VertexType {
   Position_XYZ(0, 3),
   Normal_XYZ(3, 3),
   Color_RGB(6, 3),
   Color_RGBA(6, 4),
   TexCoord_ST(10, 2);
   private final int offset;
   private final int count;

   private VertexType(int offset, int count) {
      this.offset = offset;
      this.count = count;
   }

   public int getOffset() {
      return offset;
   }

   public int getCount() {
      return count;
   }
}
