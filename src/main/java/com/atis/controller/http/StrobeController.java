package com.atis.controller.http;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.*;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.cache.RedisClient;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.collections.map.HashedMap;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atis.util.RainUtil.queryRain;
import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class StrobeController extends BController {
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("updateWarnLevel","queryWarnLevel","queryStrobeWaterLevel","queryStrobe","strobeDetails")));
    }

    @At(path = "/strobe/queryWarnLevel", types = {RestRequest.Method.POST})
    public void queryWarnLevel() {
        List<AtisWarnLevel> atisWarnLevelList = AtisWarnLevel.findAll();
        render(200,OutPutUtil.retunSuccMap(null,atisWarnLevelList,token));
    }

    @At(path = "/strobe/updateWarnLevel", types = {RestRequest.Method.POST})
    public void updateWarnLevel() {
        int id = paramAsInt("warnLevelId");
        float highLevel = paramAsFloat("highLevel",0);
        float lowLevel = paramAsFloat("lowLevel",0);
        AtisWarnLevel atisWarnLevel = AtisWarnLevel.find(id);
        atisWarnLevel.attr("highLevel",highLevel);
        atisWarnLevel.attr("lowLevel",lowLevel);
        atisWarnLevel.attr("modifyBy",redisClient.get(token));
        atisWarnLevel.attr("modifyDate",System.currentTimeMillis()/1000);
        atisWarnLevel.update();
        render(200,OutPutUtil.retunSuccMap(null,null,token));
    }

    @At(path = "/strobe/queryStrobeWaterLevel", types = {RestRequest.Method.POST})
    public void queryStrobeWaterLevel(){
        int warnLevelId = paramAsInt("warnLevelId");
        AtisStrobe highStrobe = AtisStrobe.where(map("warnLevelId",warnLevelId,"isAvailable",1)).order("beforeLevel DESC").limit(1).single_fetch();
        AtisStrobe lowStrobe = AtisStrobe.where(map("warnLevelId",warnLevelId,"isAvailable",1)).order("afterLevel ASC").limit(1).single_fetch();
        Map<String,Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("currentHighestBeforeLevel",highStrobe.attr("beforeLevel",Float.class).toString());
        parameterMap.put("currentHighestAfterLevel",highStrobe.attr("afterLevel",Float.class).toString());
        parameterMap.put("currentHighestName",highStrobe.attr("strobeName",String.class).charAt(0)==highStrobe.attr("location",String.class).charAt(0)?
                highStrobe.attr("strobeName",String.class):highStrobe.attr("strobeName",String.class)+"("+highStrobe.attr("location",String.class)+")");

        parameterMap.put("currentLowestBeforeLevel",lowStrobe.attr("beforeLevel",Float.class).toString());
        parameterMap.put("currentLowestAfterLevel",lowStrobe.attr("afterLevel",Float.class).toString());
        parameterMap.put("currentLowestLevel",lowStrobe.attr("strobeName",String.class).charAt(0)==lowStrobe.attr("location",String.class).charAt(0)?
                lowStrobe.attr("strobeName",String.class):lowStrobe.attr("strobeName",String.class)+"("+lowStrobe.attr("location",String.class)+")");


        AtisGateWaterCurrent atisGateWaterCurrent = (AtisGateWaterCurrent)AtisGateWaterCurrent.findAll().get(0);
        AtisGateWaterCurrentXs atisGateWaterCurrentXs = (AtisGateWaterCurrentXs)AtisGateWaterCurrentXs.findAll().get(0);
        DecimalFormat df   = new DecimalFormat("######0.00");
        parameterMap.put("neiheWater",df.format(atisGateWaterCurrent.attr("neiheWater",Float.class)));
        parameterMap.put("qiantangWater",df.format(atisGateWaterCurrent.attr("qiantangWater",Float.class)));
        parameterMap.put("jinshuiWater",df.format(atisGateWaterCurrent.attr("jinshuiWater",Float.class)));
        parameterMap.put("chushuiWater",df.format(atisGateWaterCurrent.attr("chushuiWater",Float.class)));



        parameterMap.put("waijianglWater",df.format(atisGateWaterCurrentXs.attr("waijianglWater",Float.class)));
        parameterMap.put("waijiangrWater",df.format(atisGateWaterCurrentXs.attr("waijiangrWater",Float.class)));
        parameterMap.put("xsneiheWater",df.format(atisGateWaterCurrentXs.attr("neiheWater",Float.class)));

        AtisWarnLevel atisWarnLevel = AtisWarnLevel.find(warnLevelId);
        String highLevelWarn = atisWarnLevel.attr("highLevel",Float.class).toString();
        String lowLevelWarn = atisWarnLevel.attr("lowLevel",Float.class).toString();
        List<AtisStrobe> atisStrobeList = new ArrayList<AtisStrobe>();
        atisStrobeList = AtisStrobe.where(map("warnLevelId",warnLevelId,"isAvailable",1)).order("sort ASC").fetch();
        for(AtisStrobe atisStrobe:atisStrobeList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("beforeLevel",atisStrobe.attr("beforeLevel",Float.class).toString());
            map.put("afterLevel",atisStrobe.attr("afterLevel",Float.class).toString());
            map.put("strobeName",atisStrobe.attr("strobeName",String.class).charAt(0)==atisStrobe.attr("location",String.class).charAt(0)?
                    atisStrobe.attr("strobeName",String.class):atisStrobe.attr("strobeName",String.class)+"("+atisStrobe.attr("location",String.class)+")");
            if(atisStrobe.attr("beforeLevel",Float.class)>=atisWarnLevel.attr("highLevel",Float.class)){
                map.put("warnType", ConstantUtil.highLevelWarnType);
            }else if(atisStrobe.attr("afterLevel",Float.class)<=atisWarnLevel.attr("lowLevel",Float.class)){
                map.put("warnType", ConstantUtil.lowLevelWarnType);
            }else{
                map.put("warnType", ConstantUtil.nromalWarnType);
            }
            returnMapList.add(map);
        }
        //850排涝闸水位
        AtisStrobe atisStrobe = AtisStrobe.find(26);
        Map<String ,Object> plStrobeMap = new HashMap<String,Object>();
        plStrobeMap.put("beforeLevel",atisStrobe.attr("beforeLevel",Float.class).toString());
        plStrobeMap.put("afterLevel",atisStrobe.attr("afterLevel",Float.class).toString());
        plStrobeMap.put("strobeName",atisStrobe.attr("strobeName",String.class).charAt(0)==atisStrobe.attr("location",String.class).charAt(0)?
                atisStrobe.attr("strobeName",String.class):atisStrobe.attr("strobeName",String.class)+"|"+atisStrobe.attr("location",String.class));
        plStrobeMap.put("beforeLevelWarnType", ConstantUtil.nromalWarnType);
        returnMapList.add(plStrobeMap);

        render(200,OutPutUtil.retunSuccMap(parameterMap,returnMapList,null));
    }


    @At(path = "/strobe/queryStrobe", types = {RestRequest.Method.POST})
    public void queryStrobe() throws Exception{
        List<AtisStrobe> atisStrobeList = AtisStrobe.where(map("isAvailable",1)).order("sort ASC").fetch();
        for(AtisStrobe atisStrobe:atisStrobeList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("strobeName",atisStrobe.attr("strobeName",String.class));
            map.put("beforeLevel",atisStrobe.attr("beforeLevel",Float.class));
            map.put("afterLevel",atisStrobe.attr("afterLevel",Float.class));
            map.put("longitude",atisStrobe.attr("longitude",String.class));
            map.put("latitude",atisStrobe.attr("latitude",String.class));
            map.put("id",atisStrobe.id());
            map.put("strobeNum",atisStrobe.attr("strobeNum",Integer.class));
            map.put("strobeStatus",atisStrobe.attr("strobeStatus",Integer.class));
            map.put("strobeDesc",atisStrobe.attr("strobeDesc",String.class));
            AtisCamera atisCamera = AtisCamera.find(atisStrobe.attr("cameraId",Integer.class));
            map.put("cameraIp",atisCamera.attr("cameraIp",String.class));
            map.put("cameraPort",atisCamera.attr("cameraPort",String.class));
            map.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
            map.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
            returnMapList.add(map);
        }
        List<AtisPump> atisPumpList = AtisPump.findAll();
        Map<String,Object> parameterMap = new HashMap<String, Object>();
        List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
        for(AtisPump atisPump:atisPumpList){
            Map<String,Object> pumpMap = new HashMap<String,Object>();
            pumpMap.put("pumpName",atisPump.attr("pumpName",String.class));
            pumpMap.put("pumpId",atisPump.id());
            pumpMap.put("longitude",atisPump.attr("longitude",String.class));
            pumpMap.put("latitude",atisPump.attr("latitude",String.class));
            mapList.add(pumpMap);
        }
        parameterMap.put("pumpList",mapList);
        parameterMap.put("rainList", queryRain());
        List<AtisRiver> atisRivers = AtisRiver.findAll();
        List<Map<String,Object>> tmpList =new ArrayList<Map<String, Object>>();
        for(AtisRiver atisRiver : atisRivers){
            Map<String,Object> cameraMap = new HashMap<String,Object>();
            AtisCamera atisCamera = AtisCamera.find(Integer.parseInt(atisRiver.attr("cameraId",String.class)));
            cameraMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
            cameraMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
            cameraMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
            cameraMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
            cameraMap.put("longitude",atisRiver.attr("longitude",String.class));
            cameraMap.put("latitude",atisRiver.attr("latitude",String.class));
            cameraMap.put("riverName",atisRiver.attr("riverName",String.class));
            tmpList.add(cameraMap);
        }
        parameterMap.put("cameraList",tmpList);

        render(200,OutPutUtil.retunSuccMap(parameterMap,returnMapList,token));
    }

    @At(path = "/strobe/queryStrobeById", types = {RestRequest.Method.POST})
    public void queryStrobeById() {
        AtisStrobe atisStrobe = AtisStrobe.find(paramAsInt("strobeId"));
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("strobeName",atisStrobe.attr("strobeName",String.class));
        map.put("beforeLevel",atisStrobe.attr("beforeLevel",Float.class));
        map.put("afterLevel",atisStrobe.attr("afterLevel",Float.class));
        map.put("longitude",atisStrobe.attr("longitude",String.class));
        map.put("latitude",atisStrobe.attr("latitude",String.class));
        map.put("id",atisStrobe.id());
        map.put("strobeNum",atisStrobe.attr("strobeNum",Integer.class));
        map.put("strobeStatus",atisStrobe.attr("strobeStatus",Integer.class));
        map.put("strobeDesc",atisStrobe.attr("strobeDesc",String.class));
        AtisCamera atisCamera = AtisCamera.find(atisStrobe.attr("cameraId",Integer.class));
        map.put("cameraIp",atisCamera.attr("cameraIp",String.class));
        map.put("cameraPort",atisCamera.attr("cameraPort",String.class));
        map.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
        map.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
        AtisBroadcast atisBroadcast = AtisBroadcast.find(atisStrobe.attr("broadcastId",Integer.class));
        map.put("broadcastIp",atisBroadcast.attr("broadcastIp", String.class));
        map.put("broadcastPort",atisBroadcast.attr("broadcastPort", String.class) );
        map.put("broadcastPwd",atisBroadcast.attr("broadcastPwd", String.class) );
        map.put("broadcastAccount",atisBroadcast.attr("broadcastAccount", String.class) );
        render(200,OutPutUtil.retunSuccMap(map,null,token));
    }

