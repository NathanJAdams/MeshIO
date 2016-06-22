package ply;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import meshio.IPlyBuilder;
import meshio.MeshIOErrorCodes;
import util.PrimitiveInputStream;

public class PlyReader {
   public static <T> T read(Class<IPlyBuilder<T>> builderClass, InputStream is) {
      IPlyBuilder<T> builder = null;
      try {
         builder = builderClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
         return null;
      }
      if (is == null)
         return fail(builder, MeshIOErrorCodes.NOT_FOUND, -1);
      try (PrimitiveInputStream pis = new PrimitiveInputStream(is)) {
         if (!isPly(pis, builder))
            return fail(builder, MeshIOErrorCodes.NOT_PLY, pis);
         PlyFormat format = readFormat(pis, builder);
         if (format == null)
            return fail(builder, MeshIOErrorCodes.FORMAT_NOT_FOUND, pis);
         List<PlyElementHeader> elementHeaders = readElementHeaders(pis, builder);
         if (elementHeaders == null)
            return fail(builder, MeshIOErrorCodes.HEADER_NOT_FOUND, pis);
         if (format.load(elementHeaders, pis, builder))
            builder.onSuccess();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return builder.build();
   }

   private static boolean isPly(PrimitiveInputStream pis, IPlyBuilder<?> builder) {
      boolean isValid = PlyKeywords.PLY.equals(readLine(pis, builder));
      if (!isValid)
         fail(builder, MeshIOErrorCodes.NOT_PLY, pis);
      return isValid;
   }

   private static PlyFormat readFormat(PrimitiveInputStream pis, IPlyBuilder<?> builder) {
      String formatLine = readNonCommentLine(pis, builder);
      if (formatLine == null)
         return fail(builder, MeshIOErrorCodes.FORMAT_NOT_FOUND, pis);
      if (!formatLine.startsWith(PlyKeywords.FORMAT))
         return fail(builder, MeshIOErrorCodes.FORMAT_NOT_FOUND, pis);
      String[] formatAndVersion = formatLine.substring(PlyKeywords.FORMAT.length()).split(" ");
      if (formatAndVersion.length != 2)
         return fail(builder, MeshIOErrorCodes.FORMAT_NOT_RECOGNISED, pis);
      PlyFormat format = PlyFormat.getFormat(formatAndVersion[0], formatAndVersion[1]);
      if (format == null)
         return fail(builder, MeshIOErrorCodes.FORMAT_NOT_RECOGNISED, pis);
      return format;
   }

   private static List<PlyElementHeader> readElementHeaders(PrimitiveInputStream pis, IPlyBuilder<?> builder) {
      List<PlyElementHeader> elementHeaders = new ArrayList<>();
      String elementHeaderName = null;
      int elementHeaderCount = 0;
      List<IPlyPropertyHeader> properties = null;
      for (String line = readNonCommentLine(pis, builder); !PlyKeywords.END.equals(line); line = readNonCommentLine(pis, builder)) {
         if (line == null)
            return null;
         boolean isElement = line.startsWith(PlyKeywords.ELEMENT);
         boolean isProperty = line.startsWith(PlyKeywords.PROPERTY);
         if (!isElement && !isProperty) {
            if (PlyKeywords.END.equals(line))
               break;
            else
               return fail(builder, MeshIOErrorCodes.HEADER_NOT_FOUND, pis);
         }
         if (isElement) {
            if (properties != null)
               elementHeaders.add(new PlyElementHeader(elementHeaderName, elementHeaderCount, properties));
            String[] elementNameAndCount = line.substring(PlyKeywords.ELEMENT.length()).split(" ");
            elementHeaderName = elementNameAndCount[0];
            try {
               elementHeaderCount = Integer.parseInt(elementNameAndCount[1]);
            } catch (NumberFormatException e) {
               return fail(builder, MeshIOErrorCodes.HEADER_ELEMENT_COUNT_NOT_READ, pis);
            }
            properties = new ArrayList<>();
            builder.declareElementCount(elementHeaderName, elementHeaderCount);
         } else if (isProperty) {
            if (properties == null)
               return fail(builder, MeshIOErrorCodes.HEADER_UNEXPECTED, pis);
            properties.add(readPropertyHeader(elementHeaderName, pis, builder, line.substring(PlyKeywords.PROPERTY.length())));
         } else {
            return fail(builder, MeshIOErrorCodes.HEADER_NOT_RECOGNISED, pis);
         }
      }
      if (properties != null)
         elementHeaders.add(new PlyElementHeader(elementHeaderName, elementHeaderCount, properties));
      return elementHeaders;
   }

   private static IPlyPropertyHeader readPropertyHeader(String elementName, PrimitiveInputStream pis, IPlyBuilder<?> builder, String line) {
      return (line.startsWith(PlyKeywords.LIST))
            ? createListProperty(elementName, pis, builder, line.substring(PlyKeywords.LIST.length()))
            : createProperty(elementName, pis, builder, line);
   }

   private static IPlyPropertyHeader createListProperty(String elementName, PrimitiveInputStream pis, IPlyBuilder<?> builder, String line) {
      String[] parts = line.split(" ");
      PlyDataType countType = PlyDataType.getDataType(parts[0]);
      PlyDataType type = PlyDataType.getDataType(parts[1]);
      if (countType == null || type == null)
         return fail(builder, MeshIOErrorCodes.HEADER_NOT_RECOGNISED, pis);
      String name = parts[2];
      builder.declarePropertyListType(elementName, name, type);
      return new PlyListPropertyHeader(countType, type, parts[2]);
   }

   private static IPlyPropertyHeader createProperty(String elementName, PrimitiveInputStream pis, IPlyBuilder<?> builder, String line) {
      String[] parts = line.split(" ");
      PlyDataType type = PlyDataType.getDataType(parts[0]);
      if (type == null)
         return fail(builder, MeshIOErrorCodes.HEADER_NOT_RECOGNISED, pis);
      String name = parts[1];
      builder.declarePropertyType(elementName, name, type);
      return new PlyPropertyHeader(type, parts[1]);
   }

   private static String readNonCommentLine(PrimitiveInputStream pis, IPlyBuilder<?> builder) {
      String line;
      do {
         line = readLine(pis, builder);
         if (line == null)
            return fail(builder, MeshIOErrorCodes.HEADER_NOT_FOUND, pis);
      } while (line.startsWith(PlyKeywords.COMMENT));
      return line;
   }

   private static String readLine(PrimitiveInputStream pis, IPlyBuilder<?> builder) {
      try {
         return pis.readLine();
      } catch (IOException e) {
      }
      return null;
   }

   private static <T> T fail(IPlyBuilder<?> builder, int errorCode, PrimitiveInputStream pis) {
      return fail(builder, errorCode, pis.getLineNumber());
   }

   private static <T> T fail(IPlyBuilder<?> builder, int errorCode, int lineNumber) {
      builder.onFailure(errorCode, lineNumber);
      return null;
   }
}
