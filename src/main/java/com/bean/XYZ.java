package com.bean;

public class XYZ {
    private int X;

    public final int getX() {
        return X;
    }

    public final void setX(int value) {
        X = value;
    }

    private int Y;

    public final int getY() {
        return Y;
    }

    public final void setY(int value) {
        Y = value;
    }

    private int Z;

    public final int getZ() {
        return Z;
    }

    public final void setZ(int value) {
        Z = value;
    }

    private String[] Xz(int a, int b, int c) {
        int d = 0;
        int f = 0;
        d = (int) Math.floor(c / 2.0);
        f = c - d;
        d = (1 << d) - 1 << f;
        f = (1 << f) - 1;

        return new String[]{Integer.toString(c), Integer.toString(a & d | b & f), Integer.toString(b & d | a & f)};
    }

    @Override
    public String toString() {
        return DotNetToJavaStringHelper.join(",", this.Xz(this.getX(), this.getY(), this.getZ()));
    }
}
