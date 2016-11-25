package tests.meshio.formats.ply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import meshio.MeshFormats;
import meshio.MeshIOException;
import meshio.MeshVertexType;
import meshio.formats.ply.PlyDataType;
import meshio.formats.ply.PlyFormat;
import meshio.formats.ply.PlyIO;
import meshio.mesh.EditableMesh;
import tests.AReadWriteTest;
import util.PrimitiveOutputStream;

public class PlyIOReadTest extends AReadWriteTest {
   @Test
   public void testRead() throws IOException {
      testReadMesh(format(), vertices(), faces());
      testReadMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(), faces());
      testReadMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(new float[] { 0, 0, 0 }),
            faces());
      testReadMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z), vertices(new float[] { 0, 0, 0 }),
            faces(new int[] { 0, 1, 2 }));
      testReadMesh(format(MeshVertexType.Position_X, MeshVertexType.Position_Y), vertices(new float[] { 1, 2, 3, 4 }), faces(new int[] { 0, 1, 2 }));
      testReadMesh(format(MeshVertexType.Position_X, MeshVertexType.Color_R), vertices(new float[] { 1, 2, 3, 4 }), faces(new int[] { 1, 2, 3 }));
   }

   private void testReadMesh(MeshVertexType[] vertexFormat, float[][] vertexData, int[][] faceIndices) throws IOException {
      PlyDataType[] verticesDataTypes = { PlyDataType.Float, PlyDataType.Double };
      PlyDataType[] indicesDataTypes = { PlyDataType.Uchar, PlyDataType.Char, PlyDataType.Uint, PlyDataType.Int, PlyDataType.Ulong,
            PlyDataType.Long };
      for (PlyDataType verticesDataType : verticesDataTypes) {
         for (PlyDataType indicesCountDataType : indicesDataTypes) {
            for (PlyDataType indicesDataType : indicesDataTypes) {
               testReadMesh(verticesDataType, indicesCountDataType, indicesDataType, vertexFormat, vertexData, faceIndices, PlyFormat.ASCII_1_0);
               testReadMesh(verticesDataType, indicesCountDataType, indicesDataType, vertexFormat, vertexData, faceIndices,
                     PlyFormat.BINARY_BIG_ENDIAN_1_0);
               testReadMesh(verticesDataType, indicesCountDataType, indicesDataType, vertexFormat, vertexData, faceIndices,
                     PlyFormat.BINARY_LITTLE_ENDIAN_1_0);
            }
         }
      }
   }

   private void testReadMesh(PlyDataType vertexDataType, PlyDataType indicesCountType, PlyDataType indicesType, MeshVertexType[] vertexFormat,
         float[][] vertexData, int[][] faceIndices, PlyFormat plyFormat) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrimitiveOutputStream pos = new PrimitiveOutputStream(baos);
      pos.writeLine("ply");
      writeComment(pos);
      pos.writeLine("format " + plyFormat.getEncoding() + ' ' + plyFormat.getVersion());
      writeComment(pos);
      int numVertices = (vertexFormat == null || vertexData == null || vertexFormat.length == 0)
            ? 0
            : vertexData.length;
      int numFaces = (faceIndices == null)
            ? 0
            : faceIndices.length;
      pos.writeLine("element vertex " + numVertices);
      writeComment(pos);
      if (vertexFormat != null)
         for (int vertexDataTypeIndex = 0; vertexDataTypeIndex < vertexFormat.length; vertexDataTypeIndex++)
            pos.writeLine("property " + vertexDataType.getRepresentation() + ' ' + PlyIO.getPropertyName(vertexFormat[vertexDataTypeIndex]));
      pos.writeLine("element face " + numFaces);
      writeComment(pos);
      pos.writeLine("property list " + indicesCountType.getRepresentation() + ' ' + indicesType.getRepresentation() + " vertex_index");
      writeComment(pos);
      pos.writeLine("end_header");
      writeVertices(plyFormat, vertexFormat, vertexDataType, numVertices, vertexData, pos);
      writeIndices(plyFormat, indicesCountType, indicesType, numFaces, faceIndices, pos);
      pos.flush();
      byte[] buffer = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
      EditableMesh actualMesh = new EditableMesh();
      actualMesh.setVertexFormat(vertexFormat);
      try {
         MeshFormats.Ply_WritesAscii.read(actualMesh, bais);
      } catch (MeshIOException e) {
         Assert.fail();
      }
      EditableMesh expectedMesh = createMesh(vertexFormat, vertexData, faceIndices);
      checkMeshEquals(expectedMesh, actualMesh);
   }

   private void writeComment(PrimitiveOutputStream pos) throws IOException {
      pos.writeLine("comment blah blah blah");
   }

   private void writeVertices(PlyFormat plyFormat, MeshVertexType[] vertexFormat, PlyDataType vertexDataType, int numVertices, float[][] vertexData,
         PrimitiveOutputStream pos) throws IOException {
      if (plyFormat == PlyFormat.ASCII_1_0) {
         StringBuilder sb = new StringBuilder();
         for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++) {
            sb.setLength(0);
            for (int datumIndex = 0; datumIndex < vertexFormat.length; datumIndex++) {
               sb.append(vertexData[vertexIndex][datumIndex]);
               sb.append(' ');
            }
            sb.setLength(sb.length() - 1);
            pos.writeLine(sb.toString());
         }
      } else if (plyFormat == PlyFormat.BINARY_BIG_ENDIAN_1_0 || plyFormat == PlyFormat.BINARY_LITTLE_ENDIAN_1_0) {
         boolean isBigEndian = (plyFormat == PlyFormat.BINARY_BIG_ENDIAN_1_0);
         writeVerticesBinary(vertexFormat, vertexDataType, numVertices, vertexData, pos, isBigEndian);
      }
   }

   private void writeVerticesBinary(MeshVertexType[] vertexFormat, PlyDataType vertexDataType, int numVertices, float[][] vertexData,
         PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
      for (int vertexIndex = 0; vertexIndex < numVertices; vertexIndex++)
         for (int datumIndex = 0; datumIndex < vertexFormat.length; datumIndex++)
            vertexDataType.writeReal(pos, isBigEndian, vertexData[vertexIndex][datumIndex]);
   }

   private void writeIndices(PlyFormat plyFormat, PlyDataType countType, PlyDataType indicesType, int numFaces, int[][] indices,
         PrimitiveOutputStream pos) throws IOException {
      if (plyFormat == PlyFormat.ASCII_1_0) {
         StringBuilder sb = new StringBuilder();
         for (int faceIndex = 0; faceIndex < numFaces; faceIndex++) {
            sb.setLength(0);
            sb.append(3);
            sb.append(' ');
            int startIndex = faceIndex * 3;
            for (int datumIndex = 0; datumIndex < 3; datumIndex++) {
               sb.append(indices[startIndex][datumIndex]);
               sb.append(' ');
            }
            sb.setLength(sb.length() - 1);
            pos.writeLine(sb.toString());
         }
      } else if (plyFormat == PlyFormat.BINARY_BIG_ENDIAN_1_0 || plyFormat == PlyFormat.BINARY_LITTLE_ENDIAN_1_0) {
         boolean isBigEndian = (plyFormat == PlyFormat.BINARY_BIG_ENDIAN_1_0);
         writeIndicesBinary(plyFormat, countType, indicesType, numFaces, indices, pos, isBigEndian);
      }
   }

   private void writeIndicesBinary(PlyFormat plyFormat, PlyDataType countType, PlyDataType indicesType, int numFaces, int[][] indices,
         PrimitiveOutputStream pos, boolean isBigEndian) throws IOException {
      for (int faceIndex = 0; faceIndex < numFaces; faceIndex++) {
         countType.writeInteger(pos, isBigEndian, 3);
         for (int datumIndex = 0; datumIndex < 3; datumIndex++)
            indicesType.writeInteger(pos, isBigEndian, indices[faceIndex][datumIndex]);
      }
   }
}
