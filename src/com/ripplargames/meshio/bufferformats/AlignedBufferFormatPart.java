package com.ripplargames.meshio.bufferformats;

import java.nio.ByteBuffer;

import com.ripplargames.meshio.util.BufferUtil;
import com.ripplargames.meshio.vertex.VertexDataType;

public class AlignedBufferFormatPart {
    private final int offset;
    private final VertexDataType dataType;

    public AlignedBufferFormatPart(int offset, VertexDataType dataType) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be zero or greater");
        }
        this.offset = offset;
        if (dataType == null) {
            throw new NullPointerException("Data Type must not be null");
        }
        this.dataType = dataType;
    }

    public int getOffset() {
        return offset;
    }

    public VertexDataType getDataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AlignedBufferFormatPart other = (AlignedBufferFormatPart) obj;
        return (offset == other.offset)
                && (dataType == other.dataType);
    }

    @Override
    public int hashCode() {
        return 31 * offset + dataType.hashCode();
    }

    public static void main(String[] args) {
        ByteBuffer bb1 = BufferUtil.createByteBuffer(200000);
        ByteBuffer bb2 = BufferUtil.createByteBuffer(200000);
        long pre = System.nanoTime();
        for(int i=0;i<200000;i++){
            bb1.put(i,bb2.get(i));
        }
        long post = System.nanoTime();
        System.out.println((post-pre)*1E-6);
        System.out.println(bb1.get(123456));
    }
}
