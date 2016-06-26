package meshio.util;

import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PrimitiveOutputStream extends FilterOutputStream {
   public PrimitiveOutputStream(OutputStream os) {
      super(new BufferedOutputStream(os));
   }

   public void writeFloat(float f, boolean isBigEndian) throws IOException {
      int i = Float.floatToIntBits(f);
      writeLong(i, isBigEndian, 4);
   }

   public void writeDouble(double d, boolean isBigEndian) throws IOException {
      long l = Double.doubleToLongBits(d);
      writeLong(l, isBigEndian, 8);
   }

   public void writeInt(int i, boolean isBigEndian, int numBytes) throws IOException {
      writeLong(i, isBigEndian, numBytes);
   }

   public void writeLong(long l, boolean isBigEndian, int numBytes) throws IOException {
      byte[] bytes = new byte[numBytes];
      if (isBigEndian)
         for (int i = 0; i < numBytes; i++)
            bytes[i] = (byte) (l >>> (8 * (numBytes - 1 - i)));
      else
         for (int i = numBytes - 1; i >= 0; i--)
            bytes[i] = (byte) (l >>> (8 * i));
      write(bytes);
   }

   public void writeLine(String line) throws IOException {
      int length = line.length();
      byte[] bytes = new byte[length + 1];
      for (int i = 0; i < line.length(); i++)
         bytes[i] = (byte) line.charAt(i);
      bytes[length] = '\n';
      write(bytes);
   }
}
