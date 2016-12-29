package meshio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import meshio.formats.mbwf.MbwfFormat;
import meshio.formats.ply.PlyFormatAscii_1_0;
import meshio.io.IOExt;
import meshio.mesh.IMesh;

public class MeshIO {
   private final Map<String, IMeshFormat> extensionFormats = new HashMap<String, IMeshFormat>();

   public MeshIO() {
      registerMeshFormat(new PlyFormatAscii_1_0());
      registerMeshFormat(new MbwfFormat());
   }

   public void registerMeshFormat(IMeshFormat meshFormat) {
      extensionFormats.put(meshFormat.getFileExtension(), meshFormat);
   }

   public <T extends IMesh> T read(IMeshBuilder<T> builder, String filePath) throws MeshIOException {
      IMeshFormat format = getFormatFromFilePath(filePath);
      FileInputStream fis = null;
      try {
         fis = new FileInputStream(filePath);
         return format.read(builder, fis);
      } catch (FileNotFoundException e) {
         throw new MeshIOException("Cannot read from file at path: " + filePath);
      } finally {
         IOExt.close(fis);
      }
   }

   public void write(IMeshSaver saver, String filePath) throws MeshIOException {
      IMeshFormat format = getFormatFromFilePath(filePath);
      FileOutputStream fos = null;
      try {
         fos = new FileOutputStream(filePath);
         format.write(saver, fos);
      } catch (FileNotFoundException e) {
         throw new MeshIOException("Cannot write to file at path: " + filePath);
      } finally {
         IOExt.close(fos);
      }
   }

   public IMeshFormat getFormatFromFilePath(String filePath) throws MeshIOException {
      if (filePath == null)
         throw new MeshIOException("Cannot find mesh format from null path");
      int lastDotIndex = filePath.lastIndexOf('.');
      if (lastDotIndex == -1)
         throw new MeshIOException("Cannot find mesh extension in path: " + filePath);
      String extension = filePath.substring(lastDotIndex + 1);
      return getFormatFromExtension(extension);
   }

   public IMeshFormat getFormatFromExtension(String extension) throws MeshIOException {
      IMeshFormat format = extensionFormats.get(extension);
      if (format == null)
         throw new MeshIOException("Cannot find mesh format from extension: " + extension);
      return format;
   }
}
