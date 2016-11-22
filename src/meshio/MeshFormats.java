package meshio;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import meshio.mbwf.MbwfIO;
import meshio.mesh.IMesh;
import meshio.ply.PlyFormat;
import meshio.ply.PlyIO;

public class MeshFormats {
   private static abstract class MeshFormat implements IMeshFormat {
      private final String extension;

      private MeshFormat(String extension) {
         this(extension, true);
      }

      private MeshFormat(String extension, boolean isDefault) {
         if (isDefault || !EXTENSION_FORMATS.containsKey(extension))
            EXTENSION_FORMATS.put(extension, this);
         this.extension = extension;
      }

      @Override
      public String getFileExtension() {
         return extension;
      }
   }

   private static final Map<String, IMeshFormat> EXTENSION_FORMATS      = new HashMap<>();
   public static final IMeshFormat               Mbwf                   = new MeshFormat("mbwf") {
                                                                           @Override
                                                                           public IMesh read(IMeshBuilder builder, InputStream is)
                                                                                 throws MeshIOException {
                                                                              return MbwfIO.read(builder, is);
                                                                           }

                                                                           @Override
                                                                           public void write(IMeshSaver saver, OutputStream os)
                                                                                 throws MeshIOException {
                                                                              MbwfIO.write(saver, os);
                                                                           }
                                                                        };
   public static final IMeshFormat               Ply_WritesAscii        = new MeshFormat("ply") {
                                                                           @Override
                                                                           public IMesh read(IMeshBuilder builder, InputStream is)
                                                                                 throws MeshIOException {
                                                                              return PlyIO.read(builder, is);
                                                                           }

                                                                           @Override
                                                                           public void write(IMeshSaver saver, OutputStream os)
                                                                                 throws MeshIOException {
                                                                              PlyIO.write(saver, os, PlyFormat.ASCII_1_0);
                                                                           }
                                                                        };
   public static final IMeshFormat               Ply_WritesBigEndian    = new MeshFormat("ply", false) {
                                                                           @Override
                                                                           public IMesh read(IMeshBuilder builder, InputStream is)
                                                                                 throws MeshIOException {
                                                                              return PlyIO.read(builder, is);
                                                                           }

                                                                           @Override
                                                                           public void write(IMeshSaver saver, OutputStream os)
                                                                                 throws MeshIOException {
                                                                              PlyIO.write(saver, os, PlyFormat.BINARY_BIG_ENDIAN_1_0);
                                                                           }
                                                                        };
   public static final IMeshFormat               Ply_WritesLittleEndian = new MeshFormat("ply", false) {
                                                                           @Override
                                                                           public IMesh read(IMeshBuilder builder, InputStream is)
                                                                                 throws MeshIOException {
                                                                              return PlyIO.read(builder, is);
                                                                           }

                                                                           @Override
                                                                           public void write(IMeshSaver saver, OutputStream os)
                                                                                 throws MeshIOException {
                                                                              PlyIO.write(saver, os, PlyFormat.BINARY_LITTLE_ENDIAN_1_0);
                                                                           }
                                                                        };

   public static IMeshFormat getFormatFromFileExtension(String extension) {
      if (extension != null)
         extension = extension.toLowerCase(Locale.ENGLISH);
      return EXTENSION_FORMATS.get(extension);
   }
}
