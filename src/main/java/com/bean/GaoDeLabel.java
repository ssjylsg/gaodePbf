package com.bean;

import java.util.ArrayList;

public class GaoDeLabel {
    private Object name;
    private Object location;
    private Object baseX;
    private Object baseY;
    
    public Object getCatagroy() {
        return catagroy;
    }
    
    public void setCatagroy(Object catagroy) {
        this.catagroy = catagroy;
    }
    
    private Object catagroy;
    private static String[] poicatagroies;
    static {
        poicatagroies = "住宿,餐饮,购物,风景名胜,交通设施,金融保险,科教文化,生活服务,医疗保健,休闲体育,公共设施,商务住宅,政府机构及社会团体,摩托车服务,汽车服务,通行设施,地铁站,道路附属设施,地名,其他".split(",");
    
    }
    public Object getPoiType() {
        return poiType;
    }
    
    public void setPoiType(Object poiType) {
        this.poiType = poiType;
    }
    
    private Object poiType;

    private Object type;


    private ArrayList<String> Au;


    private ArrayList<Object> lb;


    private Object fontSize;


    private Object labels;
    
    public Object getCode() {
        return code;
    }
    
    public void setCode(Object code) {
        this.code = code;
        
        if(code != null ){
            this.setCatagroy(poicatagroies[Integer.parseInt(code.toString())]);
        }
    }
    
    private Object code;


    private Object fillStyle;


    public final void setfillStyle(Object value) {
        setFillStyle(value);
    }

    private Object strokeStyle;


    private Object bgColor;


    public GaoDeLabel() {
        setAu(new ArrayList<String>());
        this.setLb(new ArrayList<Object>());
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Object getBaseX() {
        return baseX;
    }

    public void setBaseX(Object baseX) {
        this.baseX = baseX;
    }

    public Object getBaseY() {
        return baseY;
    }

    public void setBaseY(Object baseY) {
        this.baseY = baseY;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public ArrayList<String> getAu() {
        return Au;
    }

    public void setAu(ArrayList<String> au) {
        Au = au;
    }

    public ArrayList<Object> getLb() {
        return lb;
    }

    public void setLb(ArrayList<Object> lb) {
        this.lb = lb;
    }

    public Object getFontSize() {
        return fontSize;
    }

    public void setFontSize(Object fontSize) {
        this.fontSize = fontSize;
    }

    public Object getLabels() {
        return labels;
    }

    public void setLabels(Object labels) {
        this.labels = labels;
    }

    public Object getFillStyle() {
        return fillStyle;
    }

    public void setFillStyle(Object fillStyle) {
        this.fillStyle = fillStyle;
    }

    public Object getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(Object strokeStyle) {
        this.strokeStyle = strokeStyle;
    }

    public Object getBgColor() {
        return bgColor;
    }

    public void setBgColor(Object bgColor) {
        this.bgColor = bgColor;
    }
}
