package com.ripplargames.meshio.indices;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.util.ImmutableIntArray;

public class TrianglesMeshType implements IMeshType {
    @Override
    public int elementLength() {
        return 3;
    }

    @Override
    public ImmutableIntArray[] createElements(Face face) {
        return new ImmutableIntArray[]{new ImmutableIntArray(face.getV0(), face.getV1(), face.getV2())};
    }
}
