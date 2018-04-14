package com.ripplargames.meshio.indices;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.util.ImmutableIntArray;

public class LinesMeshType implements IMeshType {
    @Override
    public int elementLength() {
        return 2;
    }

    @Override
    public ImmutableIntArray[] createElements(Face face) {
        int v0 = face.getV0();
        int v1 = face.getV1();
        int v2 = face.getV2();
        ImmutableIntArray line01 = new ImmutableIntArray(v0, v1);
        ImmutableIntArray line12 = new ImmutableIntArray(v1, v2);
        ImmutableIntArray line20 = new ImmutableIntArray(v2, v0);
        return new ImmutableIntArray[]{line01, line12, line20};
    }
}
