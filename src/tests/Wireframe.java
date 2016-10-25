package tests;

import java.util.HashMap;
import java.util.Map;

import meshio.IMeshSaver;
import meshio.MeshVertexType;
import meshio.util.PrimitiveOutputStream;

public class Wireframe implements IMeshSaver {
   private static final MeshVertexType[] FORMAT = { MeshVertexType.Position_X, MeshVertexType.Position_Y, MeshVertexType.Position_Z,
         MeshVertexType.Color_R, MeshVertexType.Color_G, MeshVertexType.Color_B, MeshVertexType.Color_A };
   private final Vertex[]                vertices;
   private final Face[]                  faces;

   public static void main(String... s) {
      System.out.println("Hello");
      Vertex[] vertices = new Vertex[1];
      vertices[0] = new Vertex();
      vertices[0].setPositionX(1);
      vertices[0].setPositionY(2);
      vertices[0].setPositionZ(3);
      vertices[0].setNormalX(4);
      vertices[0].setNormalY(5);
      vertices[0].setNormalZ(6);
      vertices[0].setImageCoordX(7);
      vertices[0].setImageCoordY(8);
      vertices[0].setColorR(9);
      vertices[0].setColorG(10);
      vertices[0].setColorB(11);
      vertices[0].setColorA(12);
      Face[] faces = new Face[1];
      faces[0] = new Face();
      faces[0].setFaceIndex0(1);
      faces[0].setFaceIndex1(2);
      faces[0].setFaceIndex2(3);
      IMeshSaver saver = new Wireframe(vertices, faces);
      PrimitiveOutputStream pos = null;
      short metadata = 0;
      int vertexCount = 1;
      MeshVertexType[] format = FORMAT;
      Map<MeshVertexType, Integer> typeIndexes = new HashMap<>();
      for (int i = 0; i < format.length; i++) {
         typeIndexes.put(format[i], i);
      }
      // try {
      // TODO
      // MbwfIO.writeVertices(saver, pos, metadata, vertexCount, format, typeIndexes);
      // } catch (IOException | MeshIOException e) {
      // }
   }

   public Wireframe(Vertex[] vertices, Face[] faces) {
      this.vertices = vertices;
      this.faces = faces;
   }

   @Override
   public MeshVertexType[] getVertexFormat() {
      return FORMAT;
   }

   @Override
   public int getVertexCount() {
      return vertices.length;
   }

   @Override
   public int getFaceCount() {
      return faces.length;
   }

   @Override
   public void getVertexData(int vertexIndex, float[] vertexData) {
      System.out.println("num vertices: " + vertices.length);
      Vertex vertex = vertices[vertexIndex];
      float[] data = vertex.getVertexData();
      System.out.println("used vertex data length: " + data.length);
      System.out.println("saved vertex data length: " + vertexData.length);
      for (int i = 0; i < data.length; i++)
         vertexData[i] = data[i];
   }

   @Override
   public void getFaceIndices(int faceIndex, int[] faceIndices) {
      Face face = faces[faceIndex];
      short[] indices = face.getFaceIndices();
      for (int i = 0; i < 3; i++)
         faceIndices[i] = indices[i];
   }

   public float[] getVertices(VertexType... vertexTypes) {
      int countPerVertex = 0;
      for (VertexType vertexType : vertexTypes)
         countPerVertex += vertexType.getCount();
      float[] filledData = new float[vertices.length * countPerVertex];
      for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++) {
         float[] vertexData = vertices[vertexIndex].getVertexData();
         int filledStartIndex = vertexIndex * countPerVertex;
         for (int vertexTypeIndex = 0; vertexTypeIndex < vertexTypes.length; vertexTypeIndex++) {
            VertexType vertexType = vertexTypes[vertexTypeIndex];
            int vertexTypeOffset = vertexType.getOffset();
            int vertexTypeCount = vertexType.getCount();
            for (int i = 0; i < vertexTypeCount; i++)
               filledData[filledStartIndex + i] = vertexData[vertexTypeOffset + i];
            filledStartIndex += vertexTypeCount;
         }
      }
      return filledData;
   }

   public float getRadiusSquaredForScale(Vector3 scale) {
      return getRadiusSquaredForScale(scale.getX(), scale.getY(), scale.getZ());
   }

   public float getRadiusSquaredForScale(float scaleX, float scaleY, float scaleZ) {
      float maxRadiusSq = 0;
      for (int vertexIndex = 0; vertexIndex < vertices.length; vertexIndex++) {
         float[] vertexData = vertices[vertexIndex].getVertexData();
         float vertexPositionX = vertexData[0] * scaleX;
         float vertexPositionY = vertexData[1] * scaleY;
         float vertexPositionZ = vertexData[2] * scaleZ;
         float radiusSq = vertexPositionX * vertexPositionX + vertexPositionY * vertexPositionY + vertexPositionZ * vertexPositionZ;
         if (radiusSq > maxRadiusSq)
            maxRadiusSq = radiusSq;
      }
      return maxRadiusSq;
   }

   public static Wireframe createSquare() {
      Vertex v0 = Vertex.create(-0.5f, -0.5f, 1, 0, 0);
      Vertex v1 = Vertex.create(-0.5f, 0.5f, 0, 1, 0);
      Vertex v2 = Vertex.create(0.5f, -0.5f, 0, 0, 1);
      Vertex v3 = Vertex.create(0.5f, 0.5f, 1, 1, 1);
      v0.setImageCoordX(0);
      v0.setImageCoordY(1);
      v1.setImageCoordX(0);
      v1.setImageCoordY(0);
      v2.setImageCoordX(1);
      v2.setImageCoordY(1);
      v3.setImageCoordX(1);
      v3.setImageCoordY(0);
      Face f0 = Face.create(0, 1, 2);
      Face f1 = Face.create(1, 2, 3);
      Vertex[] vertices = { v0, v1, v2, v3 };
      Face[] faces = { f0, f1 };
      return new Wireframe(vertices, faces);
   }
}
