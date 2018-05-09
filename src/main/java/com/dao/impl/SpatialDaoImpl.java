package com.dao.impl;

import java.util.List;
import java.util.Map;

import no.ecc.vectortile.VectorTileEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dao.SpatialDao;
import com.mercator.TileUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@Repository
public class SpatialDaoImpl implements SpatialDao {
    
    @Autowired
    protected JdbcTemplate jdbc;
    
    private String tableNmae = "beijing_road";
    private String poi_tableName = "beijing_poi";
    
    @Override
    public byte[] getContents(String type, int x, int y, int z) {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(" SELECT	NAME,	geom,	CASE WHEN RANK > 0.7 THEN	'rgb(139,0,0)'	WHEN RANK > 0.5 THEN	'rgb(255,0,0)'	WHEN RANK > 0.3 THEN \r\n");
        stringBuilder.append(" 'rgb(255,215,0)' ELSE	'rgb(50,205,50)' END AS RANK FROM 	(		SELECT			NAME,			st_astext (geom) AS geom, \r\n");
        stringBuilder.append(" random() AS RANK		FROM			beijing_roadnet		where ");
        stringBuilder.append(" TYPE IN ('次要道路（城市次干道）','城市环路/城市快速路','高速公路','主要道路（城市主干道）')");
        stringBuilder.append(" AND  st_intersects(st_setsrid(geom,4326),st_geomfromtext(?,4326))");
        stringBuilder.append(" ) T");
        String sql = stringBuilder.toString();
               // "SELECT * FROM beijing_road where parent_code = 131 AND st_intersects(st_setsrid(geom,4326),st_geomfromtext(?,4326))";
        return this.getVectorTile(sql, type, x, y, z);
    }
    
    @Override
    public byte[] getPoints(String table, int x, int y, int z) {
        String sql = "select name,st_astext(geom) as geom from  " + table
                + " where " + "st_intersects(st_setsrid(geom,4326),st_geomfromtext(?,4326)) "
                + "";
        return this.getVectorTile(sql, table, x, y, z);
    }
    
    private byte[] getVectorTile(String sql, String layerName, int x, int y, int z) {
        try {
            
            String tile = TileUtils.parseXyz2Bound(x, y, z);
            List<Map<String, Object>> results = jdbc.queryForList(sql, tile);
            VectorTileEncoder vte = new VectorTileEncoder(4096, 16, false);
            for (Map<String, Object> m : results) {
                String wkt = (String) m.get("geom");
                Geometry geom = new WKTReader().read(wkt);
                TileUtils.convert2Piexl(x, y, z, geom);
                m.remove("geom");
                vte.addFeature(layerName, m, geom);
            }
            return vte.encode();
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public byte[] getAllCityRegion() {
        return null;
    }
}
