package com.bean;

public class Pix {
    private double x;
    private double y;

    public Pix(double xp, double yp) {
        this.x = xp;
        this.y = yp;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    private void test()
    {
////        function getPixPosition(x, y, z, col, row) {
////        var resolutions =[
////        156543.0339,
////                78271.516953125,
////                39135.7584765625,
////                19567.87923828125,
////                9783.939619140625,
////                4891.9698095703125,
////                2445.9849047851562,
////                1222.9924523925781,
////                611.4962261962891,
////                305.74811309814453,
////                152.87405654907226,
////                76.43702827453613,
////                38.218514137268066,
////                19.109257068634033,
////                9.554628534317016,
////                4.777314267158508,
////                2.388657133579254,
////                1.194328566789627,
////                0.5971642833948135,
////                0.29858214169740677
////    ];
////        var maxExtent = 20037508.3427892,
////                resolution = this.resolutions[z],
////                px = row * (resolution * 256) - maxExtent + resolution * x,
////                py = maxExtent - col * (resolution * 256) - resolution * y;
////        // px = px / 20037508.34 * 180;
////        // py = py / 20037508.34 * 180;
////        // py = 180 / Math.PI * (2 * Math.atan(Math.exp(py * Math.PI / 180)) - Math.PI / 2);
////        return {
////                x: px,
////                y: py
////        };
//    }
    }
}
