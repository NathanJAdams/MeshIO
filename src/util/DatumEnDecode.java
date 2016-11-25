package util;

public class DatumEnDecode {
   private static final long BYTE_RANGE  = 0xFE;
   private static final long SHORT_RANGE = 0xFFFE;
   private static final long INT_RANGE   = 0xFFFFFFFE;

   public static byte encodeAsByte(float decoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (byte) (decoded * Byte.MAX_VALUE)
            : (byte) ((int) (decoded * BYTE_RANGE) - Byte.MAX_VALUE);
   }

   public static short encodeAsShort(float decoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (short) (decoded * Short.MAX_VALUE)
            : (short) ((int) (decoded * SHORT_RANGE) - Short.MAX_VALUE);
   }

   public static int encodeAsInt(float decoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (int) (decoded * Integer.MAX_VALUE)
            : (int) ((int) (decoded * INT_RANGE) - Integer.MAX_VALUE);
   }

   public static float decodeByte(byte encoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (float) ((double) encoded / Byte.MAX_VALUE)
            : (float) ((double) (encoded + Byte.MAX_VALUE) / BYTE_RANGE);
   }

   public static float decodeShort(short encoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (float) ((double) encoded / Short.MAX_VALUE)
            : (float) ((double) (encoded + Short.MAX_VALUE) / SHORT_RANGE);
   }

   public static float decodeInt(int encoded, boolean areNegativesUsed) {
      return areNegativesUsed
            ? (float) ((double) encoded / Integer.MAX_VALUE)
            : (float) ((double) (encoded + Integer.MAX_VALUE) / INT_RANGE);
   }
}
