package com.util;

/**
 * 高德地图帮助类
 */
public class GaodeMapUtil {
    private static String[] Xz(int a, int b, int c) {
        int d, f;
        d = (int) Math.floor(c / 2);
        f = c - d;
        d = (1 << d) - 1 << f;
        f = (1 << f) - 1;

        return new String[]{Integer.toString(c), Integer.toString(a & d | b & f), Integer.toString(b & d | a & f)};
    }

    /**
     * 高德地图URL 加密
     *
     * @param h z
     * @param l y
     * @param k x
     * @return
     */
    public static String formatXYZ(int h, int l, int k) {

        if (10 > h) {
            int m = (int) Math.pow(2, h);
            if (k >= m || 0 > k)
                k = (k + m) % m;
        }
        return String.join(",", Xz(k, l, h));
    }
}
