package com.ripplargames.meshio.indices;

import com.ripplargames.meshio.Face;
import com.ripplargames.meshio.util.ImmutableIntArray;

public interface IMeshType {
    int elementLength();

    ImmutableIntArray[] createElements(Face face);
}
