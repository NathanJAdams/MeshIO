package meshio;

import java.io.InputStream;
import java.io.OutputStream;

public class MeshIO {
   public static <T> T read(Class<IMeshBuilder<T>> builderClass, InputStream inputStream, MeshFormat meshFormat) {
      return meshFormat.read(builderClass, inputStream);
   }

   public static boolean write(IMeshSaver savable, OutputStream os, MeshFormat meshFormat) {
      return meshFormat.write(savable, os);
   }
}
