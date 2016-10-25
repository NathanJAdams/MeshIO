package meshio.mesh.indices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndicesDataTypes {
   public static final IndicesDataType<byte[]>   Byte      = new IndicesDataType<byte[]>() {
                                                              @Override
                                                              public byte[] createEmptyArray() {
                                                                 return new byte[0];
                                                              }

                                                              @Override
                                                              public byte[] createNewArray(byte[] previousArray, int newLength) {
                                                                 return Arrays.copyOf(previousArray, newLength);
                                                              }

                                                              @Override
                                                              public void setValue(byte[] array, int index, int value) {
                                                                 array[index] = (byte) value;
                                                              }
                                                           };
   public static final IndicesDataType<short[]>  Short     = new IndicesDataType<short[]>() {
                                                              @Override
                                                              public short[] createEmptyArray() {
                                                                 return new short[0];
                                                              }

                                                              @Override
                                                              public short[] createNewArray(short[] previousArray, int newLength) {
                                                                 return Arrays.copyOf(previousArray, newLength);
                                                              }

                                                              @Override
                                                              public void setValue(short[] array, int index, int value) {
                                                                 array[index] = (short) value;
                                                              }
                                                           };
   public static final IndicesDataType<int[]>    Int       = new IndicesDataType<int[]>() {
                                                              @Override
                                                              public int[] createEmptyArray() {
                                                                 return new int[0];
                                                              }

                                                              @Override
                                                              public int[] createNewArray(int[] previousArray, int newLength) {
                                                                 return Arrays.copyOf(previousArray, newLength);
                                                              }

                                                              @Override
                                                              public void setValue(int[] array, int index, int value) {
                                                                 array[index] = value;
                                                              }
                                                           };
   private static final List<IndicesDataType<?>> ALL_TYPES = new ArrayList<>();

   static {
      ALL_TYPES.add(Byte);
      ALL_TYPES.add(Short);
      ALL_TYPES.add(Int);
   }

   public static Iterable<IndicesDataType<?>> getAllTypes() {
      return ALL_TYPES;
   }
}
