package com.atis.controller.http;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.*;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.collections.map.HashedMap;

import javax.persistence.criteria.CriteriaBuilder;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WaterLineController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("queryWaterLine","saveWaterLine","queryCurrentWaterLine","queryWaterLineDetails","updateCurrentWaterLine","waterLineStart","waterLineStop")));
        beforeFilter("getUserId", WowCollections.map(only, WowCollections.list("waterLineStart","waterLineStop")));
    }

    @At(path = "/strobe/queryWaterLine", types = {RestRequest.Method.POST})
    public void queryWaterLine() {
        List<AtisWaterLine> atisWaterLineList = AtisWaterLine.where(map("parentId",0)).fetch();
        for(AtisWaterLine atisWaterLine:atisWaterLineList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id",atisWaterLine.id());
            map.put("lineName",atisWaterLine.attr("lineName", String.class));
            map.put("lineDesc",atisWaterLine.attr("lineDesc",String.class));
            List<AtisWaterLine> atisWaterLineSubList = AtisWaterLine.where(map("parentId",atisWaterLine.id())).fetch();
            List<Map<String,Object>> subList = new ArrayList<Map<String, Object>>();
            for(AtisWaterLine atisWaterSubLine:atisWaterLineSubList){
                Map<String,Object> subMap = new HashMap<String, Object>();
                subMap.put("id",atisWaterSubLine.id());
                subMap.put("lineName",atisWaterSubLine.attr("lineName", String.class));
                subMap.put("isCurrent",atisWaterSubLine.attr("isCurrent", Integer.class));
                subMap.put("lineDesc",atisWaterSubLine.attr("lineDesc", String.class));
                subList.add(subMap);
            }
            map.put("subList",subList);
            returnMapList.add(map);
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }

    @At(path = "/strobe/saveWaterLine", types = {RestRequest.Method.POST})
    public void saveWaterLine() {
        AtisWaterLine atisWaterLine = AtisWaterLine.create(map("lineName",param("lineName"),
                                                                "strobeGroup",param("strobeGroup"),
                                                                "parentId",0,"createBy",redisClient.get(token),
                                                                "modifyDate",System.currentTimeMillis()/1000,
                                                                "createDate",System.currentTimeMillis()/1000));
        atisWaterLine.save();
        render(200, OutPutUtil.retunSuccMap(null,null,token));
    }

    @At(path = "/strobe/queryCurrentWaterLine", types = {RestRequest.Method.POST})
    public void queryCurrentWaterLine() {
        AtisWaterLine atisWaterLine= AtisWaterLine.where(map("isCurrent",1)).single_fetch();
        Map<String,Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("id",atisWaterLine.id());
        parameterMap.put("lineName",atisWaterLine.attr("lineName", String.class));
        parameterMap.put("isCurrent",atisWaterLine.attr("isCurrent", Integer.class));
        parameterMap.put("lineDesc",atisWaterLine.attr("lineDesc", String.class));
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
            totalMap.put("longitude", atisStrobe.attr("longitude", String.class));
            totalMap.put("latitude", atisStrobe.attr("latitude", String.class));
            totalMap.put("needStatus",strobeArray[1] );
            totalMap.put("gatage",atisStrobe.attr("gatage", String.class) );
            AtisBroadcast atisBroadcast = AtisBroadcast.find(atisStrobe.attr("broadcastId",Integer.class));
            totalMap.put("broadcastIp",atisBroadcast.attr("broadcastIp", String.class) );
            totalMap.put("broadcastPort",atisBroadcast.attr("broadcastPort", String.class) );
            totalMap.put("broadcastPwd",atisBroadcast.attr("broadcastPwd", String.class) );
            totalMap.put("broadcastAccount",atisBroadcast.attr("broadcastAccount", String.class) );
            returnMapList.add(totalMap);
        }
        parameterMap.put("waterLineRun",redisClient.get("waterLineRun"));
        render(200, OutPutUtil.retunSuccMap(parameterMap,returnMapList,token));
    }

    @At(path = "/strobe/queryWaterLineDetails", types = {RestRequest.Method.POST})
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
            totalMap.put("longitude", atisStrobe.attr("longitude", String.class));
            totalMap.put("latitude", atisStrobe.attr("latitude", String.class));
            totalMap.put("needStatus",strobeArray[1] );
            totalMap.put("gatage",atisStrobe.attr("gatage", String.class) );
            AtisBroadcast atisBroadcast = AtisBroadcast.find(atisStrobe.attr("broadcastId",Integer.class));
            totalMap.put("broadcastIp",atisBroadcast.attr("broadcastIp", String.class) );
            totalMap.put("broadcastPort",atisBroadcast.attr("broadcastPort", String.class) );
            totalMap.put("broadcastPwd",atisBroadcast.attr("broadcastPwd", String.class) );
            totalMap.put("broadcastAccount",atisBroadcast.attr("broadcastAccount", String.class) );
            returnMapList.add(totalMap);
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }

    @At(path = "/strobe/updateCurrentWaterLine", types = {RestRequest.Method.POST})
    public void updateCurrentWaterLine() {
        int id = paramAsInt("waterLineId");
        String userId = redisClient.get(token);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        AtisWaterLineLog atisWaterLineLog = AtisWaterLineLog.create(map("waterLineId",id,"operaMonth",dateFormat.format(new Date()).toString(),"createDate",System.currentTimeMillis()/1000,"modifyDate",System.currentTimeMillis()/1000,"userId",Integer.parseInt(userId)));
        atisWaterLineLog.save();
        render(200, OutPutUtil.retunSuccMap(1,null,token));
    }

    @At(path = "/strobe/queryWaterLineStatus", types = {RestRequest.Method.POST})
    public void queryWaterLineStatus() {
        int id = paramAsInt("waterLineId");
        String userId = redisClient.get(token);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        AtisWaterLineLog atisWaterLineLog = AtisWaterLineLog.create(map("waterLineId",id,"operaMonth",dateFormat.format(new Date()).toString(),"createDate",System.currentTimeMillis()/1000,"modifyDate",System.currentTimeMillis()/1000,"userId",Integer.parseInt(userId)));
        atisWaterLineLog.save();
        render(200, OutPutUtil.retunSuccMap(1,null,token));
    }

    @At(path = "/strobe/queryWaterLineStrobeStatus", types = {RestRequest.Method.POST})
    public void queryWaterLineStrobeStatus() {
        int id = paramAsInt("waterLineId");
        Map<String,Object> parameterMap = new HashMap();
        AtisWaterLine atisWaterLine = AtisWaterLine.find(id);
        String strobeGroup =  atisWaterLine.attr("strobeGroup",String.class);
        String[] strobeGroupArray = strobeGroup.split("@");
        for(int i=0;i<strobeGroupArray.length;i++){
            String[] strobeArray = strobeGroupArray[i].split(",");
            int strobeId = Integer.parseInt(strobeArray[0]);
            AtisStrobe atisStrobe = AtisStrobe.find(strobeId);
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
//        if(redisClient.get("currentRunningStrobe").equals("0,0")){
//            parameterMap.put("isWaterLineStop","1");
//        }
        parameterMap.put("info",redisClient.get("waterLineInfo"));
        render(200, OutPutUtil.retunSuccMap(parameterMap,returnMapList,token));
    }
    @At(path = "/strobe/waterLineStart", types = {RestRequest.Method.POST})
    public void waterLineStart() {
        int id = paramAsInt("waterLineId");
        Map parameterMap = new HashMap();
        int countRunning = Integer.parseInt(redisClient.get("waterLineRun").toString()) ;
        redisClient.set("strobeQueue",param("strobeGroup"));
        //当前无配水线路正在进行，否则返回错误
        if(countRunning==0) {
            redisClient.set("waterLineInfo","组启准备中,请稍候...");
            //检查配水线路的闸门序列，如果不为空则进入循环进行单闸门控制
            while(!StringUtil.checkEmpty(redisClient.get("strobeQueue"))){
                int strobeId = Integer.parseInt(redisClient.get("strobeQueue").split("@")[0].split(",")[0]);
                AtisStrobe atisStrobe = AtisStrobe.find(strobeId);
                int strobeNum = atisStrobe.attr("strobeNum", Integer.class);
                try {
                    //如果遇到双闸门情况，依次开启每个闸门
                    for (int j = 1; j <= strobeNum; j++) {
                        AtisStrobeModbusConfig gatageConfig = AtisStrobeModbusConfig.where(map("strobeId", strobeId, "type", 5, "subStrobeId", j)).single_fetch();
                        AtisStrobeModbusConfig gatageOpera = AtisStrobeModbusConfig.where(map("strobeId", strobeId, "type", 6, "subStrobeId", j)).single_fetch();
                        ModbusUtil.writeRegister(userId,gatageConfig.attr("name", String.class), gatageConfig.attr("ip", String.class), gatageConfig.attr("port", Integer.class),
                                gatageConfig.attr("controllerId", Integer.class),
                                gatageConfig.attr("registerAddress", String.class),redisClient.get("strobeQueue").split("@")[0].split(",")[1]);
                        ModbusUtil.writeDigitalOutput(userId,gatageOpera.attr("name", String.class), gatageOpera.attr("ip", String.class), gatageOpera.attr("port", Integer.class),
                                gatageOpera.attr("controllerId", Integer.class),
                                gatageOpera.attr("registerAddress", String.class), "1");
                        //设置当前正在运行的闸门，用于组停使用
                        redisClient.set("currentRunningStrobe",String.valueOf(strobeId)+","+j);
                        redisClient.set("waterLineRun","1");
                        //如果开到最后一个闸门则置闸门控制序列为空，不为空
                        if(redisClient.get("strobeQueue").split("@")[0].length()+1>=redisClient.get("strobeQueue").length() ){
                            redisClient.set("strobeQueue","");

                        }else if(!StringUtil.checkEmpty(redisClient.get("strobeQueue"))){
                            redisClient.set("strobeQueue", redisClient.get("strobeQueue").substring(redisClient.get("strobeQueue").split("@")[0].length() + 1, redisClient.get("strobeQueue").length()));
                        }
                        if(j==1){
                            redisClient.set("waterLineTime",String.valueOf(new Date().getTime()));

                            //标志位
                            int k =1;
                            int strobeOperaFlag=0;
                            lab:while(true) {
                                while (Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,8"+ ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())==1||
                                        Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,9"+ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())==1){
                                    strobeOperaFlag = 1;
                                    redisClient.set("waterLineInfo","当前闸门["+atisStrobe.attr("strobeName", String.class)+"]正在组启中...");
                                    if((new Date().getTime()-Long.parseLong(redisClient.get("waterLineTime")))/(60*1000)>30){
                                        redisClient.set("waterLineInfo","当前闸门["+atisStrobe.attr("strobeName", String.class)+"]PLC异常,请检查");
                                        parameterMap.put("info","当前闸门["+atisStrobe.attr("strobeName", String.class)+"]PLC异常,请检查");
                                        redisClient.set("strobeQueue","");
                                        stopRunningStrobe(redisClient.get("currentRunningStrobe"),userId);
                                        render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
                                    }
                                }
                                if(strobeOperaFlag ==1 ){
                                    redisClient.set("waterLineInfo","       当前闸门["+atisStrobe.attr("strobeName", String.class)+"]组启完毕...");
                                    parameterMap.put("info", "当前闸门[" + atisStrobe.attr("strobeName", String.class) + "]组启完毕...");
                                    break lab;
                                }else {
                                    if (Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,8"+ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())!=1&&
                                            Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,9"+ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())!=1){
                                        k++;
                                        Thread.sleep(3000);
                                        if (k > 60) {
                                            redisClient.set("waterLineInfo", "当前闸门[" + atisStrobe.attr("strobeName", String.class) + "]组启超时,请检查");
                                            parameterMap.put("info", "当前闸门[" + atisStrobe.attr("strobeName", String.class) + "]组启超时,请检查");
                                            redisClient.set("strobeQueue", "");
                                            stopRunningStrobe(redisClient.get("currentRunningStrobe"), userId);
                                            render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
                                        }
                                    }
                                }
                            }

                        }else if(j==2){
                            redisClient.set("waterLineTime",String.valueOf(new Date().getTime()));

                            //标志位
                            int k =1;
                            int strobeOperaFlag=0;
                            lab:while(true) {
                                while (Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,23"+ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())==1||
                                        Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,24"+ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())==1){
                                    strobeOperaFlag = 1;
                                    redisClient.set("waterLineInfo","当前闸门["+atisStrobe.attr("strobeName", String.class)+"二号闸门]正在组启中...");
                                    if((new Date().getTime()-Long.parseLong(redisClient.get("waterLineTime")))/(60*1000)>30){
                                        redisClient.set("waterLineInfo","当前闸门["+atisStrobe.attr("strobeName", String.class)+"二号闸门]PLC异常,请检查");
                                        parameterMap.put("info","当前闸门["+atisStrobe.attr("strobeName", String.class)+"二号闸门]PLC异常,请检查");
                                        redisClient.set("strobeQueue","");
                                        stopRunningStrobe(redisClient.get("currentRunningStrobe"),userId);
                                        render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
                                    }
                                }
                                if(strobeOperaFlag ==1 ){
                                    redisClient.set("waterLineInfo","当前闸门["+atisStrobe.attr("strobeName", String.class)+"二号闸门]组启完毕...");
                                    parameterMap.put("info", "当前闸门[" + atisStrobe.attr("strobeName", String.class) + "二号闸门]组启完毕...");
                                    break lab;
                                }else {
                                    if (Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,23"+ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())!=1&&
                                            Integer.parseInt(((AtisModbusLog)redisClient.getObj("DO,24"+ConstantUtil.separator+strobeId+ConstantUtil.separator+j)).getData())!=1){
                                        k++;
                                        Thread.sleep(3000);
                                        if (k > 30) {
                                            redisClient.set("waterLineInfo", "当前闸门[" + atisStrobe.attr("strobeName", String.class) + "二号闸门]组启超时,请检查");
                                            parameterMap.put("info", "当前闸门[" + atisStrobe.attr("strobeName", String.class) + "二号闸门]组启超时,请检查");
                                            redisClient.set("strobeQueue", "");
                                            stopRunningStrobe(redisClient.get("currentRunningStrobe"), userId);
                                            redisClient.borrow().expire("waterLineInfo",300);
                                            render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
                                        }
                                    }
                                }
                            }



                        }
                    }
                } catch (Exception e) {
                    redisClient.set("waterLineRun","0");
                    redisClient.set("strobeQueue","");
                    try {
                        stopRunningStrobe(redisClient.get("currentRunningStrobe"),userId);
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                    AtisWaterLineLog atisWaterLineLog = AtisWaterLineLog.create(map("waterLineId",id,"operaMonth",
                            DateUtil.getStandMonth(),"userId",userId,"type",0,"operaDate",DateUtil.getStandDate(),"operaYear",DateUtil.getStandYear()));
                    atisWaterLineLog.save();
                    redisClient.set("waterLineInfo", atisStrobe.attr("strobeName", String.class) + " 异常，配水线路停止，请检查");
                    parameterMap.put("info", atisStrobe.attr("strobeName", String.class) + " 异常，配水线路停止，请检查");
                    redisClient.borrow().expire("waterLineInfo",300);
                    render(200, OutPutUtil.retunSuccMap(parameterMap, null, token));
                }

            }
            AtisWaterLineLog atisWaterLineLog = AtisWaterLineLog.create(map("waterLineId",id,"operaMonth",
                    DateUtil.getStandMonth(),"userId",userId,"type",1,"operaDate",DateUtil.getStandDate(),"operaYear",DateUtil.getStandYear()));
            atisWaterLineLog.save();
            AtisWaterLine atisWaterLine =AtisWaterLine.find(id);
            atisWaterLine.attr("isCurrent",1);
            atisWaterLine.update();
            redisClient.set("waterLineRun","0");
        }else{
            AtisWaterLineLog atisWaterLineLog = AtisWaterLineLog.create(map("waterLineId",id,"operaMonth",
                    DateUtil.getStandMonth(),"userId",userId,"type",0,"operaDate",DateUtil.getStandDate(),"operaYear",DateUtil.getStandYear()));
            atisWaterLineLog.save();
            parameterMap.put("info","当前正在配水，不允许操作");
            redisClient.set("waterLineInfo", "当前正在配水，不允许操作");
            render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
        }
        redisClient.set("currentRunningStrobe","0,0");
        redisClient.borrow().expire("waterLineInfo",300);
        render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
    }

    @At(path = "/strobe/waterLineStop", types = {RestRequest.Method.POST})
    public void waterLineStop() {
        String currentRunningStrobe = redisClient.get("currentRunningStrobe");
        Map parameterMap = new HashMap();
        parameterMap.put("info", "组停成功，请勿重复点击");
        redisClient.set("waterLineInfo","组停成功，请勿重复点击");
        redisClient.set("strobeQueue","");
            try {
                stopRunningStrobe(currentRunningStrobe,userId);
            } catch (Exception e) {
                parameterMap.put("info", "组停失败,请检查当前运行闸门");
                redisClient.set("waterLineInfo","组停失败,请检查当前运行闸门");
                render(200, OutPutUtil.retunSuccMap(parameterMap, null, token));
            }
        render(200, OutPutUtil.retunSuccMap(parameterMap, null, token));
    }

    protected void stopRunningStrobe(String runningStrobe,int userId) throws  Exception{
        if(!StringUtil.checkEmpty(runningStrobe)) {
            String strobeIdGroup = redisClient.get("currentRunningStrobe");
            if (!strobeIdGroup.equals("0,0")) {
               int strobeId = Integer.parseInt(strobeIdGroup.split(",")[0]);
               int subStrobeId = Integer.parseInt(strobeIdGroup.split(",")[1]);
               String registerAddress = "DO,1";
               if (subStrobeId == 2) {
                  registerAddress = "DO,16";
               }
               AtisStrobeModbusConfig atisStrobeModbusConfig = AtisStrobeModbusConfig.where(map("strobeId", strobeId, "subStrobeId", subStrobeId, "registerAddress", registerAddress)).single_fetch();
               ModbusUtil.writeDigitalOutput(userId,atisStrobeModbusConfig.attr("name", String.class), atisStrobeModbusConfig.attr("ip", String.class), atisStrobeModbusConfig.attr("port", Integer.class),
                        atisStrobeModbusConfig.attr("controllerId", Integer.class),
                        atisStrobeModbusConfig.attr("registerAddress", String.class), "1");
               redisClient.set("currentRunningStrobe","0,0");
            }
        }
    }

}