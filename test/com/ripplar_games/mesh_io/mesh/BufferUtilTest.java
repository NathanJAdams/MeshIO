package com.ripplar_games.mesh_io.mesh;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

public class BufferUtilTest {
    @Test
    public void testCopy() {
        ByteBuffer bb = BufferUtil.with(new int[]{0, 1, 2});
        ByteBuffer copy = BufferUtil.copy(bb, 12);
        Assert.assertEquals(bb, copy);
    }

    @Test
    public void testReduce() {
        ByteBuffer bb = BufferUtil.with(new int[]{0, 1, 2});
        ByteBuffer copy = BufferUtil.copy(bb, 4);
        Assert.assertEquals(4, copy.capacity());
        Assert.assertEquals(0, copy.position());
        Assert.assertEquals(4, copy.limit());
        Assert.assertEquals(0, copy.getInt());
    }

    @Test
    public void testExpand() {
        ByteBuffer bb = BufferUtil.with(new int[]{0, 1, 2});
        ByteBuffer copy = BufferUtil.copy(bb, 16);
        Assert.assertEquals(16, copy.capacity());
        Assert.assertEquals(0, copy.position());
        Assert.assertEquals(16, copy.limit());
        Assert.assertEquals(0, copy.getInt());
        Assert.assertEquals(1, copy.getInt());
        Assert.assertEquals(2, copy.getInt());
        Assert.assertEquals(0, copy.getInt());
    }
}
