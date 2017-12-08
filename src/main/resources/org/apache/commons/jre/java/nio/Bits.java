/*
 * Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.nio;

import java.util.ArrayList;
import java.util.List;


/**
 * Access to bits, native and otherwise.
 */

class Bits {                            // package-private

    private Bits() { }


    // -- Swapping --

    static short swap(short x) {
      return (short) (((x & 0xFF00) >> 8) | (x << 8));
    }

    static char swap(char x) {
      return (char) (((x & 0xFF00) >> 8) | (x << 8));
    }

    static int swap(int x) {
      return ((x >>> 24)           ) |
          ((x >>   8) &   0xFF00) |
          ((x <<   8) & 0xFF0000) |
          ((x << 24));
    }

    static long swap(long x) {
      long i = (x & 0x00ff00ff00ff00ffL) << 8 | (x >>> 8) & 0x00ff00ff00ff00ffL;
      return (i << 48) | ((i & 0xffff0000L) << 16) |
          ((i >>> 16) & 0xffff0000L) | (i >>> 48);
    }


    // -- get/put char --

    static private char makeChar(byte b1, byte b0) {
        return (char)((b1 << 8) | (b0 & 0xff));
    }

    static char getCharL(ByteBuffer bb, int bi) {
        return makeChar(bb._get(bi + 1),
                        bb._get(bi    ));
    }

    static char getCharL(long a) {
        return makeChar(_get(a + 1),
                        _get(a    ));
    }

    static char getCharB(ByteBuffer bb, int bi) {
        return makeChar(bb._get(bi    ),
                        bb._get(bi + 1));
    }

    static char getCharB(long a) {
        return makeChar(_get(a    ),
                        _get(a + 1));
    }

    static char getChar(ByteBuffer bb, int bi, boolean bigEndian) {
        return bigEndian ? getCharB(bb, bi) : getCharL(bb, bi);
    }

    static char getChar(long a, boolean bigEndian) {
        return bigEndian ? getCharB(a) : getCharL(a);
    }

    private static byte char1(char x) { return (byte)(x >> 8); }
    private static byte char0(char x) { return (byte)(x     ); }

    static void putCharL(ByteBuffer bb, int bi, char x) {
        bb._put(bi    , char0(x));
        bb._put(bi + 1, char1(x));
    }

    static void putCharL(long a, char x) {
        _put(a    , char0(x));
        _put(a + 1, char1(x));
    }

    static void putCharB(ByteBuffer bb, int bi, char x) {
        bb._put(bi    , char1(x));
        bb._put(bi + 1, char0(x));
    }

    static void putCharB(long a, char x) {
        _put(a    , char1(x));
        _put(a + 1, char0(x));
    }

    static void putChar(ByteBuffer bb, int bi, char x, boolean bigEndian) {
        if (bigEndian)
            putCharB(bb, bi, x);
        else
            putCharL(bb, bi, x);
    }

    static void putChar(long a, char x, boolean bigEndian) {
        if (bigEndian)
            putCharB(a, x);
        else
            putCharL(a, x);
    }


    // -- get/put short --

    static private short makeShort(byte b1, byte b0) {
        return (short)((b1 << 8) | (b0 & 0xff));
    }

    static short getShortL(ByteBuffer bb, int bi) {
        return makeShort(bb._get(bi + 1),
                         bb._get(bi    ));
    }

    static short getShortL(long a) {
        return makeShort(_get(a + 1),
                         _get(a    ));
    }

    static short getShortB(ByteBuffer bb, int bi) {
        return makeShort(bb._get(bi    ),
                         bb._get(bi + 1));
    }

    static short getShortB(long a) {
        return makeShort(_get(a    ),
                         _get(a + 1));
    }

    static short getShort(ByteBuffer bb, int bi, boolean bigEndian) {
        return bigEndian ? getShortB(bb, bi) : getShortL(bb, bi);
    }

    static short getShort(long a, boolean bigEndian) {
        return bigEndian ? getShortB(a) : getShortL(a);
    }

    private static byte short1(short x) { return (byte)(x >> 8); }
    private static byte short0(short x) { return (byte)(x     ); }

    static void putShortL(ByteBuffer bb, int bi, short x) {
        bb._put(bi    , short0(x));
        bb._put(bi + 1, short1(x));
    }

    static void putShortL(long a, short x) {
        _put(a    , short0(x));
        _put(a + 1, short1(x));
    }

    static void putShortB(ByteBuffer bb, int bi, short x) {
        bb._put(bi    , short1(x));
        bb._put(bi + 1, short0(x));
    }

    static void putShortB(long a, short x) {
        _put(a    , short1(x));
        _put(a + 1, short0(x));
    }

    static void putShort(ByteBuffer bb, int bi, short x, boolean bigEndian) {
        if (bigEndian)
            putShortB(bb, bi, x);
        else
            putShortL(bb, bi, x);
    }

