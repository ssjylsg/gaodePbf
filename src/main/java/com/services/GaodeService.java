package com.services;

import com.alibaba.fastjson.JSONObject;
import no.ecc.vectortile.VectorTileEncoder;

public interface GaodeService {
    void parseLimg(JSONObject obj, VectorTileEncoder vte);
    void parseBuildingRoad(JSONObject region_building_road, VectorTileEncoder vte);
    void setLAYERNAME_LABEL(boolean LAYERNAME_LABEL);
}
