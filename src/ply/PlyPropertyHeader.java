package ply;

import java.io.IOException;

import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public class PlyPropertyHeader implements IPlyPropertyHeader {
   private final PlyDataType type;
   private final String      name;

   public PlyPropertyHeader(PlyDataType PlyDataType, String name) {
      this.type = PlyDataType;
      this.name = name;
   }

   @Override
   public void write(PrimitiveOutputStream pos, IPlySaver savable) throws IOException {
      pos.writeLine(PlyKeywords.PROPERTY + type.getKeyword() + ' ' + name);
   }

   @Override
   public int loadAsciiGetNextDatumIndex(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, String[] parts,
         int partIndex) {
      String datum = parts[partIndex];
      if (!type.readDatum(loader, elementName, elementIndex, name, datum)) {
         loader.onFailure(PlyError.ERROR_DATA_NOT_READ, pis.getLineNumber() - 1);
         return -1;
      }
      return partIndex + 1;
   }

   @Override
   public boolean loadBinary(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, boolean isBigEndian) {
      boolean success = type.readDatum(loader, elementName, elementIndex, name, pis, isBigEndian);
      if (!success)
         loader.onFailure(PlyError.ERROR_DATA_INSUFFICIENT, pis.getLineNumber());
      return success;
   }

   @Override
   public void appendAscii(IPlySaver savable, String elementName, int elementIndex, StringBuilder sb) {
      type.writeAscii(savable, elementName, elementIndex, name, sb);
   }

   @Override
   public void saveBinary(IPlySaver savable, String elementName, int elementIndex, PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
      type.writeBinary(savable, elementName, elementIndex, name, pos, isBigEndian);
   }
}
