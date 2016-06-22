package meshio;

import java.io.InputStream;
import java.io.OutputStream;

import ply.IPlyBuilder;
import ply.IPlySavable;
import ply.PlyReader;
import ply.PlyWriter;

public enum MeshFormat {
   Ply {
      @Override
      public <T> T read(Class<IPlyBuilder<T>> builderClass, InputStream is) {
         return PlyReader.read(builderClass, is);
      }

      @Override
      public boolean write(IPlySavable savable, OutputStream os) {
         return PlyWriter.write(savable, os);
      }
   };
   public abstract <T> T read(Class<IPlyBuilder<T>> builderClass, InputStream is);

   public abstract boolean write(IPlySavable savable, OutputStream os);
}
