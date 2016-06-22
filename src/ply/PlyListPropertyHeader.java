package ply;

import java.io.IOException;

import meshio.IPlyBuilder;
import meshio.IPlySaver;
import meshio.MeshIOErrorCodes;
import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public class PlyListPropertyHeader implements IPlyPropertyHeader {
   private final PlyDataType countType;
   private final PlyDataType type;
   private final String      name;

   public PlyListPropertyHeader(PlyDataType countPlyDataType, PlyDataType PlyDataType, String name) {
      this.countType = countPlyDataType;
      this.type = PlyDataType;
      this.name = name;
   }

   @Override
   public void write(PrimitiveOutputStream pos, IPlySaver savable) throws IOException {
      pos.writeLine(PlyKeywords.PROPERTY + PlyKeywords.LIST + countType.getKeyword() + ' ' + type.getKeyword() + ' ' + name);
   }

   @Override
   public int loadAsciiGetNextDatumIndex(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, String[] parts,
         int partIndex) {
      String countDatum = parts[partIndex++];
      int count;
      try {
         count = countType.readCount(countDatum);
      } catch (NumberFormatException e) {
         loader.onFailure(MeshIOErrorCodes.DATA_NOT_READ, pis.getLineNumber() - 1);
         return -1;
      }
      if (count <= 0) {
         loader.onFailure(MeshIOErrorCodes.DATA_NOT_READ, pis.getLineNumber() - 1);
         return -1;
      }
      if (partIndex + count > parts.length) {
         loader.onFailure(MeshIOErrorCodes.DATA_INSUFFICIENT, pis.getLineNumber() - 1);
         return -1;
      }
      if (!type.readDatumList(loader, elementName, elementIndex, name, count, parts, partIndex)) {
         loader.onFailure(MeshIOErrorCodes.DATA_NOT_READ, pis.getLineNumber() - 1);
         return -1;
      }
      return partIndex + count;
   }

   @Override
   public boolean loadBinary(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, boolean isBigEndian) {
      int count;
      try {
         count = countType.readCount(pis, isBigEndian);
      } catch (IOException e) {
         loader.onFailure(MeshIOErrorCodes.DATA_NOT_READ, pis.getLineNumber());
         return false;
      }
      if (count <= 0) {
         loader.onFailure(MeshIOErrorCodes.DATA_NOT_READ, pis.getLineNumber());
         return false;
      }
      boolean success = type.readDatumList(loader, elementName, elementIndex, name, count, pis, isBigEndian);
      if (!success)
         loader.onFailure(MeshIOErrorCodes.DATA_INSUFFICIENT, pis.getLineNumber());
      return success;
   }

   @Override
   public void appendAscii(IPlySaver savable, String elementName, int elementIndex, StringBuilder sb) {
      type.writeAsciiList(savable, elementName, elementIndex, name, countType, sb);
   }

   @Override
   public void saveBinary(IPlySaver savable, String elementName, int elementIndex, PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
      type.writeBinaryList(savable, elementName, elementIndex, name, countType, pos, isBigEndian);
   }
}
