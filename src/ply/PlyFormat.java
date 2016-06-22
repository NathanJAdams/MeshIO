package ply;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public enum PlyFormat {
   Ascii_1_0("ascii") {
      @Override
      protected boolean isValidVersion(String version) {
         return PlyKeywords.FORMAT_VERSION_1_0.equals(version);
      }

      @Override
      protected boolean load(PlyElementHeader elementHeader, PrimitiveInputStream pis, IPlyBuilder<?> loader) {
         return elementHeader.loadAscii(pis, loader);
      }

      @Override
      protected void save(PlyElementHeader elementHeader, PrimitiveOutputStream pos, IPlySaver savable) throws IOException {
         elementHeader.saveAscii(pos, savable);
      }
   },
   BinaryBigEndian_1_0("binary_big_endian") {
      @Override
      protected boolean isValidVersion(String version) {
         return PlyKeywords.FORMAT_VERSION_1_0.equals(version);
      }

      @Override
      protected boolean load(PlyElementHeader elementHeader, PrimitiveInputStream pis, IPlyBuilder<?> loader) {
         return elementHeader.loadBinary(pis, loader, true);
      }

      @Override
      protected void save(PlyElementHeader elementHeader, PrimitiveOutputStream pos, IPlySaver savable) throws IOException {
         elementHeader.saveBinary(pos, savable, true);
      }
   },
   BinaryLittleEndian_1_0("binary_little_endian") {
      @Override
      protected boolean isValidVersion(String version) {
         return PlyKeywords.FORMAT_VERSION_1_0.equals(version);
      }

      @Override
      protected boolean load(PlyElementHeader elementHeader, PrimitiveInputStream pis, IPlyBuilder<?> loader) {
         return elementHeader.loadBinary(pis, loader, false);
      }

      @Override
      protected void save(PlyElementHeader elementHeader, PrimitiveOutputStream pos, IPlySaver savable) throws IOException {
         elementHeader.saveBinary(pos, savable, false);
      }
   };
   private static final Map<String, PlyFormat> NAMED_FORMATS = new HashMap<>();
   private final String                        keyword;

   static {
      for (PlyFormat format : PlyFormat.values())
         NAMED_FORMATS.put(format.keyword, format);
   }

   PlyFormat(String keyword) {
      this.keyword = keyword;
   }

   public String getKeyword() {
      return keyword;
   }

   public static PlyFormat getFormat(String formatString, String version) {
      PlyFormat format = NAMED_FORMATS.get(formatString);
      if (format.isValidVersion(version))
         return format;
      return null;
   }

   protected abstract boolean isValidVersion(String version);

   public void write(PrimitiveOutputStream pos, IPlySaver savable) throws IOException {
      pos.writeLine(PlyKeywords.FORMAT + keyword + ' ' + PlyKeywords.FORMAT_VERSION_1_0);
   }

   public boolean load(List<PlyElementHeader> elementHeaders, PrimitiveInputStream pis, IPlyBuilder<?> loader) {
      for (PlyElementHeader elementHeader : elementHeaders)
         if (!load(elementHeader, pis, loader))
            return false;
      return true;
   }

   protected abstract boolean load(PlyElementHeader elementHeader, PrimitiveInputStream pis, IPlyBuilder<?> loader);

   public void save(List<PlyElementHeader> elementHeaders, PrimitiveOutputStream pos, IPlySaver savable) throws IOException {
      for (PlyElementHeader elementHeader : elementHeaders)
         save(elementHeader, pos, savable);
   }

   protected abstract void save(PlyElementHeader elementHeader, PrimitiveOutputStream pos, IPlySaver savable) throws IOException;
}
