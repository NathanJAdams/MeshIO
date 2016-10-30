package meshio.mesh;

public enum MeshIndexType {
   Mesh(new int[] { 0, 1, 2 }),
   Outline(new int[] { 0, 1, 1, 2, 2, 0 });
   private final int[] offsets;

   private MeshIndexType(int[] offsets) {
      this.offsets = offsets;
   }

   public int getOffsetsLength() {
      return offsets.length;
   }

   public int[] copyOffsets() {
      int[] copy = new int[offsets.length];
      for (int i = 0; i < offsets.length; i++)
         copy[i] = offsets[i];
      return copy;
   }
}
