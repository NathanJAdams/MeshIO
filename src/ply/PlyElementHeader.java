package ply;

import java.io.IOException;
import java.util.List;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import meshio.MeshIOErrorCodes;
import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public class PlyElementHeader {
   private final String                   name;
   private final int                      count;
   private final List<IPlyPropertyHeader> propertyHeaders;

   public PlyElementHeader(String name, int count, List<IPlyPropertyHeader> propertyHeaders) {
      this.name = name;
      this.count = count;
      this.propertyHeaders = propertyHeaders;
   }

   public boolean write(PrimitiveOutputStream pos, IMeshSaver savable) throws IOException {
      if (propertyHeaders == null || propertyHeaders.isEmpty()) {
         savable.onFailure(MeshIOErrorCodes.HEADER_NOT_FOUND);
         return false;
      }
      pos.writeLine(PlyKeywords.ELEMENT + name + ' ' + count);
      for (IPlyPropertyHeader propertyHeader : propertyHeaders) {
         if (propertyHeader == null) {
            savable.onFailure(MeshIOErrorCodes.HEADER_NOT_FOUND);
            return false;
         }
         propertyHeader.write(pos, savable);
      }
      return true;
   }

   public boolean loadAscii(PrimitiveInputStream pis, IMeshBuilder<?> loader) {
      for (int i = 0; i < count; i++) {
         String element = null;
         try {
            element = pis.readLine();
         } catch (IOException e) {
            loader.onFailure(MeshIOErrorCodes.DATA_NOT_FOUND, pis.getLineNumber());
            return false;
         }
         if (element == null) {
            loader.onFailure(MeshIOErrorCodes.DATA_NOT_FOUND, pis.getLineNumber() - 1);
            return false;
         }
         String[] parts = element.split(" ");
         if (parts == null) {
            loader.onFailure(MeshIOErrorCodes.DATA_NOT_FOUND, pis.getLineNumber() - 1);
            return false;
         }
         if (parts.length == 0) {
            loader.onFailure(MeshIOErrorCodes.DATA_INSUFFICIENT, pis.getLineNumber() - 1);
            return false;
         }
         for (int partIndex = 0, propertyIndex = 0; propertyIndex < propertyHeaders.size(); propertyIndex++) {
            IPlyPropertyHeader propertyHeader = propertyHeaders.get(propertyIndex);
            if (partIndex >= parts.length) {
               loader.onFailure(MeshIOErrorCodes.DATA_INSUFFICIENT, pis.getLineNumber());
               return false;
            }
            partIndex = propertyHeader.loadAsciiGetNextDatumIndex(loader, name, i, pis, parts, partIndex);
            if (partIndex == -1)
               return false;
         }
      }
      return true;
   }

   public boolean loadBinary(PrimitiveInputStream pis, IMeshBuilder<?> loader, boolean isBigEndian) {
      for (int i = 0; i < count; i++)
         for (IPlyPropertyHeader propertyHeader : propertyHeaders)
            if (!propertyHeader.loadBinary(loader, name, i, pis, isBigEndian))
               return false;
      return true;
   }

   public void saveAscii(PrimitiveOutputStream pos, IMeshSaver savable) throws IOException {
      for (int i = 0; i < count; i++) {
         StringBuilder sb = new StringBuilder();
         for (IPlyPropertyHeader propertyHeader : propertyHeaders) {
            propertyHeader.appendAscii(savable, name, i, sb);
            sb.append(' ');
         }
         sb.setLength(sb.length() - 1);
         pos.writeLine(sb.toString());
      }
   }

   public void saveBinary(PrimitiveOutputStream pos, IMeshSaver savable, boolean isBigEndian) throws IOException {
      for (int i = 0; i < count; i++)
         for (IPlyPropertyHeader propertyHeader : propertyHeaders)
            propertyHeader.saveBinary(savable, name, i, pos, isBigEndian);
   }
}
