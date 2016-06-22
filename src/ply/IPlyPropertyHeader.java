package ply;

import java.io.IOException;

import meshio.IPlyBuilder;
import meshio.IPlySaver;
import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public interface IPlyPropertyHeader {
   void write(PrimitiveOutputStream pos, IPlySaver savable) throws IOException;

   int loadAsciiGetNextDatumIndex(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, String[] parts,
         int partIndex);

   boolean loadBinary(IPlyBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, boolean isBigEndian);

   void appendAscii(IPlySaver savable, String elementName, int elementIndex, StringBuilder sb);

   void saveBinary(IPlySaver savable, String elementName, int elementIndex, PrimitiveOutputStream pos, boolean isBigEndian) throws IOException;
}
