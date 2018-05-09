package com.services;

import com.alibaba.fastjson.JSONObject;

public interface GaodeDataResolver {
    JSONObject covertGaodeToNPGIS(String result, int zoom, int row, int col);
}
