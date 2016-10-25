package tests;

public class Vertex {
   public static final int  ELEMENTS_PER_VERTEX = 12;
   private static final int POSITION_X_INDEX    = 0;
   private static final int POSITION_Y_INDEX    = 1;
   private static final int POSITION_Z_INDEX    = 2;
   private static final int NORMAL_X_INDEX      = 3;
   private static final int NORMAL_Y_INDEX      = 4;
   private static final int NORMAL_Z_INDEX      = 5;
   private static final int COLOR_R_INDEX       = 6;
   private static final int COLOR_G_INDEX       = 7;
   private static final int COLOR_B_INDEX       = 8;
   private static final int COLOR_A_INDEX       = 9;
   private static final int IMAGE_COORD_X_INDEX = 10;
   private static final int IMAGE_COORD_Y_INDEX = 11;
   private final float[]    data                = new float[ELEMENTS_PER_VERTEX];

   public Vertex() {
      this(false);
   }

   public Vertex(boolean isInitialised) {
      if (isInitialised) {
         data[NORMAL_Y_INDEX] = 1;
         data[COLOR_A_INDEX] = 1;
      }
   }

   public float[] getVertexData() {
      return data;
   }

   public void setPositionX(float x) {
      setDatum(POSITION_X_INDEX, x);
   }

   public void setPositionY(float y) {
      setDatum(POSITION_Y_INDEX, y);
   }

   public void setPositionZ(float z) {
      setDatum(POSITION_Z_INDEX, z);
   }

   public void setNormalX(float nx) {
      setDatum(NORMAL_X_INDEX, nx);
   }

   public void setNormalY(float ny) {
      setDatum(NORMAL_Y_INDEX, ny);
   }

   public void setNormalZ(float nz) {
      setDatum(NORMAL_Z_INDEX, nz);
   }

   public void setColorR(float r) {
      setDatum(COLOR_R_INDEX, r);
   }

   public void setColorG(float g) {
      setDatum(COLOR_G_INDEX, g);
   }

   public void setColorB(float b) {
      setDatum(COLOR_B_INDEX, b);
   }

   public void setColorA(float a) {
      setDatum(COLOR_A_INDEX, a);
   }

   public void setImageCoordX(float x) {
      setDatum(IMAGE_COORD_X_INDEX, x);
   }

   public void setImageCoordY(float t) {
      setDatum(IMAGE_COORD_Y_INDEX, t);
   }

   private void setDatum(int index, float datum) {
      data[index] = datum;
   }

   public static Vertex create(float x, float y, float r, float g, float b) {
      Vertex vertex = new Vertex();
      vertex.setPositionX(x);
      vertex.setPositionY(y);
      vertex.setColorR(r);
      vertex.setColorG(g);
      vertex.setColorB(b);
      vertex.setColorA(1);
      return vertex;
   }
}
