package tests;

import java.util.Locale;

public class Vector3 {
   private static final float MIN_TOLERANCE = (float) 1E-9;
   private float              x;
   private float              y;
   private float              z;

   public Vector3() {
   }

   public Vector3(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void setValues(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static Vector3 getUnitX() {
      return new Vector3(1, 0, 0);
   }

   public static Vector3 getUnitY() {
      return new Vector3(0, 1, 0);
   }

   public static Vector3 getUnitZ() {
      return new Vector3(0, 0, 1);
   }

   public static Vector3 getUnit() {
      return new Vector3(1, 1, 1);
   }

   public boolean isUnit() {
      return (x == 1) && (y == 1) && (z == 1);
   }

   public float getX() {
      return x;
   }

   public float getY() {
      return y;
   }

   public float getZ() {
      return z;
   }

   public void setX(float x) {
      this.x = x;
   }

   public void setY(float y) {
      this.y = y;
   }

   public void setZ(float z) {
      this.z = z;
   }

   public void addX(float dX) {
      this.x += dX;
   }

   public void addY(float dY) {
      this.y += dY;
   }

   public void addZ(float dZ) {
      this.z += dZ;
   }

   public float lengthSquared() {
      return (x * x) + (y * y) + (z * z);
   }

   public float length() {
      return (float) Math.sqrt(lengthSquared());
   }

   public float inverseLength() {
      return 1f / length();
   }

   public static Vector3 add(Vector3 a, Vector3 b) {
      Vector3 result = new Vector3(0, 0, 0);
      add(result, a, b);
      return result;
   }

   public static void add(Vector3 result, Vector3 a, Vector3 b) {
      result.x = a.x + b.x;
      result.y = a.y + b.y;
      result.z = a.z + b.z;
   }

   public static Vector3 subtract(Vector3 a, Vector3 b) {
      Vector3 result = new Vector3(0, 0, 0);
      subtract(result, a, b);
      return result;
   }

   public static void subtract(Vector3 result, Vector3 a, Vector3 b) {
      result.x = a.x - b.x;
      result.y = a.y - b.y;
      result.z = a.z - b.z;
   }

   public static Vector3 multiply(Vector3 a, float scalar) {
      Vector3 result = new Vector3(0, 0, 0);
      multiply(result, a, scalar);
      return result;
   }

   public static void multiply(Vector3 result, Vector3 a, float scalar) {
      result.x = a.x * scalar;
      result.y = a.y * scalar;
      result.z = a.z * scalar;
   }

   public static Vector3 divide(Vector3 a, float scalar) {
      Vector3 result = new Vector3(0, 0, 0);
      divide(result, a, scalar);
      return result;
   }

   public static void divide(Vector3 result, Vector3 a, float scalar) {
      float multiplier = (scalar <= MIN_TOLERANCE)
            ? 0
            : 1f / scalar;
      multiply(result, a, multiplier);
   }

   public static float dot(Vector3 a, Vector3 b) {
      return (a.x * b.x) + (a.y * b.y) + (a.z * b.z);
   }

   public static Vector3 cross(Vector3 a, Vector3 b) {
      Vector3 result = new Vector3(0, 0, 0);
      cross(result, a, b);
      return result;
   }

   public static void cross(Vector3 result, Vector3 a, Vector3 b) {
      float x = (a.y * b.z) - (a.z * b.y);
      float y = (a.z * b.x) - (a.x * b.z);
      float z = (a.x * b.y) - (a.y * b.x);
      result.x = x;
      result.y = y;
      result.z = z;
   }

   public static Vector3 negate(Vector3 a) {
      Vector3 result = new Vector3(0, 0, 0);
      negate(result, a);
      return result;
   }

   public static void negate(Vector3 result, Vector3 a) {
      result.x = -a.x;
      result.y = -a.y;
      result.z = -a.z;
   }

   public static Vector3 normalise(Vector3 a) {
      Vector3 result = new Vector3(0, 0, 0);
      normalise(result, a);
      return result;
   }

   public static void normalise(Vector3 result, Vector3 a) {
      float sumSq = (a.x * a.x) + (a.y * a.y) + (a.z * a.z);
      if (sumSq <= MIN_TOLERANCE) {
         result.x = 0;
         result.y = 1;
         result.z = 0;
      } else {
         double sum = Math.sqrt(sumSq);
         multiply(result, a, (float) (1.0 / sum));
      }
   }

   @Override
   public String toString() {
      return String.format(Locale.ENGLISH, "X:%f, Y:%f, Z:%f", x, y, z);
   }
}
