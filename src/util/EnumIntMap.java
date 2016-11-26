package util;

public class EnumIntMap<E extends Enum<E>> {
   private final int[] array;

   public EnumIntMap(Class<E> enumClass) {
      this(enumClass.getEnumConstants());
   }

   public EnumIntMap(E[] enumConstants) {
      this.array = new int[enumConstants.length];
   }

   public void clear() {
      for (int i = 0; i < array.length; i++)
         array[i] = 0;
   }

   public int get(E e) {
      return array[e.ordinal()];
   }

   public void set(E e, int value) {
      array[e.ordinal()] = value;
   }
}
