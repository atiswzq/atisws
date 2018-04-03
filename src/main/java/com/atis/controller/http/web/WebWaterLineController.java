package com.atis.controller.http.web;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.ConstantUtil;
import com.atis.util.OutPutUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;

import java.text.SimpleDateFormat;
import java.util.*;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WebWaterLineController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("")));
    }

    @At(path = "/strobe/webQueryWaterLine", types = {RestRequest.Method.POST})
    public void queryWaterLine() {
        List<AtisWaterLine> atisWaterLineList = AtisWaterLine.where(map("parentId",0)).fetch();
        for(AtisWaterLine atisWaterLine:atisWaterLineList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id",atisWaterLine.id());
            map.put("lineName",atisWaterLine.attr("lineName", String.class));
            map.put("lineDesc",atisWaterLine.attr("lineDesc",String.class));
            map.put("isCurrent",atisWaterLine.attr("isCurrent", Integer.class));
            List<AtisWaterLine> atisWaterLineSubList = AtisWaterLine.where(map("parentId",atisWaterLine.id())).fetch();
            List<Map<String,Object>> subList = new ArrayList<Map<String, Object>>();
            int isCurrent = 0;
            for(AtisWaterLine atisWaterSubLine:atisWaterLineSubList){
                Map<String,Object> subMap = new HashMap<String, Object>();
                subMap.put("id",atisWaterSubLine.id());
                subMap.put("lineName",atisWaterSubLine.attr("lineName", String.class));
                subMap.put("isCurrent",atisWaterSubLine.attr("isCurrent", Integer.class));
                if(atisWaterSubLine.attr("isCurrent", Integer.class)==1){
                    map.put("isCurrent",atisWaterSubLine.attr("isCurrent", Integer.class));
                }
                subMap.put("lineDesc",atisWaterSubLine.attr("lineDesc", String.class));
                subList.add(subMap);
            }
            map.put("subList",subList);
            returnMapList.add(map);
        }
        Map<String,Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("totalWaterLineNum",atisWaterLineList.size());
        parameterMap.put("totalDefinedWaterLineNum",atisWaterLineList.size()-6);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String operaDate = dateFormat.format(new Date()).toString();
        parameterMap.put("totalWaterLineOperas",AtisWaterLineLog.count());
        parameterMap.put("totalMonthOperas",AtisWaterLineLog.where(map("operaMonth",operaDate)).count_fetch());

        parameterMap.put("totalDateOperas",0);
        render(200, OutPutUtil.retunSuccMap(parameterMap,returnMapList,token));
    }

    @At(path = "/strobe/webSaveWaterLine", types = {RestRequest.Method.POST})
    public void saveWaterLine() {
        AtisWaterLine atisWaterLine = AtisWaterLine.create(map("lineName",param("lineName"),
                                                                "strobeGroup",param("strobeGroup"),
                                                                "parentId",0,"createBy",redisClient.get(token),
                                                                "modifyDate",System.currentTimeMillis()/1000,
                                                                "createDate",System.currentTimeMillis()/1000));
        atisWaterLine.save();
        render(200, OutPutUtil.retunSuccMap(null,null,token));
    }

    @At(path = "/strobe/webQueryWaterLineDetails", types = {RestRequest.Method.POST})
    public void queryWaterLineDetails() {
        int id = paramAsInt("waterLineId");
        AtisWaterLine atisWaterLine = AtisWaterLine.find(id);
        String strobeGroup =  atisWaterLine.attr("strobeGroup",String.class);
        String[] strobeGroupArray = strobeGroup.split("@");
        Map returnMap = new HashMap();
        for(int i=0;i<strobeGroupArray.length;i++){
            String[] strobeArray = strobeGroupArray[i].split(",");
            Map<String, Object> totalMap = new HashMap<String, Object>();
            int strobeId = Integer.parseInt(strobeArray[0]);
            AtisStrobe atisStrobe = AtisStrobe.find(strobeId);
            totalMap.put("strobeName", atisStrobe.attr("strobeName", String.class));
            totalMap.put("strobeStatus", atisStrobe.attr("strobeStatus", Integer.class));
            totalMap.put("strobeNumber", atisStrobe.attr("strobeNum", Integer.class));
            totalMap.put("strobeId", atisStrobe.id());
            totalMap.put("strobeDesc", atisStrobe.attr("strobeDesc", String.class));
            totalMap.put("longitude", atisStrobe.attr("longitude", String.class));
            totalMap.put("latitude", atisStrobe.attr("latitude", String.class));
            totalMap.put("needStatus",strobeArray[1] );
            totalMap.put("gatage",atisStrobe.attr("gatage", String.class) );
            if(atisStrobe.attr("strobeNum", Integer.class)==1){
                AtisModbusLog atisModbusLog=(AtisModbusLog)redisClient.getObj("DO,5"+ ConstantUtil.separator+strobeId+ConstantUtil.separator+1);
                totalMap.put("isRemote",atisModbusLog==null?0:atisModbusLog.getData());
            }else{
                AtisModbusLog atisModbusLog= (AtisModbusLog)redisClient.getObj("DO,8"+ ConstantUtil.separator+strobeId+ConstantUtil.separator+1);
                totalMap.put("isRemote",atisModbusLog==null?0:atisModbusLog.getData().toString());
            }



            AtisCamera atisCamera = AtisCamera.find(atisStrobe.attr("cameraId",Integer.class));
            totalMap.put("cameraIp",atisCamera.attr("cameraIp", String.class) );
            totalMap.put("cameraPort",atisCamera.attr("cameraPort", String.class) );
            totalMap.put("cameraPwd",atisCamera.attr("cameraPwd", String.class) );
            totalMap.put("cameraAccount",atisCamera.attr("cameraAccount", String.class) );

            AtisBroadcast atisBroadcast = AtisBroadcast.find(atisStrobe.attr("broadcastId",Integer.class));
            totalMap.put("broadcastIp",atisBroadcast.attr("broadcastIp", String.class) );
            totalMap.put("broadcastPort",atisBroadcast.attr("broadcastPort", String.class) );
            totalMap.put("broadcastPwd",atisBroadcast.attr("broadcastPwd", String.class) );
            totalMap.put("broadcastAccount",atisBroadcast.attr("broadcastAccount", String.class) );
            returnMapList.add(totalMap);
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }

    @At(path = "/strobe/webUpdateCurrentWaterLine", types = {RestRequest.Method.POST})
    public void updateCurrentWaterLine() {
        int userId = 0 ;
        int id = paramAsInt("waterLineId");
        AtisWaterLine atisWaterLine = AtisWaterLine.find(id);
        if(atisWaterLine.attr("isCurrent",Integer.class)==1){
            render(200, OutPutUtil.retunSuccMap(null,null,token));
        }else{
            AtisWaterLine atisWaterLine1= AtisWaterLine.where(map("isCurrent",1)).single_fetch();
            atisWaterLine1.attr("isCurrent",0);
            atisWaterLine1.update();
            atisWaterLine.attr("isCurrent",1);
            atisWaterLine.update();
        }
        render(200, OutPutUtil.retunSuccMap(1,null,token));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        AtisWaterLineLog atisWaterLineLog = AtisWaterLineLog.create(map("waterLineId",id,"operaMonth",dateFormat.format(new Date()).toString(),"createDate",System.currentTimeMillis()/1000,"modifyDate",System.currentTimeMillis()/1000,"userId",userId));
        atisWaterLineLog.save();
    }

}
