package ply;

import java.io.IOException;

import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public interface IPlyPropertyHeader {
   void write(PrimitiveOutputStream pos, IPlySavable savable) throws IOException;

   int loadAsciiGetNextDatumIndex(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, String[] parts,
         int partIndex);

   boolean loadBinary(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, boolean isBigEndian);

   void appendAscii(IPlySavable savable, String elementName, int elementIndex, StringBuilder sb);

   void saveBinary(IPlySavable savable, String elementName, int elementIndex, PrimitiveOutputStream pos, boolean isBigEndian) throws IOException;
}
