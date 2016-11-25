package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class BufferExt {
   public static final int        BYTES_PER_BYTE    = 1;
   public static final int        BYTES_PER_SHORT   = 2;
   public static final int        BYTES_PER_INT     = 4;
   public static final int        BYTES_PER_FLOAT   = 4;
   private static final ByteOrder NATIVE_BYTE_ORDER = ByteOrder.nativeOrder();

   public static byte[] toByteArray(ByteBuffer buffer) {
      int capacity = buffer.capacity();
      byte[] bytes = new byte[capacity];
      for (int i = 0; i < capacity; i++)
         bytes[i] = buffer.get(i);
      return bytes;
   }

   public static short[] toShortArray(ByteBuffer buffer) {
      ShortBuffer sb = buffer.asShortBuffer();
      int capacity = sb.capacity();
      short[] shorts = new short[capacity];
      for (int i = 0; i < capacity; i++)
         shorts[i] = sb.get(i);
      return shorts;
   }

   public static int[] toIntArray(ByteBuffer buffer) {
      IntBuffer ib = buffer.asIntBuffer();
      int capacity = ib.capacity();
      int[] ints = new int[capacity];
      for (int i = 0; i < capacity; i++)
         ints[i] = ib.get(i);
      return ints;
   }

   public static float[] toFloatArray(ByteBuffer buffer) {
      FloatBuffer fb = buffer.asFloatBuffer();
      int capacity = fb.capacity();
      float[] floats = new float[capacity];
      for (int i = 0; i < capacity; i++)
         floats[i] = fb.get(i);
      return floats;
   }

   public static byte[] toByteArray(short[] array) {
      return toByteArray(with(array));
   }

   public static byte[] toByteArray(int[] array) {
      return toByteArray(with(array));
   }

   public static byte[] toByteArray(float[] array) {
      return toByteArray(with(array));
   }

   public static short[] toShortArray(byte[] array) {
      return toShortArray(with(array));
   }

   public static int[] toIntArray(byte[] array) {
      return toIntArray(with(array));
   }

   public static float[] toFloatArray(byte[] array) {
      return toFloatArray(with(array));
   }

   public static ByteBuffer with(byte[] array) {
      ByteBuffer bb = createByteBuffer(array.length * BYTES_PER_BYTE);
      bb.put(array).position(0);
      return bb;
   }

   public static ByteBuffer with(short[] array) {
      ByteBuffer bb = createByteBuffer(array.length * BYTES_PER_SHORT);
      bb.asShortBuffer().put(array).position(0);
      return bb;
   }

   public static ByteBuffer with(int[] array) {
      ByteBuffer bb = createByteBuffer(array.length * BYTES_PER_INT);
      bb.asIntBuffer().put(array).position(0);
      return bb;
   }

   public static ByteBuffer with(float[] array) {
      ByteBuffer bb = createByteBuffer(array.length * BYTES_PER_FLOAT);
      bb.asFloatBuffer().put(array).position(0);
      return bb;
   }

   public static ByteBuffer createByteBuffer(int byteCount) {
      return ByteBuffer.allocateDirect(byteCount).order(NATIVE_BYTE_ORDER);
   }
}
