package com.services.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.services.GaodeService;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import no.ecc.vectortile.VectorTileEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

@Service
public class GaodeServiceImpl implements GaodeService {
    public void setLAYERNAME_LABEL(String LAYERNAME_LABEL) {
        this.LAYERNAME_LABEL = LAYERNAME_LABEL;
    }
    
    public void parseBuildingRoad(JSONObject region_building_road, VectorTileEncoder vte) {
        JSONArray region = (JSONArray) com.alibaba.fastjson.JSONObject.parse(region_building_road.getString("region"));
        JSONArray road = (JSONArray) com.alibaba.fastjson.JSONObject.parse(region_building_road.getString("road"));
        JSONArray building = (JSONArray) com.alibaba.fastjson.JSONObject.parse(region_building_road.getString("building"));
        parseRegion(region, vte);
        parseRoad(road, vte);
        parseBulid(building, vte);
    }
    
    private void parseBulid(JSONArray building, VectorTileEncoder vte) {
        
        for (int i = 0; i < building.size(); i++) {
            
            java.util.Map<String, Object> attributes = new HashMap<>();
            JSONObject line = building.getJSONObject(i);
            JSONArray roads = line.getJSONArray("buildings");
            Polygon[] lines = new Polygon[roads.size()];
            createPolygon(factory, roads, lines);
            JSONArray style = line.getJSONArray("style");
            attributes.put("strokeStyle", style.getString(2));
            attributes.put("fillStyle", style.getString(0));
            attributes.put("lineWidth", style.getString(1));
            attributes.put("fillStyle1", style.getString(3));
            attributes.put("fillStyle2", style.getString(4));
            attributes.put("fill-extrusion-height", line.getDouble("offset") / 8.0);
            attributes.put("fill-extrusion-base", line.getDouble("offset") / 9.0);
            Geometry geometry = factory.createMultiPolygon(lines);
            attributes.put("labels", line.getString("labels"));
            attributes.put("code", line.get("code"));
            attributes.put("catagroy", line.get("catagroy"));
            vte.addFeature(this.getLayerName("building", line), attributes, geometry);
        }
    }
    
    private void createPolygon(GeometryFactory factory, JSONArray roads, Polygon[] lines) {
        for (int j = 0; j < roads.size(); j++) {
            JSONArray location = roads.getJSONArray(j);
            Coordinate[] points = new Coordinate[location.size() / 2 + 1];
            for (int t = 0; t < location.size(); t += 2) {
                points[t / 2] = new Coordinate(location.getDouble(t) * scale, location.getDouble(t + 1) * scale);
            }
            points[points.length - 1] = points[0];
            lines[j] = factory.createPolygon(points);
        }
    }
    
    private String[] getRgbaColor(String rgba) {
        if (rgba == null || rgba.equals("")) {
            return new String[0];
        }
        return rgba.replace("rgba(", "").replace(")", "").split(",");
    }
    
    private int scale = 16;
    private GeometryFactory factory = new GeometryFactory();
    
    /**
     * 道路解析
     *
     * @param road
     * @param vte
     */
    private void parseRoad(JSONArray road, VectorTileEncoder vte) {
        java.util.Map<String, Object> attributes;
        String layerName;
        for (int i = 0; i < road.size(); i++) {
            
            JSONObject line = road.getJSONObject(i);
            if ("roads:other".equals(line.getString("labels"))) {
                continue;
            }
            JSONArray roads = line.getJSONArray("roads");
            LineString[] lines = new LineString[roads.size()];
            for (int j = 0; j < roads.size(); j++) {
                JSONArray location = roads.getJSONArray(j);
                Coordinate[] points = new Coordinate[location.size() / 2];
                for (int t = 0; t < location.size(); t += 2) {
                    points[t / 2] = new Coordinate(location.getDouble(t) * scale, location.getDouble(t + 1) * scale);
                }
                lines[j] = factory.createLineString(points);
            }
            
            attributes = new HashMap<>();
            
            JSONArray style = line.getJSONArray("style");
            if (style != null && style.size() > 0 && !style.getString(5).equals("")) {
                String[] lineCaps = style.getString(5).split("_");
                if (lineCaps.length != 0) {
                    
                    attributes.put("fillStyle", line.getString("fillStyle"));
                    attributes.put("width", style.getDouble(3) + style.getDouble(0));
                    
                    String lineCap = "butt";
                    if (lineCaps.length >= 2) {
                        lineCap = lineCaps[1].replace("cap", "");
                    }
                    attributes.put("line-cap", lineCap);
                    double dash = 0;
                    if (!lineCaps[0].equals("solid")) {
                        int dashLength = parseInt(lineCaps[0].split(",")[1].replace(")", ""));
                        dash = dashLength;
                    }
                    
                    attributes.put("line-dasharray", dash);
                    attributes.put("labels", line.getString("labels"));
                    attributes.put("line-join", "round");
                    attributes.put("type", line.getString("type"));
                    attributes.put("code", line.get("code"));
                    attributes.put("catagroy", line.get("catagroy"));
                    Geometry geometry = factory.createMultiLineString(lines);
                    layerName = getDashRoadLayerName(line, dash);
                    vte.addFeature(layerName, attributes, geometry);
                }
            }
            
            attributes = new HashMap<>();
            attributes.put("fillStyle", line.getString("strokeStyle"));
            attributes.put("width", style.getDouble(0));
            
            String[] lineStyle = style.getString(2).split("_");
            String lineCap;
            if (lineStyle.length >= 2) {
                lineCap = lineStyle[1].replace("cap", "");
            } else {
                lineCap = "square";
            }
            attributes.put("line-cap", lineCap);
            double dash;
            if (!lineStyle[0].equals("solid")) {
                dash = parseDouble(lineStyle[0].split(",")[1].replace(")", ""));
            } else {
                dash = 0;
            }
            layerName = getDashRoadLayerName(line, dash);
            attributes.put("line-dasharray", new double[]{dash, dash});
            
            Geometry geometry = factory.createMultiLineString(lines);
            attributes.put("labels", line.getString("labels"));
            attributes.put("type", line.getString("type"));
            attributes.put("code", line.get("code"));
            attributes.put("catagroy", line.get("catagroy"));
            vte.addFeature(layerName, attributes, geometry);
            
        }
    }
    
