package com.atis.controller.http.web;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.DateUtil;
import com.atis.util.FlowUtil;
import com.atis.util.OutPutUtil;
import com.atis.util.SqlServerUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.collections.map.HashedMap;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WebPumpController extends BController {


    @At(path = "/pump/webQueryPump", types = {RestRequest.Method.POST})
    public void webQueryPump() throws Exception{
        List<AtisPump> atisPumpList=new ArrayList<AtisPump>();
        if(param("webPump")==null){
            atisPumpList = AtisPump.where(map("isAvailable",1)).fetch();
        }else{
            atisPumpList = AtisPump.where(map("attribute1",1)).fetch();
        }
        for(AtisPump atisPump : atisPumpList){
            int id = atisPump.id();
            List<AtisPumpCurrent> atisPumpCurrentList = AtisPumpCurrent.where(map("parentId",String.valueOf(id))).fetch();
            List<Map<String,Object>> mapList = new ArrayList<Map<String, Object>>();
            for(AtisPumpCurrent atisPumpCurrent :atisPumpCurrentList){
                Map<String,Object> tmpMap = new HashMap<String,Object>();
                tmpMap.put("pumpName",atisPumpCurrent.attr("pumpName",String.class));
                tmpMap.put("pumpStatus",atisPumpCurrent.attr("isRunning",Integer.class));
                tmpMap.put("voltageab",atisPumpCurrent.attr("voltageab",Integer.class));
                tmpMap.put("voltagebc",atisPumpCurrent.attr("voltagebc",Integer.class));
                tmpMap.put("voltageca",atisPumpCurrent.attr("voltageca",Integer.class));
                tmpMap.put("currenta",(atisPumpCurrent.attr("currenta",Integer.class))/10);
                tmpMap.put("currentb",(atisPumpCurrent.attr("currentb",Integer.class))/10);
                tmpMap.put("currentc",(atisPumpCurrent.attr("currentc",Integer.class))/10);
                tmpMap.put("yggl",atisPumpCurrent.attr("yggl",Integer.class));
                tmpMap.put("isAuto",atisPumpCurrent.attr("isAuto",Integer.class));
                //四格时间和流量统计
//                Object ggFlow = null;
//                Object plFlow = null;
//                Object ggRunTime = null;
//                Object plRunTime = null;
                if(Integer.parseInt(atisPumpCurrent.attr("parentId",String.class))==1){
                    tmpMap.put("ggFlow", FlowUtil.querySgGgFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("ggFlow"));
                    tmpMap.put("plFlow",FlowUtil.querySgPlFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("plFlow"));
                    tmpMap.put("ggRunTime",FlowUtil.querySgGgFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("ggRunTime"));
                    tmpMap.put("plRunTime",FlowUtil.querySgPlFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("plRunTime"));
//                    ggFlow = FlowUtil.querySgGgFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("ggFlow");
//                    plFlow = FlowUtil.querySgPlFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("plFlow");
//                    ggRunTime = FlowUtil.querySgGgFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("ggRunTime");
//                    plRunTime = FlowUtil.querySgPlFlow(atisPumpCurrent.id(), DateUtil.getDate(new Date())).get("plRunTime");
//                    tmpMap.put("ggFlow", ggFlow);
//                    tmpMap.put("plFlow",plFlow);
//                    tmpMap.put("ggRunTime",ggRunTime);
//                    tmpMap.put("plRunTime",plRunTime);
                }else{
                    tmpMap.put("ggFlow",atisPumpCurrent.attr("ggRunTime",Integer.class)*60*6.3);
                    tmpMap.put("plFlow",atisPumpCurrent.attr("plRunTime",Integer.class)*60*6.3);
                    tmpMap.put("ggRunTime",atisPumpCurrent.attr("ggRunTime",Integer.class));
                    tmpMap.put("plRunTime",atisPumpCurrent.attr("plRunTime",Integer.class));
                }
                tmpMap.put("subPumpId",atisPumpCurrent.id());
                tmpMap.put("isRemote",atisPumpCurrent.attr("isRemote",Integer.class));
                tmpMap.put("closeDelayTime",atisPumpCurrent.attr("closeDelayTime",String.class));
                DecimalFormat df   = new DecimalFormat("######0.00");
                tmpMap.put("closeWater",df.format(atisPumpCurrent.attr("closeWater",Float.class)));
                tmpMap.put("openWater",df.format(atisPumpCurrent.attr("openWater",Float.class)));
                tmpMap.put("currentWater",df.format(atisPumpCurrent.attr("currentWater",Float.class)));
                mapList.add(tmpMap);
            }
            Map<String,Object> returnMap= new HashMap<String, Object>();
            returnMap.put("pumpName",atisPump.attr("pumpName",String.class));
            returnMap.put("subPumpList",mapList);
            if(id == 1){
                //四格泵站流量统计
                returnMap.put("ggFlow",FlowUtil.queryTotalSgGgFlow(DateUtil.getDate(new Date())).get("ggFlow"));
                returnMap.put("plFlow",FlowUtil.queryTotalSgPlFlow(DateUtil.getDate(new Date())).get("plFlow"));

                returnMap.put("monthGgFlow",FlowUtil.queryMonthTotalSgGgFlow(DateUtil.getDate(new Date())).get("ggFlow"));
                returnMap.put("monthPlFlow",FlowUtil.queryMonthTotalSgPlFlow(DateUtil.getDate(new Date())).get("plFlow"));

//                returnMap.put("ggFlow",0);
//                returnMap.put("plFlow",0);
//
//                returnMap.put("monthGgFlow",0);
//                returnMap.put("monthPlFlow",0);
            }else {
                returnMap.put("ggFlow",0);
                returnMap.put("plFlow",0);
                returnMap.put("monthGgFlow",0);
                returnMap.put("monthPlFlow",0               );
            }
            AtisCamera atisCamera = AtisCamera.find(atisPump.attr("cameraId",Integer.class));
            returnMap.put("cameraIp",atisCamera.attr("cameraIp",String.class));
            returnMap.put("cameraPort",atisCamera.attr("cameraPort",String.class));
            returnMap.put("cameraAccount",atisCamera.attr("cameraAccount",String.class));
            returnMap.put("cameraPwd",atisCamera.attr("cameraPwd",String.class));
            returnMapList.add(returnMap);

        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }
}
