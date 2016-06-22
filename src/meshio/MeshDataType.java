package meshio;

import java.io.IOException;

import util.PrimitiveInputStream;
import util.PrimitiveOutputStream;

public abstract class MeshDataType {
   private static class Int extends MeshDataType {
      private final int byteCount;
      private final int bitMask;

      private Int(String keyword, int byteCount, int bitMask) {
         super(keyword);
         this.byteCount = byteCount;
         this.bitMask = bitMask;
      }

      @Override
      public int readCount(String countDatum) throws NumberFormatException {
         return Integer.parseInt(countDatum) & bitMask;
      }

      @Override
      public int readCount(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
         return pis.readInt(isBigEndian, byteCount) & bitMask;
      }

      @Override
      public void writeCount(StringBuilder sb, int count) {
         sb.append(count & bitMask);
      }

      @Override
      public void writeCount(PrimitiveOutputStream pos, int count, boolean isBigEndian) throws IOException {
         pos.writeInt(count & bitMask, isBigEndian, byteCount);
      }

      @Override
      public boolean readDatum(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, String datum) {
         int i;
         try {
            i = readCount(datum);
         } catch (NumberFormatException e) {
            return false;
         }
         loader.addInt(elementName, elementIndex, propertyName, i);
         return true;
      }

      @Override
      public boolean readDatum(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, PrimitiveInputStream pis,
            boolean isBigEndian) {
         try {
            loader.addInt(elementName, elementIndex, propertyName, readCount(pis, isBigEndian));
         } catch (IOException e) {
            return false;
         }
         return true;
      }

      @Override
      public boolean readDatumList(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyListName, int count, String[] parts,
            int partIndex) {
         int[] iArray = new int[count];
         int i;
         for (int index = 0; index < count; index++) {
            try {
               i = readCount(parts[partIndex + index]);
            } catch (NumberFormatException e) {
               return false;
            }
            iArray[index] = i;
         }
         loader.addIntList(elementName, elementIndex, propertyListName, iArray);
         return true;
      }

      @Override
      public boolean readDatumList(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyListName, int count,
            PrimitiveInputStream pis, boolean isBigEndian) {
         int[] iArray = new int[count];
         int i;
         for (int index = 0; index < count; index++) {
            try {
               i = readCount(pis, isBigEndian);
            } catch (IOException e) {
               return false;
            }
            iArray[index] = i;
         }
         loader.addIntList(elementName, elementIndex, propertyListName, iArray);
         return true;
      }

      @Override
      public void writeAscii(IMeshSaver savable, String elementName, int elementIndex, String propertyName, StringBuilder sb) {
         sb.append(savable.getInt(elementName, elementIndex, propertyName) & bitMask);
      }

      @Override
      public void writeBinary(IMeshSaver savable, String elementName, int elementIndex, String propertyName, PrimitiveOutputStream pos,
            boolean isBigEndian) throws IOException {
         pos.writeInt(savable.getInt(elementName, elementIndex, propertyName) & bitMask, isBigEndian, byteCount);
      }

      @Override
      public void writeAsciiList(IMeshSaver savable, String elementName, int elementIndex, String propertyListName, MeshDataType countType,
            StringBuilder sb) {
         int[] array = savable.getIntArray(elementName, elementIndex, propertyListName);
         countType.writeCount(sb, array.length);
         sb.append(' ');
         for (int i = 0; i < array.length; i++) {
            sb.append(array[i] & bitMask);
            sb.append(' ');
         }
         sb.setLength(sb.length() - 1);
      }

      @Override
      public void writeBinaryList(IMeshSaver savable, String elementName, int elementIndex, String propertyListName, MeshDataType countType,
            PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
         int[] array = savable.getIntArray(elementName, elementIndex, propertyListName);
         countType.writeCount(pos, array.length, isBigEndian);
         for (int i = 0; i < array.length; i++)
            pos.writeInt(array[i] & bitMask, isBigEndian, byteCount);
      }
   }

