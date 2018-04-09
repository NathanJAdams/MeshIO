package com.ripplargames.meshio;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.util.ResizableFloatArray;
import com.ripplargames.meshio.util.ResizableIntArray;
import com.ripplargames.meshio.vertex.VertexType;

public class MeshRawData {
    private final ResizableIntArray indices = new ResizableIntArray();
    private final Map<VertexType, ResizableFloatArray> vertexTypeData = new HashMap<VertexType, ResizableFloatArray>();
    private int maxVertexCount;


    public int getVertexCount() {
        return maxVertexCount;
    }

    public int getFaceCount() {
        return indices.length() / 3;
    }

    public int[] getFace(int faceIndex) {
        int[] face = new int[3];
        int baseIndex = faceIndex * 3;
        face[0] = indices.getAt(baseIndex + 0);
        face[1] = indices.getAt(baseIndex + 1);
        face[2] = indices.getAt(baseIndex + 2);
        return face;
    }

    public int[] getIndices() {
        return indices.copyArray();
    }

    public boolean hasVertexTypeData(VertexType vertexType) {
        return vertexTypeData.containsKey(vertexType);
    }

    public Set<VertexType> getVertexTypes() {
        return Collections.unmodifiableSet(vertexTypeData.keySet());
    }

    public float[] getVertexTypeData(VertexType vertexType) {
        return vertexTypeData.get(vertexType).copyArray();
    }

    public float getVertexTypeDatum(VertexType vertexType, int vertexIndex) {
        return vertexTypeData.get(vertexType).getAt(vertexIndex);
    }

    public void appendVertexTypeDatum(VertexType vertexType, float datum) {
        getValidVertexTypeData(vertexType).append(datum);
        maxVertexCount++;
    }

    public void setVertexTypeDatum(VertexType vertexType, int index, float datum) {
        getValidVertexTypeData(vertexType).setAt(index, datum);
        if (index >= maxVertexCount) {
            maxVertexCount = index + 1;
        }
    }

    private ResizableFloatArray getValidVertexTypeData(VertexType vertexType) {
        ResizableFloatArray data = vertexTypeData.get(vertexType);
        if (data == null) {
            data = new ResizableFloatArray();
            vertexTypeData.put(vertexType, data);
        }
        return data;
    }

    public void appendFace(int vertex0, int vertex1, int vertex2) {
        indices.append(vertex0);
        indices.append(vertex1);
        indices.append(vertex2);
    }

    public void setFace(int faceIndex, int vertex0, int vertex1, int vertex2) {
        int baseIndex = faceIndex * 3;
        indices.setAt(baseIndex + 0, vertex0);
        indices.setAt(baseIndex + 1, vertex1);
        indices.setAt(baseIndex + 2, vertex2);
    }

    public boolean isValid() throws MeshIOException {
        if (getFaceCount() == 0) {
            throw new MeshIOException("There must be at least 1 face present");
        }

        if (!vertexTypeData.containsKey(VertexType.Position_X)
                || !vertexTypeData.containsKey(VertexType.Position_Y)
                || !vertexTypeData.containsKey(VertexType.Position_Z)) {
            throw new MeshIOException("There must be (x,y,z) position data present");
        }

        Map<VertexType, Integer> vertexLengths = new HashMap<VertexType, Integer>();
        for (Map.Entry<VertexType, ResizableFloatArray> entry : vertexTypeData.entrySet()) {
            vertexLengths.put(entry.getKey(), entry.getValue().length());
        }
        Set<Integer> lengths = new HashSet<Integer>(vertexLengths.values());
        if (lengths.size() != 1) {
            throw new MeshIOException("The vertex data must be of equal length. " + vertexLengths);
        }
        int vertexCount = lengths.iterator().next();

        int[] indicesArray = indices.copyArray();
        for (int i = 0; i < indicesArray.length; i++) {
            int index = indicesArray[i];
            if ((index < 0) || (index + 1 > vertexCount)) {
                throw new MeshIOException("Face at position " + (i / 3) + " references vertex index " + index + ",  it must reference an index between 0 and " + vertexCount);
            }
        }
        return true;
    }
}
