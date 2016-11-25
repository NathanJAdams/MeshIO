package meshio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import meshio.mesh.IMesh;

public class MeshIO {
   public static IMesh read(IMeshFormat format, IMeshBuilder builder, InputStream is) throws MeshIOException {
      return format.read(builder, is);
   }

   public static void write(IMeshFormat format, IMeshSaver saver, OutputStream os) throws MeshIOException {
      format.write(saver, os);
   }

   public static IMesh read(IMeshBuilder builder, String filePath) throws MeshIOException {
      IMeshFormat format = getFormatFromFilePath(filePath);
      try {
         FileInputStream fis = new FileInputStream(filePath);
         return format.read(builder, fis);
      } catch (FileNotFoundException e) {
         throw new MeshIOException("Cannot read from file at path: " + filePath);
      }
   }

   public static void write(IMeshSaver saver, String filePath) throws MeshIOException {
      IMeshFormat format = getFormatFromFilePath(filePath);
      try {
         FileOutputStream fos = new FileOutputStream(filePath);
         format.write(saver, fos);
      } catch (FileNotFoundException e) {
         throw new MeshIOException("Cannot write to file at path: " + filePath);
      }
   }

   private static IMeshFormat getFormatFromFilePath(String filePath) throws MeshIOException {
      if (filePath == null)
         throw new MeshIOException("Cannot find mesh format from null path");
      int lastDotIndex = filePath.lastIndexOf('.');
      if (lastDotIndex == -1)
         throw new MeshIOException("Cannot find mesh format from path: " + filePath);
      String extension = filePath.substring(lastDotIndex);
      IMeshFormat format = MeshFormats.getFormatFromFileExtension(extension);
      if (format == null)
         throw new MeshIOException("Cannot find mesh format from path: " + filePath);
      return format;
   }
}
