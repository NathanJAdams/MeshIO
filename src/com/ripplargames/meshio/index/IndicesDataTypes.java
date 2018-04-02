package com.ripplargames.meshio.index;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ripplargames.meshio.util.BufferUtil;

public class IndicesDataTypes {
    public static final IndicesDataType<byte[]> Byte = new IndicesDataType<byte[]>() {
        @Override
        public boolean isValidVertexCount(int vertexCount) {
            return (vertexCount >= 3) && (vertexCount <= 0x7f);
        }

        @Override
        public byte[] createEmptyArray() {
            return new byte[0];
        }

        @Override
        public byte[] createNewArray(byte[] previousArray, int newLength) {
            return Arrays.copyOf(previousArray, newLength);
        }

        @Override
        public int getValue(byte[] array, int index) {
            return array[index];
        }

        @Override
        public void setValue(byte[] array, int index, int value) {
            array[index] = (byte) value;
        }

        @Override
        public ByteBuffer toByteBuffer(byte[] array) {
            return BufferUtil.with(array);
        }

        @Override
        public int bytesPerDatum() {
            return 1;
        }

        @Override
        public String toString() {
            return "Byte";
        }
    };
    public static final IndicesDataType<short[]> Short = new IndicesDataType<short[]>() {
        @Override
        public boolean isValidVertexCount(int vertexCount) {
            return (vertexCount >= 3) && (vertexCount <= 0x7fff);
        }

        @Override
        public short[] createEmptyArray() {
            return new short[0];
        }

        @Override
        public short[] createNewArray(short[] previousArray, int newLength) {
            return Arrays.copyOf(previousArray, newLength);
        }

        @Override
        public int getValue(short[] array, int index) {
            return array[index];
        }

        @Override
        public void setValue(short[] array, int index, int value) {
            array[index] = (short) value;
        }

        @Override
        public ByteBuffer toByteBuffer(short[] array) {
            return BufferUtil.with(array);
        }

        @Override
        public int bytesPerDatum() {
            return 2;
        }

        @Override
        public String toString() {
            return "Short";
        }
    };
    public static final IndicesDataType<int[]> Int = new IndicesDataType<int[]>() {
        @Override
        public boolean isValidVertexCount(int vertexCount) {
            return (vertexCount >= 3);// && (vertexCount <= 0x7fffffff); always true
        }

        @Override
        public int[] createEmptyArray() {
            return new int[0];
        }

        @Override
        public int[] createNewArray(int[] previousArray, int newLength) {
            return Arrays.copyOf(previousArray, newLength);
        }

        @Override
        public int getValue(int[] array, int index) {
            return array[index];
        }

        @Override
        public void setValue(int[] array, int index, int value) {
            array[index] = value;
        }

        @Override
        public ByteBuffer toByteBuffer(int[] array) {
            return BufferUtil.with(array);
        }

        @Override
        public int bytesPerDatum() {
            return 4;
        }

        @Override
        public String toString() {
            return "Int";
        }
    };
    private static final List<IndicesDataType<?>> VALUES;

    static {
        List<IndicesDataType<?>> mutableList = new ArrayList<IndicesDataType<?>>();
        mutableList.add(Byte);
        mutableList.add(Short);
        mutableList.add(Int);
        VALUES = Collections.unmodifiableList(mutableList);
    }

    public static List<IndicesDataType<?>> valuesList() {
        return VALUES;
    }
}
