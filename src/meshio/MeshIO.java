package meshio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import meshio.formats.mbwf.MbwfFormat;
import meshio.formats.ply.PlyFormatAscii_1_0;
import meshio.mesh.IMesh;

public class MeshIO {
   private final Map<String, IMeshFormat> extensionFormats = new HashMap<>();

   public MeshIO() {
      registerMeshFormat(new PlyFormatAscii_1_0());
      registerMeshFormat(new MbwfFormat());
   }

   public void registerMeshFormat(IMeshFormat meshFormat) {
      extensionFormats.put(meshFormat.getFileExtension(), meshFormat);
   }

   public IMeshFormat getMeshFormatFromExtension(String extension) {
      if (extension != null)
         extension = extension.toLowerCase(Locale.ENGLISH);
      return extensionFormats.get(extension);
   }

   public IMesh read(IMeshFormat format, IMeshBuilder builder, InputStream is) throws MeshIOException {
      return format.read(builder, is);
   }

   public void write(IMeshFormat format, IMeshSaver saver, OutputStream os) throws MeshIOException {
      format.write(saver, os);
   }

   public IMesh read(IMeshBuilder builder, String filePath) throws MeshIOException {
      IMeshFormat format = getFormatFromFilePath(filePath);
      try {
         FileInputStream fis = new FileInputStream(filePath);
         return format.read(builder, fis);
      } catch (FileNotFoundException e) {
         throw new MeshIOException("Cannot read from file at path: " + filePath);
      }
   }

   public void write(IMeshSaver saver, String filePath) throws MeshIOException {
      IMeshFormat format = getFormatFromFilePath(filePath);
      try {
         FileOutputStream fos = new FileOutputStream(filePath);
         format.write(saver, fos);
      } catch (FileNotFoundException e) {
         throw new MeshIOException("Cannot write to file at path: " + filePath);
      }
   }

   private IMeshFormat getFormatFromFilePath(String filePath) throws MeshIOException {
      if (filePath == null)
         throw new MeshIOException("Cannot find mesh format from null path");
      int lastDotIndex = filePath.lastIndexOf('.');
      if (lastDotIndex == -1)
         throw new MeshIOException("Cannot find mesh format from path: " + filePath);
      String extension = filePath.substring(lastDotIndex);
      IMeshFormat format = extensionFormats.get(extension);
      if (format == null)
         throw new MeshIOException("Cannot find mesh format from path: " + filePath);
      return format;
   }
}
