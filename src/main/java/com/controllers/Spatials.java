package com.controllers;


import com.alibaba.fastjson.JSONObject;
import com.dao.SpatialDao;
import com.services.GaodeDataResolver;
import com.services.GaodeService;
import com.util.GaodeMapUtil;
import no.ecc.vectortile.VectorTileEncoder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class Spatials {
    
    
    @Autowired
    private SpatialDao dao;
    
    @Autowired
    private GaodeService gaodeService;
    
    @CrossOrigin(origins = "*", maxAge = 3600, methods = {RequestMethod.GET})
    @RequestMapping(value = "/{type}/{z}/{x}/{y}", produces = "application/x-protobuf")
    public byte[] spatial(@PathVariable String type, @PathVariable int x, @PathVariable int y, @PathVariable int z, HttpServletResponse httpServletResponse) {
        
        byte[] data = dao.getPoints(type, x, y, z);
        return data;
    }
    @CrossOrigin(origins = "*", maxAge = 3600, methods = {RequestMethod.GET})
    @RequestMapping(value = "/roadnet/{z}/{x}/{y}",  produces = "application/x-protobuf")
    public byte[] getRoadnet(@PathVariable int x, @PathVariable int y, @PathVariable int z, HttpServletResponse httpServletResponse){
        return this.dao.getContents("roadnet",x,y,z);
    }
    
    private String gaodeMapTile = "C:\\Users\\Administrator\\Desktop\\gaode\\s";
    
    
    @CrossOrigin(origins = "*", maxAge = 3600, methods = {RequestMethod.GET})
    @RequestMapping(value = "/gaodeMap/{label}/{z}/{x}/{y}", produces = "application/x-protobuf")
    public byte[] gaodeMap(@PathVariable int x, @PathVariable int y, @PathVariable int z, @PathVariable String label) throws IOException {
        String file = gaodeMapTile + File.separator + z + File.separator + x + File.separator + y + ".json";
        if (!new File(file).exists()) {
            return new byte[0];
        }
        FileInputStream fis = new FileInputStream(file);
        int len = 0;
        gaodeService.setLAYERNAME_LABEL(label);
        byte[] data = new byte[(int) new File(file).length()];
        fis.read(data);
        JSONObject json = (JSONObject) com.alibaba.fastjson.JSON.parse(new String(data));
        VectorTileEncoder vte = new VectorTileEncoder(4096, 16, false);
        gaodeService.parseLimg(json.getJSONObject("limg"), vte);
        gaodeService.parseBuildingRoad(json.getJSONObject("region_building_road"), vte);
        return vte.encode();
    }
    
    private String gaodeOnlineUrl = "https://vdata.amap.com/tiles?mapType=normal&v=2&style=5&rd=1&flds=region,building,road,poilabel,roadlabel&t={0}&lv=13&preload=1";
    
    
    @Autowired
    private GaodeDataResolver resolver;
    
    @CrossOrigin(origins = "*", maxAge = 3600, methods = {RequestMethod.GET})
    @RequestMapping(value = "/gaodeproxy/{label}/{z}/{x}/{y}", produces = "application/x-protobuf")
    public byte[] gaodeProxy(@PathVariable int x, @PathVariable int y, @PathVariable int z, @PathVariable String label) throws ExecutionException, InterruptedException {
        byte[] buffer = readCache(z, x, y,label);
        if (buffer != null) {
            return buffer;
        }
        if(z > 18){
            return  null;
        }
        gaodeService.setLAYERNAME_LABEL(label);
        String url = java.text.MessageFormat.format(gaodeOnlineUrl, GaodeMapUtil.formatXYZ(z, y, x));
        ExecutorService executor = Executors.newCachedThreadPool();
        
        Future<byte[]> future = executor.submit(new Callable<byte[]>() {
            public byte[] call() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = null;
                String msg = "";
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        msg = new String(response.body().bytes(), "UTF-8");
                        JSONObject json = resolver.covertGaodeToNPGIS(msg, z, x, y);
                        cacheJsonTile(json.toJSONString(),z,x,y,label);
                        VectorTileEncoder vte = new VectorTileEncoder(4096, 16, false);
                        gaodeService.parseLimg(json.getJSONObject("limg"), vte);
                        gaodeService.parseBuildingRoad(json.getJSONObject("region_building_road"), vte);
                        byte[] buffer = vte.encode();
                        cacheVectorTile(buffer, z, x, y,label);
                        return buffer;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(url);
                    
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
                return null;
            }
        });
        
        return future.get();
    }
    
    private String cacheFile = "C:\\Users\\Administrator\\Desktop\\gaode\\buffer";
    private String jsonCacheFile = "C:\\Users\\Administrator\\Desktop\\gaode\\s";
    
    private byte[] readCache(int z, int x, int y,String label) {
        //System.out.println(Spatials.class.getClass().getResource("/").getPath() + System.getProperty("user.dir"));
        String file = String.join(File.separator,new String[]{
                cacheFile,
                label,
                Integer.toString(z),
                Integer.toString(x),
                Integer.toString(y)
        })+".pbf";
        
        File fileInfo = new File(file);
        if (fileInfo.exists()) {
            byte[] buffer = null;
            try {
                buffer = new byte[(int) fileInfo.length()];
                DataInputStream stream = new DataInputStream(new FileInputStream(file));
                stream.readFully(buffer);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer;
        }
        return null;
    }
    
    private void cacheVectorTile(byte[] buffer, int z, int x, int y,String label) {
        String file = String.join(File.separator,new String[]{
                cacheFile,
                label,
                Integer.toString(z),
                Integer.toString(x),
                Integer.toString(y)
        })+".pbf";
        File fileInfo = new File(file);
        if (fileInfo.exists()) {
            return;
        } else {
            try {
                String temp = String.join(File.separator,new String[]{
                        cacheFile,
                        label,
                        Integer.toString(z),
                        Integer.toString(x)
                });
                File tempFileInfo = new File(temp);
                if (!tempFileInfo.exists()) {
                    tempFileInfo.mkdirs();
                }
                fileInfo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        DataOutputStream fw = null;
        try {
            fw = new DataOutputStream(new FileOutputStream(file));
            fw.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    private void cacheJsonTile(String json,int z, int x, int y,String label){
        String file = String.join(File.separator,new String[]{
                jsonCacheFile,
                label,
                Integer.toString(z),
                Integer.toString(x),
                Integer.toString(y)
        })+".json";
        File fileInfo = new File(file);
        if (fileInfo.exists()) {
            return;
        } else {
            try {
                String temp = String.join(File.separator,new String[]{
                        jsonCacheFile,
                        label,
                        Integer.toString(z),
                        Integer.toString(x)
                });
                File tempFileInfo = new File(temp);
                if (!tempFileInfo.exists()) {
                    tempFileInfo.mkdirs();
                }
                fileInfo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        DataOutputStream fw = null;
        try {
            fw = new DataOutputStream(new FileOutputStream(file));
            fw.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
