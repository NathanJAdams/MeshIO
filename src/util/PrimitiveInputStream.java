package util;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrimitiveInputStream extends FilterInputStream {
   private int lineNumber = 1;

   public PrimitiveInputStream(InputStream is) {
      super(new BufferedInputStream(is));
   }

   public int getLineNumber() {
      return lineNumber;
   }

   public float readFloat(boolean isBigEndian) throws IOException {
      return Float.intBitsToFloat((int) readLong(isBigEndian, 4));
   }

   public double readDouble(boolean isBigEndian) throws IOException {
      return Double.longBitsToDouble(readLong(isBigEndian, 8));
   }

   public int readInt(boolean isBigEndian, int numBytes) throws IOException {
      return (int) readLong(isBigEndian, numBytes);
   }

   public long readLong(boolean isBigEndian, int numBytes) throws IOException {
      int[] bytes = read(numBytes);
      long total = 0;
      if (isBigEndian)
         for (int i = 0; i < numBytes; i++)
            total = (total << 8) | bytes[i];
      else
         for (int i = numBytes - 1; i >= 0; i--)
            total = (total << 8) | bytes[i];
      return total;
   }

   public String readLine() throws IOException {
      StringBuilder sb = new StringBuilder();
      try {
         for (int b = read(); !isEndOfLine(b); b = read())
            sb.append((char) b);
         lineNumber++;
      } catch (IOException e) {
         if (sb.length() == 0)
            throw e;
      }
      try {
         for (int b = peek(); isCarriageReturnOrLineFeed(b); b = peek())
            skip(1);
      } catch (IOException e) {
      }
      return sb.toString();
   }

   private static boolean isEndOfLine(int b) {
      return isCarriageReturnOrLineFeed(b) || (b == -1);
   }

   private static boolean isCarriageReturnOrLineFeed(int b) {
      return ((char) b == '\r' || (char) b == '\n');
   }

   private int[] read(int numBytes) throws IOException {
      int[] bytes = new int[numBytes];
      for (int i = 0; i < numBytes; i++)
         bytes[i] = read();
      return bytes;
   }

   private int peek() throws IOException {
      mark(1);
      int peeked = read();
      reset();
      return peeked;
   }

   @Override
   public int read() throws IOException {
      int i = super.read();
      if (i == -1)
         throw new IOException("EOF");
      return i;
   }
}
