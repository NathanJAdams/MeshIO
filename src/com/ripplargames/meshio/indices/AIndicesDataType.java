package com.ripplargames.meshio.indices;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.List;

import com.ripplargames.meshio.MeshIOException;
import com.ripplargames.meshio.util.BufferUtil;
import com.ripplargames.meshio.util.ImmutableIntArray;

public abstract class AIndicesDataType implements IndicesDataType {
    @Override
    public ByteBuffer flatten(List<ImmutableIntArray> elements, int elementLength) throws MeshIOException {
        int bytesPerDatum = bytesPerDatum();
        int byteCount = elements.size() * elementLength * bytesPerDatum;
        ByteBuffer buffer = BufferUtil.createByteBuffer(byteCount);
        for (int arrayIndex = 0; arrayIndex < elements.size(); arrayIndex++) {
            ImmutableIntArray array = elements.get(arrayIndex);
            int baseIndex = arrayIndex * elementLength;
            for (int datumIndex = 0; datumIndex < elementLength; datumIndex++) {
                setValue(buffer, (baseIndex + datumIndex) * bytesPerDatum, array.valueAt(datumIndex));
            }
        }
        return buffer;
    }

    public void throwInvalidDataType(Type dataType, int value) throws MeshIOException {
        throw new MeshIOException("Value: " + value + " cannot be used as a " + dataType);
    }
}
