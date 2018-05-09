package com.services.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bean.*;
import com.services.GaodeDataResolver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;

@Service
public class GaodeDataResolverImpl implements GaodeDataResolver {
    private double[] resolutions = new double[]{156543.0339, 78271.516953125, 39135.7584765625, 19567.87923828125, 9783.939619140625, 4891.9698095703125, 2445.9849047851562, 1222.9924523925781, 611.4962261962891, 305.74811309814453, 152.87405654907226, 76.43702827453613, 38.218514137268066, 19.109257068634033, 9.554628534317016, 4.777314267158508, 2.388657133579254, 1.194328566789627, 0.5971642833948135};

    private GaoDeMapUtil util = null;

    public GaodeDataResolverImpl() {
        util = new GaoDeMapUtil(this.resolutions);
    }

    public final String ToJson(Object obj) {
        return com.alibaba.fastjson.JSON.toJSONString(obj);
    }

    public JSONObject covertGaodeToNPGIS(String result, int zoom, int row, int col) {
        GaoDeRoad gaodeRoad = null;
        GaoDeImg gaodeImg = null;
        XYZ xyz = new XYZ();
        xyz.setZ(zoom);
        xyz.setX(row);
        xyz.setY(col);

        Pix lp = util.getPixPosition(0, 0, zoom, col, row);
        gaodeRoad = new GaoDeRoad();

        gaodeRoad.setKey(zoom + "_" + row + "_" + col + "_region,building,road");
        gaodeRoad.setXyz(xyz);
        gaodeImg = new GaoDeImg();

        gaodeImg.setKey(zoom + "_" + row + "_" + col);
        gaodeImg.setXyz(xyz);
        if (!DotNetToJavaStringHelper.isNullOrEmpty(result)) {

            String[] c = result.split("\\|");

            for (String r : c) {

                if (r == null || r.equals("")) {
                    continue;
                }

                JSONArray g = null;
                try {
                    g = com.alibaba.fastjson.JSON.parseArray(r);
                } catch (Exception e) {

                }

                if (g != null && g.size() > 0) {

                    String[] l = g.get(0).toString().split("-");
                    String m = l[3];

                    ArrayList<JSONObject> data = new ArrayList<JSONObject>();
                    ArrayList<GaoDeLabel> labelData = new ArrayList<GaoDeLabel>();
                    GaoDeMapUtil h = util;
                    for (int j = g.size() - 1; j >= 1; j--) {
                        JSONArray p = (JSONArray) ((g.get(j) instanceof JSONArray) ? g.get(j) : null);
                        switch (m) {
                            case "road":
                                h.roadLine(data, p, m, j - 1);
                                break;
                            case "region":
                                h.region(data, p, m);
                                break;
                            case "building":
                                h.building(data, p, m);
                                break;
                            case "poilabel":
                            case "roadlabel":
                                String[] s = null;
                                String fillStyle = null;
                                String strokeStyle = null;

                                String icon = null;
                                if (p.get(1) != null) {
                                    s = p.get(1).toString().split("[&]", -1);
                                    fillStyle = h.getRgb(s[2]);
                                    strokeStyle = h.getRgb(s[3]);
                                    icon = s[0];
                                } else {
                                    continue;
                                }


                                JSONArray pk = (JSONArray) ((p.get(0) instanceof JSONArray) ? p.get(0) : null);

                                for (int k = 0; k < pk.size(); k++) {
                                    JSONArray w = (JSONArray) ((pk.get(k) instanceof JSONArray) ? pk.get(k) : null);
                                    GaoDeLabel O = h.poiFill(w, l);
                                    O.setFontSize(s[1]);
                                    O.setPoiType(p.get(m.equals("roadlabel") ? 4 : 6));
                                    O.setLabels(p.get(m.equals("roadlabel") ? 3 : 4));

                                    O.setfillStyle(fillStyle);
                                    O.setStrokeStyle(strokeStyle);
                                    h.poiLabelIcon(w, O, icon, s);
                                    if (m.equals("poilabel") && !s[4].equals("")) {
                                        O.setBgColor(h.getRgb(s[4]));
                                    }
                                    labelData.add(O);
                                }
                                break;
                        }
                    }

                    if (m.equals("road") || m.equals("region") || m.equals("building")) {
                        data.sort(new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject o1, JSONObject o2) {
                                return o1.getDouble("rank") - o2.getDouble("rank") > 0 ? 1 : 0;
                                //return 1;//x.rank - y.rank;
                            }
                        });
                        switch (m) {
                            case "road":
                                gaodeRoad.setRoad(data);
                                break;
                            case "region":
                                gaodeRoad.setRegion(data);
                                break;
                            case "building":
                                gaodeRoad.setBuilding(data);
                                break;
                        }
                    } else if (m.equals("poilabel") || m.equals("roadlabel")) {
                        if (m.equals("poilabel")) {
                            gaodeImg.setPoilabel(labelData);
                        } else {
                            gaodeImg.setRoadlabel(labelData);
                        }
                    }
                }
            }
        }

        JSONObject obj = new JSONObject();
        JSONObject limg = new JSONObject();
        JSONObject region_building_road = new JSONObject();

        limg.put("poilabel", this.ToJson(gaodeImg.getPoilabel()));
        limg.put("roadlabel", this.ToJson(gaodeImg.getRoadlabel()));


        region_building_road.put("building", this.ToJson(gaodeRoad.getBuilding()));
        region_building_road.put("region", this.ToJson(gaodeRoad.getRegion()));
        region_building_road.put("road", this.ToJson(gaodeRoad.getRoad()));

        obj.put("limg", limg);
        obj.put("region_building_road", region_building_road);

        return obj;
    }
}


