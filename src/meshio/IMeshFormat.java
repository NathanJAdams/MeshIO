package meshio;

import java.io.InputStream;
import java.io.OutputStream;

public interface IMeshFormat {
   <T> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException;

   void write(IMeshSaver saver, OutputStream os) throws MeshIOException;

   String getFileExtension();
}