   private static abstract class Flt extends MeshDataType {
      private Flt(String keyword) {
         super(keyword);
      }

      @Override
      public int readCount(String countDatum) throws NumberFormatException {
         return (int) readFloat(countDatum);
      }

      @Override
      public int readCount(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
         return (int) readFloat(pis, isBigEndian);
      }

      @Override
      public void writeCount(StringBuilder sb, int count) {
         sb.append(count);
      }

      @Override
      public void writeCount(PrimitiveOutputStream pos, int count, boolean isBigEndian) throws IOException {
         pos.writeInt(count, isBigEndian, 4);
      }

      public abstract float readFloat(String countDatum) throws NumberFormatException;

      public abstract float readFloat(PrimitiveInputStream pis, boolean isBigEndian) throws IOException;

      public abstract void writeFloat(IMeshSaver savable, String elementName, int elementIndex, String propertyName, float f, PrimitiveOutputStream pos,
            boolean isBigEndian) throws IOException;

      @Override
      public boolean readDatum(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, String datum) {
         float f;
         try {
            f = readFloat(datum);
         } catch (NumberFormatException e) {
            return false;
         }
         loader.addFloat(elementName, elementIndex, propertyName, f);
         return true;
      }

      @Override
      public boolean readDatum(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, PrimitiveInputStream pis,
            boolean isBigEndian) {
         try {
            loader.addFloat(elementName, elementIndex, propertyName, readFloat(pis, isBigEndian));
         } catch (IOException e) {
            return false;
         }
         return true;
      }

      @Override
      public boolean readDatumList(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyListName, int count, String[] parts,
            int partIndex) {
         float[] fArray = new float[count];
         float f;
         for (int i = 0; i < count; i++) {
            try {
               f = readFloat(parts[partIndex + i]);
            } catch (NumberFormatException e) {
               return false;
            }
            fArray[i] = f;
         }
         loader.addFloatList(elementName, elementIndex, propertyListName, fArray);
         return true;
      }

      @Override
      public boolean readDatumList(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyListName, int count,
            PrimitiveInputStream pis, boolean isBigEndian) {
         float[] fArray = new float[count];
         float f;
         for (int i = 0; i < count; i++) {
            try {
               f = readFloat(pis, isBigEndian);
            } catch (IOException e) {
               return false;
            }
            fArray[i] = f;
         }
         loader.addFloatList(elementName, elementIndex, propertyListName, fArray);
         return true;
      }

      @Override
      public void writeAscii(IMeshSaver savable, String elementName, int elementIndex, String propertyName, StringBuilder sb) {
         sb.append(savable.getFloat(elementName, elementIndex, propertyName));
      }

      @Override
      public void writeBinary(IMeshSaver savable, String elementName, int elementIndex, String propertyName, PrimitiveOutputStream pos,
            boolean isBigEndian) throws IOException {
         float f = savable.getFloat(elementName, elementIndex, propertyName);
         writeFloat(savable, elementName, elementIndex, propertyName, f, pos, isBigEndian);
      }

      @Override
      public void writeAsciiList(IMeshSaver savable, String elementName, int elementIndex, String propertyListName, MeshDataType countType,
            StringBuilder sb) {
         float[] array = savable.getFloatArray(elementName, elementIndex, propertyListName);
         countType.writeCount(sb, array.length);
         sb.append(' ');
         for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            sb.append(' ');
         }
         sb.setLength(sb.length() - 1);
      }

