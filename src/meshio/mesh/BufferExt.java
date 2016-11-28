package meshio.mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BufferExt {
   public static final int        BYTES_PER_BYTE    = 1;
   public static final int        BYTES_PER_SHORT   = 2;
   public static final int        BYTES_PER_INT     = 4;
   public static final int        BYTES_PER_FLOAT   = 4;
   private static final ByteOrder NATIVE_BYTE_ORDER = ByteOrder.nativeOrder();

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
      return with(array, 0, array.length);
   }

   public static ByteBuffer with(float[] array, int offset, int length) {
      ByteBuffer bb = createByteBuffer(array.length * BYTES_PER_FLOAT);
      bb.asFloatBuffer().put(array, offset, length).position(0);
      return bb;
   }

   public static ByteBuffer createByteBuffer(int byteCount) {
      return ByteBuffer.allocateDirect(byteCount).order(NATIVE_BYTE_ORDER);
   }
}
