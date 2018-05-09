package com.bean;

import java.util.ArrayList;

public class GaoDeImg {
    private String key;
    private XYZ Xyz;
    private ArrayList<GaoDeLabel> poilabel;
    private ArrayList<GaoDeLabel> roadlabel;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public XYZ getXyz() {
        return Xyz;
    }

    public void setXyz(XYZ xyz) {
        Xyz = xyz;
    }

    public ArrayList<GaoDeLabel> getPoilabel() {
        return poilabel;
    }

    public void setPoilabel(ArrayList<GaoDeLabel> poilabel) {
        this.poilabel = poilabel;
    }

    public ArrayList<GaoDeLabel> getRoadlabel() {
        return roadlabel;
    }

    public void setRoadlabel(ArrayList<GaoDeLabel> roadlabel) {
        this.roadlabel = roadlabel;
    }
}
