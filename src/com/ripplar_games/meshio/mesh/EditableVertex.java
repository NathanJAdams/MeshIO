package com.ripplar_games.meshio.mesh;

import com.ripplar_games.meshio.MeshVertexType;

public class EditableVertex {
   private final float[] data = new float[MeshVertexType.getValues().length];

   public void clear() {
      for (int i = 0; i < data.length; i++)
         data[i] = 0;
   }

   public float getDatum(MeshVertexType meshVertexType) {
      return data[meshVertexType.ordinal()];
   }

   public void setDatum(MeshVertexType meshVertexType, float value) {
      data[meshVertexType.ordinal()] = value;
   }
}
