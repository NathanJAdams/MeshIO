package meshio;

import java.io.InputStream;
import java.io.OutputStream;

public class MeshIO {
   public static <T> T read(Class<IMeshBuilder<T>> meshBuilderClass, InputStream inputStream, MeshFormat meshFormat) {
      return meshFormat.read(meshBuilderClass, inputStream);
   }

   public static boolean write(IMeshSaver meshSaver, OutputStream os, MeshFormat meshFormat) {
      return meshFormat.write(meshSaver, os);
   }
}