    private String getDashRoadLayerName(JSONObject line, double dash) {
        String layerName;
        if (dash != 0) {
            if (dash != 6 && dash != 12)
                System.out.println(dash);
            layerName = String.join("_", new String[]{"road", "dash", Integer.toString((int) dash)});
        } else {
            layerName = this.getLayerName("road", line);
        }
        return layerName;
    }
    
    private void parseRegion(JSONArray region, VectorTileEncoder vte) {
        for (int i = 0; i < region.size(); i++) {
            
            java.util.Map<String, Object> attributes = new HashMap<>();
            JSONObject line = region.getJSONObject(i);
            JSONArray roads = line.getJSONArray("regions");
            Polygon[] lines = new Polygon[roads.size()];
            createPolygon(factory, roads, lines);
            attributes.put("style", line.getJSONArray("style").getString(0));
            Geometry geometry = factory.createMultiPolygon(lines);
            attributes.put("labels", line.getString("labels"));
            attributes.put("code", line.get("code"));
            attributes.put("catagroy", line.get("catagroy"));
            vte.addFeature(this.getLayerName("region", line), attributes, geometry);
        }
    }
    
    private void addPoiExtent(String bgColor, String text, String font, int fontSize, double x, double y, VectorTileEncoder vte) {
        if (bgColor != null && !bgColor.equals("")) {
            x = x * scale;
            y = y * scale;
            //  System.out.println(text + bgColor);
            Font f = new Font(font, Font.PLAIN, fontSize);
            FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(f);
            double width = fm.stringWidth(text) * scale * 2;
            double height = fm.getHeight() * scale * 2;
            
            Geometry geometry = this.factory.createPolygon(new Coordinate[]{
                    new Coordinate(x, y),
                    new Coordinate(x, y + height),
                    new Coordinate(x + width, y + height),
                    new Coordinate(x + width, y),
                    new Coordinate(x, y)
            });
            Map<String, Object> attributes = new HashMap();
            attributes.put("bgColor", bgColor);
            vte.addFeature("poi_gb", attributes, geometry);
        }
    }
    
    private String fontFamily = "SimHei";
    
    private void parsePoi(JSONArray poilabel, VectorTileEncoder vte) {
        java.util.Map<String, Object> attributes = null;
        
        for (Object aPoilabel : poilabel) {
            JSONObject poi = (JSONObject) aPoilabel;
            JSONArray names = poi.getJSONArray("name");
            Double baseX = poi.getDouble("baseX");
            Double baseY = poi.getDouble("baseY");
            String fillStyle = poi.getString("fillStyle");
            String strokeStyle = poi.getString("strokeStyle");
            String bgColor = poi.getString("bgColor");
            String[] nameList = new String[names.size()];
            Geometry geom;
            double x, y;
            for (int j = 0; j < names.size(); j++) {
                attributes = new HashMap<>();
                attributes.put("name", names.getString(j));
                nameList[j] = names.getString(j);
                
                JSONArray location = poi.getJSONArray("location");
                if (location != null) {
                    JSONArray index = location.getJSONArray(j);
                    x = baseX * scale + index.getDouble(0);
                    y = baseY * scale + index.getDouble(1);
                    attributes.put("text_font", this.fontFamily);
                    attributes.put("text_size", index.getDoubleValue(3) - 2);
                    attributes.put("text_color", (bgColor == null || bgColor.equals("")) ? fillStyle : bgColor);
                    attributes.put("text_halo_color", strokeStyle);
                    geom = factory.createPoint(new Coordinate(x * 1, y * 1));
                    if (j == names.size() - 1) {
                        this.addPoiImage(poi, attributes);
                    }
                    this.addPoiExtent(bgColor, names.getString(j), fontFamily, index.getInteger(3), x, y, vte);
                    attributes.put("labels", poi.getString("labels"));
                    attributes.put("poiType", poi.getString("poiType"));
                    attributes.put("code", poi.getString("code"));
                    attributes.put("catagroy", poi.getString("catagroy"));
                    vte.addFeature(getLayerName("poi", poi), attributes, geom);
                } else if (j == names.size() - 1) {
                    this.parsePoiImg(poi, vte, baseX, baseY, getLayerName("poi", poi));
                }
            }
        }
    }
    
