package com.ripplargames.meshio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ripplargames.meshio.vertex.VertexDataType;
import com.ripplargames.meshio.vertex.VertexFormat;
import com.ripplargames.meshio.vertex.VertexFormatPart;
import com.ripplargames.meshio.vertex.VertexType;

public class TestUtil {
    private static final Random RANDOM = new Random();

    public static VertexFormat randomVertexFormat() {
        int numSubFormats = 2 + RANDOM.nextInt(2);
        List<VertexFormatPart> formatParts = new ArrayList<VertexFormatPart>();
        for (int i = 0; i < numSubFormats; i++) {
            formatParts.add(randomFormatPart());
        }
        return new VertexFormat(formatParts);
    }

    public static VertexFormatPart randomFormatPart() {
        VertexType vertexType = randomValue(VertexType.valuesList());
        VertexDataType vertexDataType = randomValue(VertexDataType.valuesList());
        return new VertexFormatPart(vertexType, vertexDataType);
    }

    public static <T> T randomValue(List<T> list) {
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }
}
