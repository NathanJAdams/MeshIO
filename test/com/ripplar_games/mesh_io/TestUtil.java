package com.ripplar_games.mesh_io;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ripplar_games.mesh_io.index.IndicesDataType;
import com.ripplar_games.mesh_io.index.IndicesDataTypes;
import com.ripplar_games.mesh_io.mesh.MeshType;
import com.ripplar_games.mesh_io.vertex.VertexDataType;
import com.ripplar_games.mesh_io.vertex.VertexFormat;
import com.ripplar_games.mesh_io.vertex.VertexSubFormat;
import com.ripplar_games.mesh_io.vertex.VertexType;

public class TestUtil {
    private static final float DELTA = 1E-9f;
    private static final Random RANDOM = new Random();
    private static final MeshType[] MESH_TYPES = MeshType.values();
    private static final VertexType[] VERTEX_TYPES = VertexType.getValues();
    private static final List<IndicesDataType<?>> INDICES_DATA_TYPES = IndicesDataTypes.getAllTypes();
    private static final VertexDataType[] VERTEX_DATA_TYPES = VertexDataType.values();

    public static VertexFormat randomVertexFormat() {
        int numSubFormats = RANDOM.nextInt(5);
        List<VertexSubFormat> subFormats = new ArrayList<VertexSubFormat>();
        for (int i = 0; i < numSubFormats; i++) {
            subFormats.add(randomVertexSubFormat());
        }
        return new VertexFormat(subFormats);
    }

    public static VertexSubFormat randomVertexSubFormat() {
        VertexType vertexType = randomVertexType();
        VertexDataType vertexDataType = randomVertexDataType();
        return new VertexSubFormat(vertexType, vertexDataType);
    }

    public static MeshType randomMeshType() {
        return MESH_TYPES[RANDOM.nextInt(MESH_TYPES.length)];
    }

    public static VertexType randomVertexType() {
        return VERTEX_TYPES[RANDOM.nextInt(VERTEX_TYPES.length)];
    }

    public static IndicesDataType<?> randomIndicesDataType() {
        return INDICES_DATA_TYPES.get(RANDOM.nextInt(INDICES_DATA_TYPES.size()));
    }

    public static VertexDataType randomVertexDataType() {
        return VERTEX_DATA_TYPES[RANDOM.nextInt(VERTEX_DATA_TYPES.length)];
    }
}