//    @At(path = "/strobe/strobeDetail", types = {RestRequest.Method.POST})
//    public void strobeDetail() {
//        if(redisClient==null){
//            redisClient = new RedisClient(settings);
//            logger.info("strobeDetail==>new RedisClient");
//        }
//
//        String strobeId = param("strobeId");
//        String subStrobeId = param("subStrobeId");
//        Map parameterMap = new HashMap();
////        AtisStrobe atisStrobe= AtisStrobe.find(Integer.parseInt(strobeId));
//        parameterMap.put("strobeStatus",redisClient.get(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.strobeStatus).toString());
////        int strobeNum = atisStrobe.attr("strobeNum",Integer.class);
//        int strobeNum = Integer.parseInt(redisClient.get(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.strobeNum).toString());
//        if(strobeNum==1){
//            if (Integer.parseInt(subStrobeId)==1){
//                parameterMap.put("gatage", StringUtil.getIntData(redisClient,"RE,5",strobeId,subStrobeId));
//                parameterMap.put("current",StringUtil.getIntData(redisClient,"RE,7",strobeId,subStrobeId));
//                parameterMap.put("voltageA",StringUtil.getIntData(redisClient,"RE,9",strobeId,subStrobeId));
//                parameterMap.put("voltageB",StringUtil.getIntData(redisClient,"RE,11",strobeId,subStrobeId));
//                parameterMap.put("voltageC",StringUtil.getIntData(redisClient,"RE,13",strobeId,subStrobeId));
//                parameterMap.put("isRemote",StringUtil.getIntData(redisClient,"DO,5",strobeId,subStrobeId));
//                parameterMap.put("openEnd",StringUtil.getIntData(redisClient,"DO,6",strobeId,subStrobeId));
//                parameterMap.put("closeEnd",StringUtil.getIntData(redisClient,"DO,7",strobeId,subStrobeId));
//                parameterMap.put("isOpening",StringUtil.getIntData(redisClient,"DO,8",strobeId,subStrobeId));
//                parameterMap.put("isClosing",StringUtil.getIntData(redisClient,"DO,9",strobeId,subStrobeId));
//                parameterMap.put("electricalError",StringUtil.getIntData(redisClient,"DO,15",strobeId,subStrobeId));
//                parameterMap.put("communicationError",StringUtil.getIntData(redisClient,"DO,16",strobeId,subStrobeId));
//                parameterMap.put("speedError",StringUtil.getIntData(redisClient,"DO,17",strobeId,subStrobeId));
//                parameterMap.put("timeoutError",StringUtil.getIntData(redisClient,"DO,18",strobeId,subStrobeId));
//                parameterMap.put("currentError",StringUtil.getIntData(redisClient,"DO,19",strobeId,subStrobeId));
//                parameterMap.put("voltageError",StringUtil.getIntData(redisClient,"DO,20",strobeId,subStrobeId));
//            }
//        }else{
//            if(Integer.parseInt(subStrobeId)==1){
//                parameterMap.put("gatage",StringUtil.getIntData(redisClient,"RE,5",strobeId,subStrobeId));
//                parameterMap.put("current",StringUtil.getIntData(redisClient,"RE,9",strobeId,subStrobeId));
//                parameterMap.put("voltageA",StringUtil.getIntData(redisClient,"RE,11",strobeId,subStrobeId));
//                parameterMap.put("voltageB",StringUtil.getIntData(redisClient,"RE,13",strobeId,subStrobeId));
//                parameterMap.put("voltageC",StringUtil.getIntData(redisClient,"RE,15",strobeId,subStrobeId));
//                parameterMap.put("isRemote",StringUtil.getIntData(redisClient,"DO,5",strobeId,subStrobeId));
//                parameterMap.put("openEnd",StringUtil.getIntData(redisClient,"DO,6",strobeId,subStrobeId));
//                parameterMap.put("closeEnd",StringUtil.getIntData(redisClient,"DO,7",strobeId,subStrobeId));
//                parameterMap.put("isOpening",StringUtil.getIntData(redisClient,"DO,8",strobeId,subStrobeId));
//                parameterMap.put("isClosing",StringUtil.getIntData(redisClient,"DO,9",strobeId,subStrobeId));
//                parameterMap.put("electricalError",StringUtil.getIntData(redisClient,"DO,30",strobeId,subStrobeId));
//                parameterMap.put("communicationError",StringUtil.getIntData(redisClient,"DO,31",strobeId,subStrobeId));
//                parameterMap.put("speedError",StringUtil.getIntData(redisClient,"DO,32",strobeId,subStrobeId));
//                parameterMap.put("timeoutError",StringUtil.getIntData(redisClient,"DO,33",strobeId,subStrobeId));
//                parameterMap.put("currentError",StringUtil.getIntData(redisClient,"DO,34",strobeId,subStrobeId));
//                parameterMap.put("voltageError",StringUtil.getIntData(redisClient,"DO,35",strobeId,subStrobeId));
//            }else if(Integer.parseInt(subStrobeId)==2) {
//                parameterMap.put("gatage",StringUtil.getIntData(redisClient,"RE,7",strobeId,subStrobeId));
//                parameterMap.put("current",StringUtil.getIntData(redisClient,"RE,9",strobeId,subStrobeId));
//                parameterMap.put("voltageA",StringUtil.getIntData(redisClient,"RE,11",strobeId,subStrobeId));
//                parameterMap.put("voltageB",StringUtil.getIntData(redisClient,"RE,13",strobeId,subStrobeId));
//                parameterMap.put("voltageC",StringUtil.getIntData(redisClient,"RE,15",strobeId,subStrobeId));
//                parameterMap.put("isRemote",StringUtil.getIntData(redisClient,"DO,20",strobeId,subStrobeId));
//                parameterMap.put("openEnd",StringUtil.getIntData(redisClient,"DO,21",strobeId,subStrobeId));
//                parameterMap.put("closeEnd",StringUtil.getIntData(redisClient,"DO,22",strobeId,subStrobeId));
//                parameterMap.put("isOpening",StringUtil.getIntData(redisClient,"DO,23",strobeId,subStrobeId));
//                parameterMap.put("isClosing",StringUtil.getIntData(redisClient,"DO,24",strobeId,subStrobeId));
//                parameterMap.put("electricalError",StringUtil.getIntData(redisClient,"DO,30",strobeId,subStrobeId));
//                parameterMap.put("communicationError",StringUtil.getIntData(redisClient,"DO,31",strobeId,subStrobeId));
//                parameterMap.put("speedError",StringUtil.getIntData(redisClient,"DO,32",strobeId,subStrobeId));
//                parameterMap.put("timeoutError",StringUtil.getIntData(redisClient,"DO,33",strobeId,subStrobeId));
//                parameterMap.put("currentError",StringUtil.getIntData(redisClient,"DO,34",strobeId,subStrobeId));
//                parameterMap.put("voltageError",StringUtil.getIntData(redisClient,"DO,35",strobeId,subStrobeId));
//            }
//        }
//        render(200,OutPutUtil.retunSuccMap(parameterMap,null,token));
//    }


    @At(path = "/strobe/queryStrobeStatus", types = {RestRequest.Method.POST})
    public void queryWaterLineStrobeStatus() {
        List<AtisStrobe> atisStrobeList = AtisStrobe.where(map("isAvailable",1)).fetch();
        for(AtisStrobe atisStrobe :atisStrobeList){
            if(atisStrobe.attr("isOpening",Integer.class)==1){
                Map returnMap = new HashMap();
                returnMap.put("strobeName", atisStrobe.attr("strobeName", String.class));
                returnMap.put("strobeStatus", atisStrobe.attr("strobeStatus", Integer.class));
                returnMap.put("strobeNumber", atisStrobe.attr("strobeNum", Integer.class));
                returnMap.put("strobeId", atisStrobe.id());
                returnMap.put("longitude", atisStrobe.attr("longitude", String.class));
                returnMap.put("latitude", atisStrobe.attr("latitude", String.class));
                returnMap.put("type",1);
                returnMapList.add(returnMap);
            }else if(atisStrobe.attr("isClosing",Integer.class)==1){
                Map returnMap = new HashMap();
                returnMap.put("strobeName", atisStrobe.attr("strobeName", String.class));
                returnMap.put("strobeStatus", atisStrobe.attr("strobeStatus", Integer.class));
                returnMap.put("strobeNumber", atisStrobe.attr("strobeNum", Integer.class));
                returnMap.put("strobeId", atisStrobe.id());
                returnMap.put("longitude", atisStrobe.attr("longitude", String.class));
                returnMap.put("latitude", atisStrobe.attr("latitude", String.class));
                returnMap.put("type",0);
                returnMapList.add(returnMap);
            }
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }

    @At(path = "/strobe/getRedisStatus", types = {RestRequest.Method.GET})
    public void getRedisStatus() {
        String register = param("register");
        AtisModbusLog atisModbusLog =(AtisModbusLog) redisClient.getObj(register);
        Map returnMap = new HashMap();
        returnMap.put("sss",atisModbusLog.getData());
        render(200, OutPutUtil.retunSuccMap(returnMap,null,null));
    }

    @At(path = "/strobe/strobeDetail", types = {RestRequest.Method.POST})
    public void strobeDetail() {
        String strobeId = param("strobeId");
        String subStrobeId = param("subStrobeId");
        Map parameterMap = new HashMap();
        Jedis jedis =redisClient.borrow();
        try {
//        AtisStrobe atisStrobe= AtisStrobe.find(Integer.parseInt(strobeId));
            parameterMap.put("strobeStatus", jedis.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus).toString());
//        int strobeNum = atisStrobe.attr("strobeNum",Integer.class);
            int strobeNum = Integer.parseInt(jedis.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeNum).toString());
            if (strobeNum == 1) {
                if (Integer.parseInt(subStrobeId) == 1) {
                    parameterMap.put("gatage", StringUtil.getIntData(jedis, "RE,5", strobeId, subStrobeId));
                    parameterMap.put("current", StringUtil.getIntData(jedis, "RE,7", strobeId, subStrobeId));
                    parameterMap.put("voltageA", StringUtil.getIntData(jedis, "RE,9", strobeId, subStrobeId));
                    parameterMap.put("voltageB", StringUtil.getIntData(jedis, "RE,11", strobeId, subStrobeId));
                    parameterMap.put("voltageC", StringUtil.getIntData(jedis, "RE,13", strobeId, subStrobeId));
                    parameterMap.put("isRemote", StringUtil.getIntData(jedis, "DO,5", strobeId, subStrobeId));
                    parameterMap.put("openEnd", StringUtil.getIntData(jedis, "DO,6", strobeId, subStrobeId));
                    parameterMap.put("closeEnd", StringUtil.getIntData(jedis, "DO,7", strobeId, subStrobeId));
                    parameterMap.put("isOpening", StringUtil.getIntData(jedis, "DO,8", strobeId, subStrobeId));
                    parameterMap.put("isClosing", StringUtil.getIntData(jedis, "DO,9", strobeId, subStrobeId));
                    parameterMap.put("electricalError", StringUtil.getIntData(jedis, "DO,15", strobeId, subStrobeId));
                    parameterMap.put("communicationError", StringUtil.getIntData(jedis, "DO,16", strobeId, subStrobeId));
                    parameterMap.put("speedError", StringUtil.getIntData(jedis, "DO,17", strobeId, subStrobeId));
                    parameterMap.put("timeoutError", StringUtil.getIntData(jedis, "DO,18", strobeId, subStrobeId));
                    parameterMap.put("currentError", StringUtil.getIntData(jedis, "DO,19", strobeId, subStrobeId));
                    parameterMap.put("voltageError", StringUtil.getIntData(jedis, "DO,20", strobeId, subStrobeId));
                }
            } else {
                if (Integer.parseInt(subStrobeId) == 1) {
                    parameterMap.put("gatage", StringUtil.getIntData(jedis, "RE,5", strobeId, subStrobeId));
                    parameterMap.put("current", StringUtil.getIntData(jedis, "RE,9", strobeId, subStrobeId));
                    parameterMap.put("voltageA", StringUtil.getIntData(jedis, "RE,11", strobeId, subStrobeId));
                    parameterMap.put("voltageB", StringUtil.getIntData(jedis, "RE,13", strobeId, subStrobeId));
                    parameterMap.put("voltageC", StringUtil.getIntData(jedis, "RE,15", strobeId, subStrobeId));
                    parameterMap.put("isRemote", StringUtil.getIntData(jedis, "DO,5", strobeId, subStrobeId));
                    parameterMap.put("openEnd", StringUtil.getIntData(jedis, "DO,6", strobeId, subStrobeId));
                    parameterMap.put("closeEnd", StringUtil.getIntData(jedis, "DO,7", strobeId, subStrobeId));
                    parameterMap.put("isOpening", StringUtil.getIntData(jedis, "DO,8", strobeId, subStrobeId));
                    parameterMap.put("isClosing", StringUtil.getIntData(jedis, "DO,9", strobeId, subStrobeId));
                    parameterMap.put("electricalError", StringUtil.getIntData(jedis, "DO,30", strobeId, subStrobeId));
                    parameterMap.put("communicationError", StringUtil.getIntData(jedis, "DO,31", strobeId, subStrobeId));
                    parameterMap.put("speedError", StringUtil.getIntData(jedis, "DO,32", strobeId, subStrobeId));
                    parameterMap.put("timeoutError", StringUtil.getIntData(jedis, "DO,33", strobeId, subStrobeId));
                    parameterMap.put("currentError", StringUtil.getIntData(jedis, "DO,34", strobeId, subStrobeId));
                    parameterMap.put("voltageError", StringUtil.getIntData(jedis, "DO,35", strobeId, subStrobeId));
                } else if (Integer.parseInt(subStrobeId) == 2) {
                    parameterMap.put("gatage", StringUtil.getIntData(jedis, "RE,7", strobeId, subStrobeId));
                    parameterMap.put("current", StringUtil.getIntData(jedis, "RE,9", strobeId, subStrobeId));
                    parameterMap.put("voltageA", StringUtil.getIntData(jedis, "RE,11", strobeId, subStrobeId));
                    parameterMap.put("voltageB", StringUtil.getIntData(jedis, "RE,13", strobeId, subStrobeId));
                    parameterMap.put("voltageC", StringUtil.getIntData(jedis, "RE,15", strobeId, subStrobeId));
                    parameterMap.put("isRemote", StringUtil.getIntData(jedis, "DO,20", strobeId, subStrobeId));
                    parameterMap.put("openEnd", StringUtil.getIntData(jedis, "DO,21", strobeId, subStrobeId));
                    parameterMap.put("closeEnd", StringUtil.getIntData(jedis, "DO,22", strobeId, subStrobeId));
                    parameterMap.put("isOpening", StringUtil.getIntData(jedis, "DO,23", strobeId, subStrobeId));
                    parameterMap.put("isClosing", StringUtil.getIntData(jedis, "DO,24", strobeId, subStrobeId));
                    parameterMap.put("electricalError", StringUtil.getIntData(jedis, "DO,30", strobeId, subStrobeId));
                    parameterMap.put("communicationError", StringUtil.getIntData(jedis, "DO,31", strobeId, subStrobeId));
                    parameterMap.put("speedError", StringUtil.getIntData(jedis, "DO,32", strobeId, subStrobeId));
                    parameterMap.put("timeoutError", StringUtil.getIntData(jedis, "DO,33", strobeId, subStrobeId));
                    parameterMap.put("currentError", StringUtil.getIntData(jedis, "DO,34", strobeId, subStrobeId));
                    parameterMap.put("voltageError", StringUtil.getIntData(jedis, "DO,35", strobeId, subStrobeId));
                }
            }
        }catch (Exception e){
            redisClient.revert(jedis);
            logger.info("strobeId:"+strobeId+"出错"+e.toString());
        }
        redisClient.revert(jedis);
        render(200,OutPutUtil.retunSuccMap(parameterMap,null,token));
    }

}