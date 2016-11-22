package meshio;

import java.io.InputStream;
import java.io.OutputStream;

import meshio.mesh.IMesh;

public interface IMeshFormat {
   IMesh read(IMeshBuilder builder, InputStream is) throws MeshIOException;

   void write(IMeshSaver saver, OutputStream os) throws MeshIOException;

   String getFileExtension();
}
