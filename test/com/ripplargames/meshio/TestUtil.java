package com.ripplargames.meshio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ripplargames.meshio.vertices.VertexFormat;
import com.ripplargames.meshio.vertices.VertexFormatPart;
import com.ripplargames.meshio.vertices.VertexDataType;
import com.ripplargames.meshio.vertices.VertexType;

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
        VertexType vertexType = randomValue(VertexType.values());
        VertexDataType vertexDataType = randomValue(VertexDataType.values());
        return new VertexFormatPart(vertexType, vertexDataType);
    }

    public static <T> T randomValue(T[] array) {
        int index = (int) (Math.random() * array.length);
        return array[index];
    }
}
