package com.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public class GaoDeMapUtil {
    private double[] resolutions;

    public GaoDeMapUtil(double[] res) {
        this.resolutions = res;
    }

    public final void building(ArrayList<JSONObject> data, JSONArray p, String m) {

        String[] s = null;
        String[] style = null;
        if (p.get(1) != null) {
            s = p.get(1).toString().split("[&]", -1);
            style = new String[]{this.getRgb(s[0]), s[1], this.getRgb(s[2]), this.getRgb(s[3]), this.getRgb(s[4]), s[5], s[6]};
        }else {
            return;
        }

        JSONObject O = new JSONObject();

        O.put("labels", p.get(2));
        O.put("type", m);
        O.put("rank", p.get(3));
        O.put("offset", Integer.parseInt(p.get(3).toString()) * Math.pow(2, 2));
        O.put("style", style);

        JSONArray pk = (JSONArray) ((p.get(0) instanceof JSONArray) ? p.get(0) : null);
        JSONArray buildings = new JSONArray();
        for (int k = 0; k < pk.size(); k++) {
            String w = pk.get(k).toString();
            buildings.add(this.NC(w));
        }
        O.put("buildings", buildings);
        data.add(O);
    }

    public final void region(ArrayList<JSONObject> data, JSONArray p, String m) {

        String[] s = null;
        String[] style = null;
        if (p.get(1) != null) {
            s = p.get(1).toString().split("&", -1);
            style = new String[]{this.getRgb(s[0])};
        }else {
            return;
        }

        JSONObject O = new JSONObject();
        JSONArray regions = new JSONArray();


        O.put("labels", p.get(3));
        O.put("type", m);
        O.put("rank", p.get(3).toString().equals("regions:traffic") ? 63 : p.get(2));
        O.put("style", style);

        JSONArray pk = (JSONArray) ((p.get(0) instanceof JSONArray) ? p.get(0) : null);
        for (int k = 0; k < pk.size(); k++) {
            String w = pk.get(k).toString();
            regions.add(this.NC(w));
        }
        O.put("regions", regions);
        data.add(O);
    }

    public void roadLine(ArrayList<JSONObject> data, JSONArray p, String m, Object index) {

        String[] s = null;
        String fillStyle = null;
        String strokeStyle = null;
        int width = 0;
        if (p.get(1) != null) {
            s = p.get(1).toString().split("&", -1);
            fillStyle = this.getRgb(s[4]);
            strokeStyle = this.getRgb(s[1]);
            width = Integer.parseInt(s[0]);

        }else {
            return;
        }


        JSONObject O = new JSONObject();
        JSONArray roads = new JSONArray();


        O.put("labels", p.get(3));
        O.put("type", m);
        O.put("fillStyle", fillStyle);
        O.put("strokeStyle", strokeStyle);
        O.put("width", width);
        O.put("rank", p.get(3).toString().equals("regions:traffic") ? 63 : p.get(2));
        O.put("style", s);
        O.put("FK", index);

        JSONArray pk = (JSONArray) ((p.get(0) instanceof JSONArray) ? p.get(0) : null);
        for (int k = 0; k < pk.size(); k++) {
            String w = pk.get(k).toString();
            roads.add(this.NC(w));
        }
        O.put("roads", roads);
        data.add(O);
    }


    public final Pix getPixPosition(int x, int y, int z, double col, double row) {
        double maxExtent = 20037508.3427892;
        double resolution = this.resolutions[z];
        double px = row * (resolution * 256) - maxExtent + resolution * x;
        double py = maxExtent - col * (resolution * 256) - resolution * y;

        return new Pix(px, py);
    }

    private double[] k(String a) {
        ArrayList<Double> b = new ArrayList<Double>();
        int d = a.length();
        for (int c = 0; c < d; c += 2) {
            b.add(Integer.parseInt(a.substring(c, c + 2), 16) / 255.0);
        }
        if (b.size() < 4) {
            return new double[]{0.0, 0, 0, 0};
        }
        return new double[]{b.get(1), b.get(2), b.get(3), b.get(0)};
    }

    public final String getRgb(String s) {
        if (s.equals("") || s.length() == 1) {
            return null;
        }
        double[] f = this.k(s);

        return "rgba(" + DotNetToJavaStringHelper.join(",", new String[]{Double.toString(f[0] * 255), Double.toString(f[1] * 255), Double.toString(f[2] * 255), Double.toString(f[3])}) + ")";
    }

    private String WW = "ASDFGHJKLQWERTYUIO!sdfghjkl";

    public final Integer[] NC(String a) {
        int b = -1;
        ArrayList<Integer> c = new ArrayList<Integer>();
        Integer d = null;
        int f = 0;
        for (int g = a.length(); f < g; f += 1) {
            char b1 = a.charAt(f);
            b = this.WW.indexOf(b1);


            if (d == null) {
                d = 27 * b;
            } else {
                c.add(d.intValue() + b - 333);
                d = null;
            }
        }
        Integer[] r = new Integer[c.size()];
        c.toArray(r);
        return r;
    }

    class XYZN {
        public int x;
        public int y;
        public int z;
        public double N;

        public XYZN(double n, int x, int y, int z) {
            this.N = n;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }


    /**
     * @param a
     * @param b [x,y,N]
     * @return
     */
    public final double[] jM(Integer[] a, GaoDeMapUtil.XYZN b) {
        double c = 0,
                d = 0,
                f = 1;
        c = 256 * b.x;
        d = 256 * b.y;
        f = b.N;
        return new double[]{(c + a[0]) * f, (d + a[1]) * f};
    }

    public final GaoDeLabel poiFill(JSONArray w, String[] l) {
        Integer[] p = this.NC(w.get(1).toString());

        double[] J = this.jM(p, new GaoDeMapUtil.XYZN(
                Math.pow(2, 20 - Integer.parseInt(l[0])), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[0])
        ));
        GaoDeLabel o = new GaoDeLabel();
        //o.setName(w.get(0).toString().split("^"));
        o.setName(new String[]{w.get(0).toString().replace('^',' ')});
        o.setLocation(w.get(2));
        o.setBaseX(p[0]);
        o.setBaseY(p[1]);
        o.setType(l[3]);
        o.setAu(new ArrayList<String>());
        o.setLb(new ArrayList<Object>());
        return o;
    }

    private String normalsmall = "icon-normal-small.png";
    private String bizsmall = "icon-biz-small.png";

    public final void poiLabelIcon(JSONArray w, GaoDeLabel o, String c, String[] s) {
        if(c == null || c.equals("")){
            return;
        }
        boolean k = false;
        boolean h = false;

        if (o.getType().toString().equals("poilabel")) {
            if (!DotNetToJavaStringHelper.isNullOrEmpty(c) && !DotNetToJavaStringHelper.isNullOrEmpty(w.get(3).toString())) {
                o.getAu().add(s[s.length - 1].equals("1") ? this.bizsmall : this.normalsmall);
                int C = Integer.parseInt(c) - 1;
                double x = Math.floor(C / 10.0);
                int G = C % 10;
                int y = 0, A = 0, D = 0;

                if (k) {
                    y = 48;
                    A = 40;
                    D = 28;
                } else {
                    if (h) {
                        y = A = 40;
                        D = 28;
                    } else {
                        y = A = 24;
                        D = 20;
                    }
                }
                int z = y;
                if (151 == C || 152 == C || 153 == C) {
                    // debugger;
                    // D -= 4,
                    //     M = w[7][0][2],
                    //     z = y * Math.max(M + 2, D) / D;
                }

                o.getLb().add(new int[]{-z / 2, -y / 2, z, y, A * G, (int) (A * x), A, A});
            }
        } else if (o.getType().toString().equals("roadlabel")) {
            if (!DotNetToJavaStringHelper.isNullOrEmpty(c)) {
                ArrayList J = (ArrayList) ((w.get(3) instanceof ArrayList) ? w.get(3) : null);
                if (J == null) {
                    return;
                }

                if (J.size() < 1 || DotNetToJavaStringHelper.isNullOrEmpty(J.get(2).toString())) {
                    return;
                }

                Object M = J.get(2);
                double Q = -Math.floor(Integer.parseInt(J.get(3).toString()) / 2.0);
                int C = Integer.parseInt(c) - 1;

                double x = Math.floor(C / 10.0);

                int G = C % 10;
                int y = 0;
                int A = 0;
                // dynamic O = o;
                int D = 0;
                //k ? (y = 48,
                //    D = A = 40) : h ? (y = A = 40,
                //    D = 36) : (y = A = 24,
                //    D = 20);
                if (k) {
                    y = 48;
                    D = A = 40;
                } else {
                    if (h) {
                        y = A = 40;
                        D = 36;
                    } else {
                        y = A = 24;
                        D = 20;
                    }
                }
                int z = y * Math.max(Integer.parseInt(M.toString()) + 2, D) / D;

                o.getLb().add(new double[]{-z / 2, -y / 2, z, y, A * G, A * x, A, A});
                o.getAu().add(Q == 01 ? this.bizsmall : this.normalsmall);
                o.setLocation(new double[]{-Math.floor(Integer.parseInt(M.toString()) / 2.0), Q, 0, 0});
            }
        }

    }
}
