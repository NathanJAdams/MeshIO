package ply;

import java.io.IOException;

import meshio.IMeshBuilder;
import meshio.IMeshSaver;
import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public interface IPlyPropertyHeader {
   void write(PrimitiveOutputStream pos, IMeshSaver savable) throws IOException;

   int loadAsciiGetNextDatumIndex(IMeshBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, String[] parts,
         int partIndex);

   boolean loadBinary(IMeshBuilder<?> loader, String elementName, int elementIndex, PrimitiveInputStream pis, boolean isBigEndian);

   void appendAscii(IMeshSaver savable, String elementName, int elementIndex, StringBuilder sb);

   void saveBinary(IMeshSaver savable, String elementName, int elementIndex, PrimitiveOutputStream pos, boolean isBigEndian) throws IOException;
}