    /**
     * @param poi
     * @param attributes
     */
    private void addPoiImage(JSONObject poi, Map<String, Object> attributes) {
        JSONArray lbs = poi.getJSONArray("lb");
        if (lbs != null && lbs.size() > 0) {
            JSONArray lb = lbs.getJSONArray(0);
            attributes.put("icon-offset", new double[]{lb.getDouble(0), lb.getDouble(0)});
            String image = String.join("_", new String[]{lb.getString(4), lb.getString(5)});
            attributes.put("icon-image", image);
        }
    }
    
    private String LAYERNAME_LABEL = "default";
    
//    private String getLayerName(String defaultName, JSONObject object) {
//        if (LAYERNAME_LABEL) {
//            return defaultName;
//        }
//        return object.getString("labels");
//    }
    
    private String getLayerName(String defaultName, JSONObject object) {
        if (LAYERNAME_LABEL.equalsIgnoreCase("default") ||
                LAYERNAME_LABEL.equalsIgnoreCase("true")) {
            return defaultName;
        }
        String labels = object.getString("labels");
        if (LAYERNAME_LABEL.equalsIgnoreCase("false")){
            return labels;
        }
        
        switch (defaultName){
            case "region":
                if(labels.equalsIgnoreCase("water")){
                    return "water";
                }
                return  "region";
            case "road_label":
                if(labels.equalsIgnoreCase("water")){
                    return "water_label";
                }
                return  defaultName;
            case "poi":
                if(labels.equalsIgnoreCase("labels:pois")){
                    return defaultName;
                }
                return "labels";
            case "road":
                return defaultName;
            case "building":
                return "building";
        }
        return labels;
    }
    
    private void parsePoiImg(JSONObject poi, VectorTileEncoder vte, double baseX, double baseY, String layerName) {
        JSONArray lbs = poi.getJSONArray("lb");
        if (lbs != null && lbs.size() > 0) {
            double x, y;
            java.util.Map<String, Object> attributes;
            for (int l = 0; l < lbs.size(); l++) {
                attributes = new HashMap<>();
                JSONArray lb = lbs.getJSONArray(l);
                x = baseX * scale + lb.getDouble(0);
                y = baseY * scale + lb.getDouble(1);
                attributes.put("icon-image", String.join("_", new String[]{lb.getString(4), lb.getString(5)}));
                Geometry geom = factory.createPoint(new Coordinate(x, y));
                vte.addFeature(layerName, attributes, geom);
            }
        }
    }
    
    public void parseLimg(JSONObject obj, VectorTileEncoder vte) {
        JSONArray poilabel = (JSONArray) com.alibaba.fastjson.JSONObject.parse(obj.getString("poilabel"));
        JSONArray roadlabel = (JSONArray) com.alibaba.fastjson.JSONObject.parse(obj.getString("roadlabel"));
        
        parsePoi(poilabel, vte);
        parseRoadLabel(roadlabel, vte);
        
    }
    
    private void parseRoadLabel(JSONArray roadlabel, VectorTileEncoder vte) {
        for (Object aRoadlabel : roadlabel) {
            JSONObject poi = (JSONObject) aRoadlabel;
            JSONArray names = poi.getJSONArray("name");
            Double baseX = poi.getDouble("baseX");
            Double baseY = poi.getDouble("baseY");
            String strokeStyle = poi.getString("strokeStyle");
            String fillStyle = poi.getString("fillStyle");
            double x, y;
            for (int j = 0; j < names.size(); j++) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("name", names.getString(j));
                Object location = poi.get("location");
                Geometry geom = null;
                if (location instanceof JSONArray) {
                    JSONArray index = ((JSONArray) location);
                    x = baseX * scale + index.getDouble(0);
                    y = baseY * scale + index.getDouble(1);
                    geom = factory.createPoint(new Coordinate(x, y));
                    
                } else {
                    x = baseX * scale;// + poi.getDouble("location");
                    y = baseY * scale + 5;
                    geom = factory.createPoint(new Coordinate(x, y));
                }
                
                if (j == names.size() - 1) {
                    this.addPoiImage(poi, attributes);
                }
                attributes.put("text_size", poi.getString("fontSize"));
                attributes.put("labels", poi.getString("labels"));
                attributes.put("text_halo_color", strokeStyle);
                attributes.put("text_color", fillStyle);
                attributes.put("type", poi.getString("type"));
                attributes.put("poiType", poi.getString("poiType"));
                
                
                vte.addFeature(this.getLayerName("road_label",poi), attributes, geom);
            }
        }
    }
}
