package tests.ply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import io.PrimitiveInputStream;
import io.PrimitiveOutputStream;
import meshio.formats.ply.PlyDataType;

public class PlyIODataTypeTest {
   @Test
   public void testDataTypes() {
      for (PlyDataType dataType : PlyDataType.values())
         if (dataType == PlyDataType.Float || dataType == PlyDataType.Double)
            testDataTypeReal(dataType);
         else
            testDataTypeInteger(dataType);
   }

   private void testDataTypeInteger(PlyDataType dataType) {
      final long a = 123;
      final long b = 30;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrimitiveOutputStream pos = new PrimitiveOutputStream(baos);
      try {
         dataType.writeInteger(pos, true, a);
         dataType.writeInteger(pos, false, b);
         pos.flush();
      } catch (IOException e) {
         Assert.fail();
      }
      byte[] buffer = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      PrimitiveInputStream pis = new PrimitiveInputStream(bais);
      try {
         int i = (int) dataType.readInteger(pis, true);
         if (i != a)
            Assert.fail();
      } catch (IOException e) {
         Assert.fail();
      }
      try {
         int i = (int) dataType.readInteger(pis, false);
         if (i != b)
            Assert.fail();
      } catch (IOException e) {
         Assert.fail();
      }
      try {
         pis.read();
      } catch (IOException e) {
         return;
      }
      Assert.fail();
   }

   private void testDataTypeReal(PlyDataType dataType) {
      final double a = 9.875;
      final double b = 19.875;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrimitiveOutputStream pos = new PrimitiveOutputStream(baos);
      try {
         dataType.writeReal(pos, true, a);
         dataType.writeReal(pos, false, b);
         pos.flush();
      } catch (IOException e) {
         Assert.fail();
      }
      byte[] buffer = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      PrimitiveInputStream pis = new PrimitiveInputStream(bais);
      try {
         double d = dataType.readReal(pis, true);
         if (d != a)
            Assert.fail();
      } catch (IOException e) {
         Assert.fail();
      }
      try {
         double d = dataType.readReal(pis, false);
         if (d != b)
            Assert.fail();
      } catch (IOException e) {
         Assert.fail();
      }
      try {
         pis.read();
      } catch (IOException e) {
         return;
      }
      Assert.fail();
   }
}
