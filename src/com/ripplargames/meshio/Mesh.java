package com.ripplargames.meshio;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ripplargames.meshio.indices.IMeshType;
import com.ripplargames.meshio.indices.IndicesDataType;
import com.ripplargames.meshio.util.BufferUtil;
import com.ripplargames.meshio.util.ImmutableIntArray;
import com.ripplargames.meshio.util.ResizableFloatArray;
import com.ripplargames.meshio.vertices.AlignedBufferFormatPart;
import com.ripplargames.meshio.vertices.BufferFormat;
import com.ripplargames.meshio.vertices.VertexDataType;
import com.ripplargames.meshio.vertices.VertexType;

public class Mesh {
    private final List<Face> faces = new ArrayList<Face>();
    private final Map<VertexType, ResizableFloatArray> vertexTypeData = new HashMap<VertexType, ResizableFloatArray>();
    private int maxVertexCount;

    public int vertexCount() {
        return maxVertexCount;
    }

    public int faceCount() {
        return faces.size();
    }

    public ByteBuffer indices(IMeshType meshType, IndicesDataType indicesDataType) throws MeshIOException {
        Set<ImmutableIntArray> filter = new HashSet<ImmutableIntArray>();
        List<ImmutableIntArray> elements = new ArrayList<ImmutableIntArray>();
        for (Face face : faces) {
            ImmutableIntArray[] faceElements = meshType.createElements(face);
            for (ImmutableIntArray faceElement : faceElements) {
                if (filter.add(faceElement)) {
                    elements.add(faceElement);
                }
            }
        }
        return indicesDataType.flatten(elements, meshType.elementLength());
    }

    public ByteBuffer vertices(BufferFormat bufferFormat) throws MeshIOException {
        for (VertexType vertexType : bufferFormat.vertexTypes()) {
            if (!hasVertexTypeData(vertexType)) {
                throw new MeshIOException("No data found for vertex type: " + vertexType.name());
            }
        }
        int byteCount = bufferFormat.byteCount();
        int totalByteCount = byteCount * maxVertexCount;
        ByteBuffer buffer = BufferUtil.createByteBuffer(totalByteCount);
        for (Map.Entry<VertexType, AlignedBufferFormatPart> entry : bufferFormat.alignedParts()) {
            VertexType vertexType = entry.getKey();
            AlignedBufferFormatPart alignedBufferFormatPart = entry.getValue();
            ResizableFloatArray vertexTypeData = vertexTypeData(vertexType);
            int offset = alignedBufferFormatPart.offset();
            VertexDataType dataType = alignedBufferFormatPart.dataType();
            for (int vertexIndex = 0; vertexIndex < maxVertexCount; vertexIndex++) {
                float datum = vertexTypeData.getAt(vertexIndex);
                int index = vertexIndex * byteCount + offset;
                dataType.setDatum(buffer, index, datum);
            }
        }
        return buffer;
    }

    public void appendFace(Face face) {
        faces.add(face);
        maxVertexCount = Math.max(maxVertexCount, face.getV0());
        maxVertexCount = Math.max(maxVertexCount, face.getV1());
        maxVertexCount = Math.max(maxVertexCount, face.getV2());
    }

    public List<Face> faces() {
        return Collections.unmodifiableList(faces);
    }

    public boolean hasVertexTypeData(VertexType vertexType) {
        return vertexTypeData.containsKey(vertexType);
    }

    public Set<VertexType> vertexTypes() {
        return Collections.unmodifiableSet(vertexTypeData.keySet());
    }

    public ResizableFloatArray vertexTypeData(VertexType vertexType) {
        return vertexTypeData.get(vertexType);
    }

    public float vertexTypeDatum(VertexType vertexType, int vertexIndex) {
        return vertexTypeData.get(vertexType).getAt(vertexIndex);
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

    public boolean isValid() throws MeshIOException {
        if (faces.isEmpty()) {
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
        return true;
    }
}
