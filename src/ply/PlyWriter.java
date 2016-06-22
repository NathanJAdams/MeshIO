package ply;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import meshio.IPlySaver;
import meshio.MeshIOErrorCodes;
import util.PrimitiveOutputStream;

public class PlyWriter {
   public static boolean write(IPlySaver savable, OutputStream os) {
      PlyFormat format = savable.getFormat();
      if (format == null)
         return fail(savable, MeshIOErrorCodes.FORMAT_NOT_FOUND);
      List<PlyElementHeader> elementHeaders = savable.getElementHeaders();
      if (elementHeaders == null || elementHeaders.isEmpty())
         return fail(savable, MeshIOErrorCodes.HEADER_ELEMENTS_MISSING);
      try (PrimitiveOutputStream pos = new PrimitiveOutputStream(os)) {
         writeMagic(pos);
         writeFormat(pos, savable, format);
         if (!writeElementHeaders(pos, savable, elementHeaders))
            return false;
         writeEnd(pos);
         writeData(pos, savable, format, elementHeaders);
         savable.onSuccess();
         return true;
      } catch (IOException e) {
         return fail(savable, MeshIOErrorCodes.DATA_NOT_WRITTEN);
      }
   }

   private static void writeMagic(PrimitiveOutputStream pos) throws IOException {
      pos.writeLine(PlyKeywords.PLY);
   }

   private static void writeFormat(PrimitiveOutputStream pos, IPlySaver savable, PlyFormat format) throws IOException {
      format.write(pos, savable);
   }

   private static void writeEnd(PrimitiveOutputStream pos) throws IOException {
      pos.writeLine(PlyKeywords.END);
   }

   private static boolean writeElementHeaders(PrimitiveOutputStream pos, IPlySaver savable, List<PlyElementHeader> elementHeaders)
         throws IOException {
      for (PlyElementHeader elementHeader : elementHeaders) {
         if (elementHeader == null)
            return fail(savable, MeshIOErrorCodes.HEADER_ELEMENTS_MISSING);
         if (!elementHeader.write(pos, savable))
            return false;
      }
      return true;
   }

   private static void writeData(PrimitiveOutputStream pos, IPlySaver savable, PlyFormat format, List<PlyElementHeader> elementHeaders)
         throws IOException {
      format.save(elementHeaders, pos, savable);
   }

   private static boolean fail(IPlySaver savable, int errorCode) {
      savable.onFailure(errorCode);
      return false;
   }
}
