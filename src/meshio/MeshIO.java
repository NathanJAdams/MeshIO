package meshio;

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
}