    static void putShort(long a, short x, boolean bigEndian) {
        if (bigEndian)
            putShortB(a, x);
        else
            putShortL(a, x);
    }


    // -- get/put int --

    static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3       ) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) <<  8) |
                ((b0 & 0xff)      ));
    }

    static int getIntL(ByteBuffer bb, int bi) {
        return makeInt(bb._get(bi + 3),
                       bb._get(bi + 2),
                       bb._get(bi + 1),
                       bb._get(bi    ));
    }

    static int getIntL(long a) {
        return makeInt(_get(a + 3),
                       _get(a + 2),
                       _get(a + 1),
                       _get(a    ));
    }

    static int getIntB(ByteBuffer bb, int bi) {
        return makeInt(bb._get(bi    ),
                       bb._get(bi + 1),
                       bb._get(bi + 2),
                       bb._get(bi + 3));
    }

    static int getIntB(long a) {
        return makeInt(_get(a    ),
                       _get(a + 1),
                       _get(a + 2),
                       _get(a + 3));
    }

    static int getInt(ByteBuffer bb, int bi, boolean bigEndian) {
        return bigEndian ? getIntB(bb, bi) : getIntL(bb, bi) ;
    }

    static int getInt(long a, boolean bigEndian) {
        return bigEndian ? getIntB(a) : getIntL(a) ;
    }

    private static byte int3(int x) { return (byte)(x >> 24); }
    private static byte int2(int x) { return (byte)(x >> 16); }
    private static byte int1(int x) { return (byte)(x >>  8); }
    private static byte int0(int x) { return (byte)(x      ); }

    static void putIntL(ByteBuffer bb, int bi, int x) {
        bb._put(bi + 3, int3(x));
        bb._put(bi + 2, int2(x));
        bb._put(bi + 1, int1(x));
        bb._put(bi    , int0(x));
    }

    static void putIntL(long a, int x) {
        _put(a + 3, int3(x));
        _put(a + 2, int2(x));
        _put(a + 1, int1(x));
        _put(a    , int0(x));
    }

    static void putIntB(ByteBuffer bb, int bi, int x) {
        bb._put(bi    , int3(x));
        bb._put(bi + 1, int2(x));
        bb._put(bi + 2, int1(x));
        bb._put(bi + 3, int0(x));
    }

    static void putIntB(long a, int x) {
        _put(a    , int3(x));
        _put(a + 1, int2(x));
        _put(a + 2, int1(x));
        _put(a + 3, int0(x));
    }

    static void putInt(ByteBuffer bb, int bi, int x, boolean bigEndian) {
        if (bigEndian)
            putIntB(bb, bi, x);
        else
            putIntL(bb, bi, x);
    }

    static void putInt(long a, int x, boolean bigEndian) {
        if (bigEndian)
            putIntB(a, x);
        else
            putIntL(a, x);
    }


    // -- get/put long --

    static private long makeLong(byte b7, byte b6, byte b5, byte b4,
                                 byte b3, byte b2, byte b1, byte b0)
    {
        return ((((long)b7       ) << 56) |
                (((long)b6 & 0xff) << 48) |
                (((long)b5 & 0xff) << 40) |
                (((long)b4 & 0xff) << 32) |
                (((long)b3 & 0xff) << 24) |
                (((long)b2 & 0xff) << 16) |
                (((long)b1 & 0xff) <<  8) |
                (((long)b0 & 0xff)      ));
    }

    static long getLongL(ByteBuffer bb, int bi) {
        return makeLong(bb._get(bi + 7),
                        bb._get(bi + 6),
                        bb._get(bi + 5),
                        bb._get(bi + 4),
                        bb._get(bi + 3),
                        bb._get(bi + 2),
                        bb._get(bi + 1),
                        bb._get(bi    ));
    }

    static long getLongL(long a) {
        return makeLong(_get(a + 7),
                        _get(a + 6),
                        _get(a + 5),
                        _get(a + 4),
                        _get(a + 3),
                        _get(a + 2),
                        _get(a + 1),
                        _get(a    ));
    }

    static long getLongB(ByteBuffer bb, int bi) {
        return makeLong(bb._get(bi    ),
                        bb._get(bi + 1),
                        bb._get(bi + 2),
                        bb._get(bi + 3),
                        bb._get(bi + 4),
                        bb._get(bi + 5),
                        bb._get(bi + 6),
                        bb._get(bi + 7));
    }

    static long getLongB(long a) {
        return makeLong(_get(a    ),
                        _get(a + 1),
                        _get(a + 2),
                        _get(a + 3),
                        _get(a + 4),
                        _get(a + 5),
                        _get(a + 6),
                        _get(a + 7));
    }

    static long getLong(ByteBuffer bb, int bi, boolean bigEndian) {
        return bigEndian ? getLongB(bb, bi) : getLongL(bb, bi);
    }

    static long getLong(long a, boolean bigEndian) {
        return bigEndian ? getLongB(a) : getLongL(a);
    }

    private static byte long7(long x) { return (byte)(x >> 56); }
    private static byte long6(long x) { return (byte)(x >> 48); }
    private static byte long5(long x) { return (byte)(x >> 40); }
    private static byte long4(long x) { return (byte)(x >> 32); }
    private static byte long3(long x) { return (byte)(x >> 24); }
    private static byte long2(long x) { return (byte)(x >> 16); }
    private static byte long1(long x) { return (byte)(x >>  8); }
    private static byte long0(long x) { return (byte)(x      ); }

    static void putLongL(ByteBuffer bb, int bi, long x) {
        bb._put(bi + 7, long7(x));
        bb._put(bi + 6, long6(x));
        bb._put(bi + 5, long5(x));
        bb._put(bi + 4, long4(x));
        bb._put(bi + 3, long3(x));
        bb._put(bi + 2, long2(x));
        bb._put(bi + 1, long1(x));
        bb._put(bi    , long0(x));
    }

    static void putLongL(long a, long x) {
        _put(a + 7, long7(x));
        _put(a + 6, long6(x));
        _put(a + 5, long5(x));
        _put(a + 4, long4(x));
        _put(a + 3, long3(x));
        _put(a + 2, long2(x));
        _put(a + 1, long1(x));
        _put(a    , long0(x));
    }

    static void putLongB(ByteBuffer bb, int bi, long x) {
        bb._put(bi    , long7(x));
        bb._put(bi + 1, long6(x));
        bb._put(bi + 2, long5(x));
        bb._put(bi + 3, long4(x));
        bb._put(bi + 4, long3(x));
        bb._put(bi + 5, long2(x));
        bb._put(bi + 6, long1(x));
        bb._put(bi + 7, long0(x));
    }

    static void putLongB(long a, long x) {
        _put(a    , long7(x));
        _put(a + 1, long6(x));
        _put(a + 2, long5(x));
        _put(a + 3, long4(x));
        _put(a + 4, long3(x));
        _put(a + 5, long2(x));
        _put(a + 6, long1(x));
        _put(a + 7, long0(x));
    }

    static void putLong(ByteBuffer bb, int bi, long x, boolean bigEndian) {
        if (bigEndian)
            putLongB(bb, bi, x);
        else
            putLongL(bb, bi, x);
    }

    static void putLong(long a, long x, boolean bigEndian) {
        if (bigEndian)
            putLongB(a, x);
        else
            putLongL(a, x);
    }


    // -- get/put float --

    static float getFloatL(long a) {
        return _getFloat(a);
    }

    static float getFloatB(long a) {
        return _getFloat(a);
    }

    static float getFloat(long a, boolean bigEndian) {
        return bigEndian ? getFloatB(a) : getFloatL(a);
    }

    static void putFloatL(long a, float x) {
        _putFloat(a, Float.valueOf(x));
    }

    static void putFloatB(long a, float x) {
        _putFloat(a, Float.valueOf(x));
    }

    static void putFloat(long a, float x, boolean bigEndian) {
        if (bigEndian)
            putFloatB(a, x);
        else
            putFloatL(a, x);
    }


    // -- get/put double --

    static double getDoubleL(long a) {
        return _getDouble(a);
    }

    static double getDoubleB(long a) {
        return _getDouble(a);
    }

    static double getDouble(long a, boolean bigEndian) {
        return bigEndian ? getDoubleB(a) : getDoubleL(a);
    }

    static void putDoubleL(long a, double x) {
      _putDouble(a, Double.valueOf(x));
    }

    static void putDoubleB(long a, double x) {
      _putDouble(a, Double.valueOf(x));
    }

    static void putDouble(long a, double x, boolean bigEndian) {
        if (bigEndian)
            putDoubleB(a, x);
        else
            putDoubleL(a, x);
    }


    // -- Unsafe access --

    private static final List<Byte> unsafe = new ArrayList<>(16);

    private static byte _get(long a) {
        return unsafe.get((int) a).byteValue();
    }

    private static void _put(long a, byte b) {
      while (unsafe.size() < a) {
        unsafe.add(Byte.valueOf("0"));
      }
      unsafe.set((int) a, Byte.valueOf(b));
    }


    // -- Unsafe access --

    private static final List<Double> unsafeDouble = new ArrayList<>(16);

    private static Double _getDouble(long a) {
        return unsafeDouble.get((int) a);
    }

    private static void _putDouble(long a, Double b) {
      while (unsafeDouble.size() < a) {
        unsafeDouble.add(Double.NaN);
      }
      unsafeDouble.set((int) a, b);
    }

    // -- Unsafe access --

    private static final List<Float> unsafeFloat = new ArrayList<>(16);

    private static Float _getFloat(long a) {
        return unsafeFloat.get((int) a);
    }

    private static void _putFloat(long a, Float b) {
      while (unsafeFloat.size() < a) {
        unsafeFloat.add(Float.NaN);
      }
      unsafeFloat.set((int) a, b);
    }
}
