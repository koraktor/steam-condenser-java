/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2020, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.packets;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;

public class QueryPacketTest {

    @Test
    public void testPadding() {
        QueryPacket packet = new QueryPacket((byte) 0x21, new byte[] { 0x22, 0x23, 0x24, 0x25 }) {};
        byte[] bytes = packet.getBytes();

        assertThat(bytes.length, is(1200));

        assertThat(bytes[0], is((byte) 0xFF));
        assertThat(bytes[1], is((byte) 0xFF));
        assertThat(bytes[2], is((byte) 0xFF));
        assertThat(bytes[3], is((byte) 0xFF));
        assertThat(bytes[4], is((byte) 0x21));
        assertThat(bytes[5], is((byte) 0x22));
        assertThat(bytes[6], is((byte) 0x23));
        assertThat(bytes[7], is((byte) 0x24));
        assertThat(bytes[8], is((byte) 0x25));

        byte[] paddingBytes = ArrayUtils.subarray(bytes, 9, 1200);
        byte[] expectedPadding = new byte[1191];
        Arrays.fill(expectedPadding, (byte) 0x0);
        assertThat(paddingBytes, is(equalTo(expectedPadding)));
    }
}
