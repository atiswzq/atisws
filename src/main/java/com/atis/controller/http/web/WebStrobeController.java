package com.atis.controller.http.web;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.*;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.cache.*;
import net.csdn.modules.cache.SerializeUtil;
import net.csdn.modules.http.RestRequest;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WebStrobeController extends BController {
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("")));
    }

    @At(path = "/strobe/webQueryStrobeById", types = {RestRequest.Method.POST})
    public void webQueryStrobeById() {
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



//    @At(path = "/strobe/webStrobeDetail", types = {RestRequest.Method.POST})
//    public void webStrobeDetail() {
//            String strobeId = param("strobeId");
//            String subStrobeId = param("subStrobeId");
//            Map parameterMap = new HashMap();
//        try {
////        AtisStrobe atisStrobe= AtisStrobe.find(Integer.parseInt(strobeId));
//            parameterMap.put("strobeDesc", redisClient.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeDesc).toString());
//            parameterMap.put("strobeStatus", redisClient.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus).toString());
////        parameterMap.put("strobeStatus",atisStrobe.attr("strobeStatus",Integer.class));
//            int strobeNum = Integer.parseInt(redisClient.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeNum).toString());
////        int strobeNum = atisStrobe.attr("strobeNum",Integer.class);
//            if (strobeNum == 1) {
//                if (Integer.parseInt(subStrobeId) == 1) {
//                    if (strobeId.equals("10")) {
//                        AtisModbusLog atisModbusLog = (AtisModbusLog) redisClient.getObject("DO,8" + ConstantUtil.separator + strobeId + ConstantUtil.separator + subStrobeId);
//                        System.out.println(11);
//                    }
//                    parameterMap.put("gatage", StringUtil.getIntData(redisClient, "RE,5", strobeId, subStrobeId));
//                    parameterMap.put("current", StringUtil.getIntData(redisClient, "RE,7", strobeId, subStrobeId));
//                    parameterMap.put("voltageA", StringUtil.getIntData(redisClient, "RE,9", strobeId, subStrobeId));
//                    parameterMap.put("voltageB", StringUtil.getIntData(redisClient, "RE,11", strobeId, subStrobeId));
//                    parameterMap.put("voltageC", StringUtil.getIntData(redisClient, "RE,13", strobeId, subStrobeId));
//                    parameterMap.put("isRemote", StringUtil.getIntData(redisClient, "DO,5", strobeId, subStrobeId));
//                    parameterMap.put("openEnd", StringUtil.getIntData(redisClient, "DO,6", strobeId, subStrobeId));
//                    parameterMap.put("closeEnd", StringUtil.getIntData(redisClient, "DO,7", strobeId, subStrobeId));
//                    parameterMap.put("isOpening", StringUtil.getIntData(redisClient, "DO,8", strobeId, subStrobeId));
//                    parameterMap.put("isClosing", StringUtil.getIntData(redisClient, "DO,9", strobeId, subStrobeId));
//                    parameterMap.put("electricalError", StringUtil.getIntData(redisClient, "DO,15", strobeId, subStrobeId));
//                    parameterMap.put("communicationError", StringUtil.getIntData(redisClient, "DO,16", strobeId, subStrobeId));
//                    parameterMap.put("speedError", StringUtil.getIntData(redisClient, "DO,17", strobeId, subStrobeId));
//                    parameterMap.put("timeoutError", StringUtil.getIntData(redisClient, "DO,18", strobeId, subStrobeId));
//                    parameterMap.put("currentError", StringUtil.getIntData(redisClient, "DO,19", strobeId, subStrobeId));
//                    parameterMap.put("voltageError", StringUtil.getIntData(redisClient, "DO,20", strobeId, subStrobeId));
//                }
//            } else {
//                if (Integer.parseInt(subStrobeId) == 1) {
//                    parameterMap.put("gatage", StringUtil.getIntData(redisClient, "RE,5", strobeId, subStrobeId));
//                    parameterMap.put("current", StringUtil.getIntData(redisClient, "RE,9", strobeId, subStrobeId));
//                    parameterMap.put("voltageA", StringUtil.getIntData(redisClient, "RE,11", strobeId, subStrobeId));
//                    parameterMap.put("voltageB", StringUtil.getIntData(redisClient, "RE,13", strobeId, subStrobeId));
//                    parameterMap.put("voltageC", StringUtil.getIntData(redisClient, "RE,15", strobeId, subStrobeId));
//                    parameterMap.put("isRemote", StringUtil.getIntData(redisClient, "DO,5", strobeId, subStrobeId));
//                    parameterMap.put("openEnd", StringUtil.getIntData(redisClient, "DO,6", strobeId, subStrobeId));
//                    parameterMap.put("closeEnd", StringUtil.getIntData(redisClient, "DO,7", strobeId, subStrobeId));
//                    parameterMap.put("isOpening", StringUtil.getIntData(redisClient, "DO,8", strobeId, subStrobeId));
//                    parameterMap.put("isClosing", StringUtil.getIntData(redisClient, "DO,9", strobeId, subStrobeId));
//                    parameterMap.put("electricalError", StringUtil.getIntData(redisClient, "DO,30", strobeId, subStrobeId));
//                    parameterMap.put("communicationError", StringUtil.getIntData(redisClient, "DO,31", strobeId, subStrobeId));
//                    parameterMap.put("speedError", StringUtil.getIntData(redisClient, "DO,32", strobeId, subStrobeId));
//                    parameterMap.put("timeoutError", StringUtil.getIntData(redisClient, "DO,33", strobeId, subStrobeId));
//                    parameterMap.put("currentError", StringUtil.getIntData(redisClient, "DO,34", strobeId, subStrobeId));
//                    parameterMap.put("voltageError", StringUtil.getIntData(redisClient, "DO,35", strobeId, subStrobeId));
//                } else if (Integer.parseInt(subStrobeId) == 2) {
//                    parameterMap.put("gatage", StringUtil.getIntData(redisClient, "RE,7", strobeId, subStrobeId));
//                    parameterMap.put("current", StringUtil.getIntData(redisClient, "RE,9", strobeId, subStrobeId));
//                    parameterMap.put("voltageA", StringUtil.getIntData(redisClient, "RE,11", strobeId, subStrobeId));
//                    parameterMap.put("voltageB", StringUtil.getIntData(redisClient, "RE,13", strobeId, subStrobeId));
//                    parameterMap.put("voltageC", StringUtil.getIntData(redisClient, "RE,15", strobeId, subStrobeId));
//                    parameterMap.put("isRemote", StringUtil.getIntData(redisClient, "DO,20", strobeId, subStrobeId));
//                    parameterMap.put("openEnd", StringUtil.getIntData(redisClient, "DO,21", strobeId, subStrobeId));
//                    parameterMap.put("closeEnd", StringUtil.getIntData(redisClient, "DO,22", strobeId, subStrobeId));
//                    parameterMap.put("isOpening", StringUtil.getIntData(redisClient, "DO,23", strobeId, subStrobeId));
//                    parameterMap.put("isClosing", StringUtil.getIntData(redisClient, "DO,24", strobeId, subStrobeId));
//                    parameterMap.put("electricalError", StringUtil.getIntData(redisClient, "DO,30", strobeId, subStrobeId));
//                    parameterMap.put("communicationError", StringUtil.getIntData(redisClient, "DO,31", strobeId, subStrobeId));
//                    parameterMap.put("speedError", StringUtil.getIntData(redisClient, "DO,32", strobeId, subStrobeId));
//                    parameterMap.put("timeoutError", StringUtil.getIntData(redisClient, "DO,33", strobeId, subStrobeId));
//                    parameterMap.put("currentError", StringUtil.getIntData(redisClient, "DO,34", strobeId, subStrobeId));
//                    parameterMap.put("voltageError", StringUtil.getIntData(redisClient, "DO,35", strobeId, subStrobeId));
//                }
//            }
////            Thread.sleep(1000);
//        }catch (Exception e){
//            logger.info("strobeId:"+strobeId+"出错"+e.toString());
//        }
//        render(200,OutPutUtil.retunSuccMap(parameterMap,null,token));
//    }

    @At(path = "/strobe/webQueryStrobe", types = {RestRequest.Method.POST})
    public void webQueryStrobe() throws Exception{
        List<AtisStrobe>  atisStrobeList = AtisStrobe.where(map("isAvailable",1)).fetch();
        Map<String,Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("totalStrobeNum",atisStrobeList.size());
        for(AtisStrobe atisStrobe : atisStrobeList){
            Map<String,Object> returnMap = new HashMap<String, Object>();
            returnMap.put("strobeName",atisStrobe.attr("strobeName",String.class));
            returnMap.put("strobeId",atisStrobe.id());
            returnMap.put("longitude",atisStrobe.attr("longitude",String.class));
            returnMap.put("latitude",atisStrobe.attr("latitude",String.class));
            returnMapList.add(returnMap);
        }
        List<AtisPump> atisPumpList = AtisPump.findAll();
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
        parameterMap.put("rainList", RainUtil.queryRain());
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



    @At(path = "/strobe/webStrobeDetail", types = {RestRequest.Method.POST})
    public void webStrobeDetail() {
        String strobeId = param("strobeId");
        String subStrobeId = param("subStrobeId");
        Map parameterMap = new HashMap();
        Jedis jedis = redisClient.borrow();
        try {
//        AtisStrobe atisStrobe= AtisStrobe.find(Integer.parseInt(strobeId));
            parameterMap.put("strobeDesc", jedis.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeDesc).toString());
            parameterMap.put("strobeStatus", jedis.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus).toString());
//        parameterMap.put("strobeStatus",atisStrobe.attr("strobeStatus",Integer.class));
            int strobeNum = Integer.parseInt(jedis.get(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeNum).toString());
//        int strobeNum = atisStrobe.attr("strobeNum",Integer.class);
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
//            Thread.sleep(1000);
        }catch (Exception e){
            redisClient.revert(jedis);
            logger.info("strobeId:"+strobeId+"出错"+e.toString());
            e.printStackTrace();
        }
        redisClient.revert(jedis);
        render(200,OutPutUtil.retunSuccMap(parameterMap,null,token));
    }
}
