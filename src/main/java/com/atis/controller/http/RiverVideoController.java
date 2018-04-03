package com.atis.controller.http;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.OutPutUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class RiverVideoController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("")));
    }

//    @At(path = "/river/queryRiverVideo", types = {RestRequest.Method.POST})
//    public void queryRiverVideo() {
//        List<AtisRiver> atisRiverList = AtisRiver.order("riverName ASC").fetch();
//        for(AtisRiver atisRiver:atisRiverList){
//            Map<String,Object> map = new HashMap<String, Object>();
//            map.put("id",atisRiver.id());
//            map.put("riverName",atisRiver.attr("riverName",String.class));
//            AtisCamera atisCamera = AtisCamera.find(atisRiver.attr("atisCameraId",Integer.class));
//            map.put("cameraIp",atisCamera.attr("cameraIp",String.class));
//            map.put("cameraPort",atisCamera.attr("cameraPort",String.class));
//            map.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
//            map.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
//            returnMapList.add(map);
//        }
//        render(200,OutPutUtil.retunSuccMap(null,returnMapList,token));
//    }
//
//    @At(path = "/river/queryVideoDetail", types = {RestRequest.Method.POST})
//    public void queryVideoDetail() {
//        List<AtisRiver> atisRiverList =  AtisRiver.order("riverName ASC").fetch();
//        for(AtisRiver atisRiver : atisRiverList){
//            Map<String,Object> map = new HashMap<String, Object>();
//            map.put("id",atisRiver.id());
//            map.put("riverName",atisRiver.attr("riverName",String.class));
//            String cameraGroupId = atisRiver.attr("cameraGroup",String.class);
//            String[] cameraGroup = cameraGroupId.split("@");
//            List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
//            for(int i = 0; i<cameraGroup.length;i++){
//                Map<String,Object> subMap = new HashMap<String, Object>();
//                String camera = cameraGroup[i];
//                String id = camera.split(",")[0];
//                AtisCamera atisCamera = AtisCamera.find(Integer.parseInt(id));
//                subMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
//                subMap.put("strobeName",camera.split(",")[1]);
//                subMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
//                subMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
//                subMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
//                mapList.add(subMap);
//            }
//            map.put("cameraList",mapList);
//            returnMapList.add(map);
//        }
//        render(200,OutPutUtil.retunSuccMap(null,returnMapList,token));
//    }
    @At(path = "/river/queryMonitor", types = {RestRequest.Method.POST})
    public void queryMonitor() {
        List<AtisArea> atisAreas = AtisArea.findAll();
        for(AtisArea atisArea:atisAreas){
            Map<String,Object> parametermap = new HashMap<String,Object>();
            parametermap.put("areaId",atisArea.id());
            parametermap.put("areaName",atisArea.attr("areaName",String.class));
            if(atisArea.id()==3){
                List<Map<String,Object>> riverList = new ArrayList<Map<String, Object>>();
                List<AtisRiver> atisRivers = AtisRiver.order("sort ASC").fetch();
                for(AtisRiver atisRiver:atisRivers){
                    Map<String,Object> riverMap = new HashMap<String,Object>();
                    riverMap.put("name",atisRiver.attr("riverName",String.class));
                    String cameraIdGroup = atisRiver.attr("cameraId",String.class);
                    String[] tmpCameraId = cameraIdGroup.split(",");
                    List<Map<String,Object>> caremaList = new ArrayList<Map<String, Object>>();
                    for(int i =0 ;i<tmpCameraId.length;i++){
                        AtisCamera atisCamera = AtisCamera.find(Integer.parseInt(tmpCameraId[i]));
                        Map<String,Object> cameraMap = new HashMap<String,Object>();
                        cameraMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
                        cameraMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
                        cameraMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
                        cameraMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
                        caremaList.add(cameraMap);
                    }
                    riverMap.put("caremaList",caremaList);
                    riverList.add(riverMap);
                }
                parametermap.put("monitorList",riverList);
                returnMapList.add(parametermap);
            }else{
                List<Map<String,Object>> strobeList = new ArrayList<Map<String, Object>>();
                List<AtisStrobe> atisStrobes = AtisStrobe.where(map("isAvailable",1,"areaId",atisArea.id())).order("sort ASC").fetch();
                for(AtisStrobe atisStrobe:atisStrobes){
                    Map<String,Object> strobeMap = new HashMap<String,Object>();
                    strobeMap.put("name",atisStrobe.attr("strobeName",String.class));
                    String cameraIdGroup = atisStrobe.attr("cameraIdGroup",String.class);
                    String[] tmpCameraId = cameraIdGroup.split(",");
                    List<Map<String,Object>> caremaList = new ArrayList<Map<String, Object>>();
                    for(int i =0 ;i<tmpCameraId.length;i++){
                        AtisCamera atisCamera = AtisCamera.find(Integer.parseInt(tmpCameraId[i]));
                        Map<String,Object> cameraMap = new HashMap<String,Object>();
                        cameraMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
                        cameraMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
                        cameraMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
                        cameraMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
                        caremaList.add(cameraMap);
                    }
                    strobeMap.put("caremaList",caremaList);
                    strobeList.add(strobeMap);
                }
                if (atisArea.id()==1){
                    Map<String,Object> strobeMap = new HashMap<String,Object>();
                    strobeMap.put("name","西公园泵");
                    String cameraIdGroup = "92,93";
                    String[] tmpCameraId = cameraIdGroup.split(",");
                    List<Map<String,Object>> caremaList = new ArrayList<Map<String, Object>>();
                    for(int i =0 ;i<tmpCameraId.length;i++){
                        AtisCamera atisCamera = AtisCamera.find(Integer.parseInt(tmpCameraId[i]));
                        Map<String,Object> cameraMap = new HashMap<String,Object>();
                        cameraMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
                        cameraMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
                        cameraMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
                        cameraMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
                        caremaList.add(cameraMap);
                    }
                    strobeMap.put("caremaList",caremaList);
                    strobeList.add(strobeMap);
                }
                parametermap.put("monitorList",strobeList);
                returnMapList.add(parametermap);
            }

        }
        render(200,OutPutUtil.retunSuccMap(null,returnMapList,null));
    }
    @At(path = "/river/queryMonitorByKeyWords", types = {RestRequest.Method.POST})
    public void queryMonitorByKeyWords() {
        System.out.println(param("keyWord"));
        List<Map> list = AtisRiver.nativeSqlClient().defaultMysqlService().query("select id,camera_id,river_name name from atis_river where river_name like '%"+param("keyWord")+"%' union select id,camera_id_group camera_id,strobe_name name from atis_strobe where strobe_name like  '%"+param("keyWord")+"%'");
        for(Map map :list){
            Map<String,Object> returnMap  = new HashMap<String,Object>();
            returnMap.put("name",map.get("name").toString());
            String cameraIdGroup = map.get("camera_id").toString();
            String[] tmpCameraId = cameraIdGroup.split(",");
            List<Map<String,Object>> caremaList = new ArrayList<Map<String, Object>>();
            for(int i =0 ;i<tmpCameraId.length;i++){
                AtisCamera atisCamera = AtisCamera.find(Integer.parseInt(tmpCameraId[i]));
                Map<String,Object> cameraMap = new HashMap<String,Object>();
                cameraMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
                cameraMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
                cameraMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
                cameraMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
                caremaList.add(cameraMap);
            }
            returnMap.put("caremaList",caremaList);
            returnMapList.add(returnMap);
        }
        render(200,OutPutUtil.retunSuccMap(null,returnMapList,null));
    }
}
