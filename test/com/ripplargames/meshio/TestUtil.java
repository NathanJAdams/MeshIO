package com.ripplargames.meshio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ripplargames.meshio.vertex.VertexDataType;
import com.ripplargames.meshio.bufferformats.BufferFormat;
import com.ripplargames.meshio.bufferformats.BufferFormatPart;
import com.ripplargames.meshio.vertex.VertexType;

public class TestUtil {
    private static final Random RANDOM = new Random();

    public static BufferFormat randomVertexFormat() {
        int numSubFormats = 2 + RANDOM.nextInt(2);
        List<BufferFormatPart> formatParts = new ArrayList<BufferFormatPart>();
        for (int i = 0; i < numSubFormats; i++) {
            formatParts.add(randomFormatPart());
        }
        return new BufferFormat(formatParts);
    }

    public static BufferFormatPart randomFormatPart() {
        VertexType vertexType = randomValue(VertexType.valuesList());
        VertexDataType vertexDataType = randomValue(VertexDataType.valuesList());
        return new BufferFormatPart(vertexType, vertexDataType);
    }

    public static <T> T randomValue(List<T> list) {
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }
}
