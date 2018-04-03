package com.atis.controller.http.web;

import com.atis.controller.BController;
import com.atis.model.AtisCamera;
import com.atis.model.AtisRiver20170218;
import com.atis.model.AtisWarnLevel;
import com.atis.util.OutPutUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WebRiverVideoController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("","")));
    }

//    @At(path = "/river/webQueryRiverVideo", types = {RestRequest.Method.POST})
//    public void queryRiverVideo() {
//        List<AtisWarnLevel> atisWarnLevelList = AtisWarnLevel.findAll();
//        for(AtisWarnLevel atisWarnLevel : atisWarnLevelList){
//            int warnLevelId = atisWarnLevel.id();
//            List<AtisRiver20170218> AtisRiver20170218List = AtisRiver20170218.where(map("warnLevelId",warnLevelId)).order("riverName ASC").fetch();
//            List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
//            Map<String,Object> map = new HashMap<String, Object>();
//            for(AtisRiver20170218 AtisRiver20170218 : AtisRiver20170218List){
//                String cameraGroupId = AtisRiver20170218.attr("cameraGroup",String.class);
//                String[] cameraGroup = cameraGroupId.split("@");
//                for(int i = 0; i<cameraGroup.length;i++){
//                    Map<String,Object> subMap = new HashMap<String, Object>();
//                    String camera = cameraGroup[i];
//                    String id = camera.split(",")[0];
//                    AtisCamera atisCamera = AtisCamera.find(Integer.parseInt(id));
//                    subMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
//                    subMap.put("riverName",AtisRiver20170218.attr("riverName",String.class)+camera.split(",")[1]);
//                    subMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
//                    subMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
//                    subMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
//                    mapList.add(subMap);
//                }
//
//            }
//            map.put("cameraList",mapList);
//            map.put("areaName",atisWarnLevel.attr("areaName",String.class));
//            map.put("totalCameraNum", mapList.size());
//            returnMapList.add(map);
//        }
//        render(200,OutPutUtil.retunSuccMap(null,returnMapList,token));
//    }
}
