package meshio;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import meshio.ply.PlyFormat;
import meshio.ply.PlyIO;

public abstract class MeshIO {
   public static final MeshIO               Ply_WritesAscii        = new MeshIO("ply") {
                                                                      @Override
                                                                      public <T> T read(IMeshBuilder<T> builder, InputStream is)
                                                                            throws MeshIOException {
                                                                         return PlyIO.read(builder, is);
                                                                      }

                                                                      @Override
                                                                      public void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
                                                                         PlyIO.write(saver, os, PlyFormat.ASCII_1_0);
                                                                      }
                                                                   };
   public static final MeshIO               Ply_WritesBigEndian    = new MeshIO("ply", false) {
                                                                      @Override
                                                                      public <T> T read(IMeshBuilder<T> builder, InputStream is)
                                                                            throws MeshIOException {
                                                                         return PlyIO.read(builder, is);
                                                                      }

                                                                      @Override
                                                                      public void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
                                                                         PlyIO.write(saver, os, PlyFormat.BINARY_BIG_ENDIAN_1_0);
                                                                      }
                                                                   };
   public static final MeshIO               Ply_WritesLittleEndian = new MeshIO("ply", false) {
                                                                      @Override
                                                                      public <T> T read(IMeshBuilder<T> builder, InputStream is)
                                                                            throws MeshIOException {
                                                                         return PlyIO.read(builder, is);
                                                                      }

                                                                      @Override
                                                                      public void write(IMeshSaver saver, OutputStream os) throws MeshIOException {
                                                                         PlyIO.write(saver, os, PlyFormat.BINARY_LITTLE_ENDIAN_1_0);
                                                                      }
                                                                   };
   private static final Map<String, MeshIO> EXTENSION_FORMATS      = new HashMap<>();
   private final String                     extension;

   private MeshIO(String extension) {
      this(extension, true);
   }

   private MeshIO(String extension, boolean isDefault) {
      if (isDefault || !EXTENSION_FORMATS.containsKey(extension))
         EXTENSION_FORMATS.put(extension, this);
      this.extension = extension;
   }

   public abstract <T> T read(IMeshBuilder<T> builder, InputStream is) throws MeshIOException;

   public abstract void write(IMeshSaver saver, OutputStream os) throws MeshIOException;

   public static MeshIO fromExtension(String extension) {
      if (extension != null)
         extension = extension.toLowerCase(Locale.ENGLISH);
      return EXTENSION_FORMATS.get(extension);
   }

   public String getExtension() {
      return extension;
   }
}
