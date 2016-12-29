package com.ripplar_games.meshio.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class IOExt {
   private static final int BUFFER_SIZE = 1 << 16;

   public static boolean close(Closeable closeable) {
      try {
         if (closeable != null)
            closeable.close();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return false;
   }

   public static boolean flush(OutputStream stream) {
      try {
         if (stream != null)
            stream.flush();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return false;
   }

   public static String readLine(InputStream is) {
      return readLine(new PrimitiveInputStream(is));
   }

   public static List<String> readAllLines(InputStream is) {
      PrimitiveInputStream pis = new PrimitiveInputStream(is);
      List<String> lines = new ArrayList<String>();
      String s = null;
      do {
         s = readLine(pis);
         if (s != null)
            lines.add(s);
      } while (s != null);
      return lines;
   }

   public static String readAll(InputStream inputStream) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      copyContents(inputStream, baos);
      return new String(baos.toByteArray());
   }

   private static String readLine(PrimitiveInputStream pis) {
      try {
         return pis.readLine();
      } catch (IOException e) {
      }
      return null;
   }

   public static byte[] readAllBytes(InputStream is) {
      if (is == null) {
         return null;
      }
      ByteArrayOutputStream baos = null;
      try {
         baos = new ByteArrayOutputStream();
         copyContents(is, baos);
         return baos.toByteArray();
      } finally {
         close(baos);
      }
   }

   public static boolean writeBytes(OutputStream os, byte[] bytes) {
      try {
         BufferedOutputStream bos = new BufferedOutputStream(os);
         bos.write(bytes);
         bos.flush();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return false;
   }

   public static boolean copyContents(InputStream from, OutputStream to) {
      InputStream in = null;
      OutputStream out = null;
      in = new BufferedInputStream(from);
      out = new BufferedOutputStream(to);
      try {
         byte[] buffer = new byte[BUFFER_SIZE];
         int size;
         do {
            size = in.read(buffer);
            if (size != -1)
               out.write(buffer, 0, size);
         } while (size != -1);
         out.flush();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }
      return false;
   }
}
