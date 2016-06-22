package meshio;

import java.io.InputStream;
import java.io.OutputStream;

import ply.PlyFormat;
import ply.PlyReader;
import ply.PlyWriter;

public enum MeshFormat {
   Ply_WritesAscii {
      @Override
      public <T> T read(Class<IMeshBuilder<T>> builderClass, InputStream is) {
         return PlyReader.read(builderClass, is);
      }

      @Override
      public boolean write(IMeshSaver savable, OutputStream os) {
         return PlyWriter.write(savable, os, PlyFormat.Ascii_1_0);
      }
   },
   Ply_WritesBigEndian {
      @Override
      public <T> T read(Class<IMeshBuilder<T>> builderClass, InputStream is) {
         return PlyReader.read(builderClass, is);
      }

      @Override
      public boolean write(IMeshSaver savable, OutputStream os) {
         return PlyWriter.write(savable, os, PlyFormat.BinaryBigEndian_1_0);
      }
   },
   Ply_WritesLittleEndian {
      @Override
      public <T> T read(Class<IMeshBuilder<T>> builderClass, InputStream is) {
         return PlyReader.read(builderClass, is);
      }

      @Override
      public boolean write(IMeshSaver savable, OutputStream os) {
         return PlyWriter.write(savable, os, PlyFormat.BinaryLittleEndian_1_0);
      }
   },;
   public abstract <T> T read(Class<IMeshBuilder<T>> builderClass, InputStream is);

   public abstract boolean write(IMeshSaver savable, OutputStream os);
}
