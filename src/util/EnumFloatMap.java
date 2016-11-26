package util;

public class EnumFloatMap<E extends Enum<E>> {
   private final float[] array;

   public EnumFloatMap(Class<E> enumClass) {
      this(enumClass.getEnumConstants());
   }

   public EnumFloatMap(E[] enumConstants) {
      this.array = new float[enumConstants.length];
   }

   public void clear() {
      for (int i = 0; i < array.length; i++)
         array[i] = 0;
   }

   public float get(E e) {
      return array[e.ordinal()];
   }

   public void set(E e, float value) {
      array[e.ordinal()] = value;
   }
}