      @Override
      public void writeBinaryList(IMeshSaver savable, String elementName, int elementIndex, String propertyListName, MeshDataType countType,
            PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
         float[] array = savable.getFloatArray(elementName, elementIndex, propertyListName);
         countType.writeCount(pos, array.length, isBigEndian);
         for (int i = 0; i < array.length; i++)
            writeFloat(savable, elementName, elementIndex, propertyListName, array[i], pos, isBigEndian);
      }
   }

   public static MeshDataType         Char   = new Int("char", 1, Byte.MAX_VALUE);
   public static MeshDataType         Uchar  = new Int("uchar", 1, 0xFF);
   public static MeshDataType         Short  = new Int("short", 2, java.lang.Short.MAX_VALUE);
   public static MeshDataType         Ushort = new Int("ushort", 2, 0xFFFF);
   public static MeshDataType         Int    = new Int("int", 4, Integer.MAX_VALUE);
   public static MeshDataType         Uint   = new Int("uint", 4, 0xFFFFFFFF);
   public static MeshDataType         Float  = new Flt("float") {
                                               @Override
                                               public float readFloat(String countDatum) throws NumberFormatException {
                                                  return java.lang.Float.parseFloat(countDatum);
                                               }

                                               @Override
                                               public float readFloat(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
                                                  return pis.readFloat(isBigEndian);
                                               }

                                               @Override
                                               public void writeFloat(IMeshSaver savable, String elementName, int elementIndex, String propertyName,
                                                     float f, PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
                                                  pos.writeFloat(f, isBigEndian);
                                               }
                                            };
   public static MeshDataType         Double = new Flt("double") {
                                               @Override
                                               public float readFloat(String countDatum) throws NumberFormatException {
                                                  return (float) java.lang.Double.parseDouble(countDatum);
                                               }

                                               @Override
                                               public float readFloat(PrimitiveInputStream pis, boolean isBigEndian) throws IOException {
                                                  return (float) pis.readDouble(isBigEndian);
                                               }

                                               @Override
                                               public void writeFloat(IMeshSaver savable, String elementName, int elementIndex, String propertyName,
                                                     float f, PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
                                                  pos.writeDouble(f, isBigEndian);
                                               }
                                            };
   public static final MeshDataType[] VALUES = new MeshDataType[] { Char, Uchar, Short, Ushort, Int, Uint, Float, Double };
   private final String              keyword;

   private MeshDataType(String keyword) {
      this.keyword = keyword;
   }

   public static MeshDataType getDataType(String name) {
      switch (name) {
      case "char":
      case "int8":
         return Char;
      case "uchar":
      case "uint8":
         return Uchar;
      case "short":
      case "int16":
         return Short;
      case "ushort":
      case "uint16":
         return Ushort;
      case "int":
      case "int32":
         return Int;
      case "uint":
      case "uint32":
         return Uint;
      case "float":
      case "float32":
         return Float;
      case "double":
      case "float64":
         return Double;
      default:
         return null;
      }
   }

   public String getKeyword() {
      return keyword;
   }

   public abstract int readCount(String countDatum) throws NumberFormatException;

   public abstract int readCount(PrimitiveInputStream pis, boolean isBigEndian) throws IOException;

   public abstract void writeCount(StringBuilder sb, int count);

   public abstract void writeCount(PrimitiveOutputStream pos, int count, boolean isBigEndian) throws IOException;

   public abstract boolean readDatum(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, String datum);

   public abstract boolean readDatum(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, PrimitiveInputStream pis,
         boolean isBigEndian);

   public abstract boolean readDatumList(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, int count, String[] parts,
         int partIndex);

   public abstract boolean readDatumList(IMeshBuilder<?> loader, String elementName, int elementIndex, String propertyName, int count,
         PrimitiveInputStream pis, boolean isBigEndian);

   public abstract void writeAscii(IMeshSaver savable, String elementName, int elementIndex, String propertyName, StringBuilder sb);

   public abstract void writeBinary(IMeshSaver savable, String elementName, int elementIndex, String propertyName, PrimitiveOutputStream pos,
         boolean isBigEndian) throws IOException;

   public abstract void writeAsciiList(IMeshSaver savable, String elementName, int elementIndex, String propertyName, MeshDataType countType,
         StringBuilder sb);

   public abstract void writeBinaryList(IMeshSaver savable, String elementName, int elementIndex, String propertyName, MeshDataType countType,
         PrimitiveOutputStream pos, boolean isBigEndian) throws IOException;
}
