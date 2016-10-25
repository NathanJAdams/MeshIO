package tests;

public class Face {
   public static final int ELEMENTS_PER_FACE = 3;
   public static final int FACE_0_INDEX      = 0;
   public static final int FACE_1_INDEX      = 1;
   public static final int FACE_2_INDEX      = 2;
   private final short[]   data              = new short[ELEMENTS_PER_FACE];

   public Face() {
      this(false);
   }

   public Face(boolean isInitialised) {
      if (isInitialised) {
         data[FACE_1_INDEX] = 1;
         data[FACE_2_INDEX] = 2;
      }
   }

   public short[] getFaceIndices() {
      return data;
   }

   public void setFaceIndex0(int v0) {
      setFaceIndex(FACE_0_INDEX, v0);
   }

   public void setFaceIndex1(int v1) {
      setFaceIndex(FACE_1_INDEX, v1);
   }

   public void setFaceIndex2(int v2) {
      setFaceIndex(FACE_2_INDEX, v2);
   }

   private void setFaceIndex(int index, int vertexIndex) {
      data[index] = (short) vertexIndex;
   }

   public static Face create(int v0, int v1, int v2) {
      Face face = new Face();
      face.setFaceIndex0(v0);
      face.setFaceIndex1(v1);
      face.setFaceIndex2(v2);
      return face;
   }
}
