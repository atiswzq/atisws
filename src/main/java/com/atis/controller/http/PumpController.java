package com.atis.controller.http;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.OutPutUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/9.
 */
public class PumpController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("queryPump")));
    }

    @At(path = "/pump/queryPump", types = {RestRequest.Method.POST})
    public void queryPump() throws Exception{
        List<AtisPump> atisPumpList = AtisPump.where(map("isAvailable",1)).fetch();
        for(AtisPump atisPump : atisPumpList){
            int id = atisPump.id();
            List<AtisPumpCurrent> atisPumpCurrentList = AtisPumpCurrent.where(map("parentId",String.valueOf(id))).order("id ASC").limit(1).fetch();
            for(AtisPumpCurrent atisPumpCurrent :atisPumpCurrentList){
                Map<String,Object> tmpMap = new HashMap<String,Object>();
                tmpMap.put("id",atisPump.id());
                tmpMap.put("pumpName",atisPumpCurrent.attr("pumpName",String.class));
                tmpMap.put("pumpStatus",atisPumpCurrent.attr("pumpStatus",Integer.class));
                tmpMap.put("voltageab",atisPumpCurrent.attr("voltageab",Integer.class));
                tmpMap.put("voltagebc",atisPumpCurrent.attr("voltagebc",Integer.class));
                tmpMap.put("voltageca",atisPumpCurrent.attr("voltageca",Integer.class));
                tmpMap.put("currenta",(atisPumpCurrent.attr("currenta",Integer.class))/10);
                tmpMap.put("currentb",(atisPumpCurrent.attr("currentb",Integer.class))/10);
                tmpMap.put("currentc",(atisPumpCurrent.attr("currentc",Integer.class))/10);
                tmpMap.put("yggl",atisPumpCurrent.attr("yggl",Integer.class));
                tmpMap.put("ggRunTime",atisPumpCurrent.attr("ggRunTime",Integer.class));
                tmpMap.put("plRunTime",atisPumpCurrent.attr("plRunTime",Integer.class));
                tmpMap.put("isRunning",atisPumpCurrent.attr("isRunning",Integer.class));
                tmpMap.put("subPumpId",atisPumpCurrent.id());
                tmpMap.put("ggFlow",atisPumpCurrent.attr("ggRunTime",Integer.class)*60*6.3);
                tmpMap.put("plFlow",atisPumpCurrent.attr("plRunTime",Integer.class)*60*6.3);
                tmpMap.put("isRemote",atisPumpCurrent.attr("isRemote",Integer.class));
                tmpMap.put("isAuto",atisPumpCurrent.attr("isAuto",Integer.class));
                tmpMap.put("closeDelayTime",atisPumpCurrent.attr("closeDelayTime",String.class));
                DecimalFormat df   = new DecimalFormat("######0.00");
                tmpMap.put("closeWater",df.format(atisPumpCurrent.attr("closeWater",Float.class)));
                tmpMap.put("openWater",df.format(atisPumpCurrent.attr("openWater",Float.class)));
                tmpMap.put("currentWater",df.format(atisPumpCurrent.attr("currentWater",Float.class)));
                returnMapList.add(tmpMap);
            }
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }

    @At(path = "/pump/queryPumpDetailById", types = {RestRequest.Method.POST})
    public void queryPumpDetailById() throws Exception{
        AtisPump atisPump = AtisPump.find(paramAsInt("id"));
        int id = atisPump.id();
        List<AtisPumpCurrent> atisPumpCurrentList = AtisPumpCurrent.where(map("parentId",String.valueOf(id))).order("id ASC").fetch();
        for(AtisPumpCurrent atisPumpCurrent :atisPumpCurrentList){
            Map<String,Object> tmpMap = new HashMap<String,Object>();
            tmpMap.put("pumpName",atisPumpCurrent.attr("pumpName",String.class));
            tmpMap.put("pumpStatus",atisPumpCurrent.attr("pumpStatus",Integer.class));
            tmpMap.put("voltageab",atisPumpCurrent.attr("voltageab",Integer.class));
            tmpMap.put("voltagebc",atisPumpCurrent.attr("voltagebc",Integer.class));
            tmpMap.put("voltageca",atisPumpCurrent.attr("voltageca",Integer.class));
            tmpMap.put("currenta",(atisPumpCurrent.attr("currenta",Integer.class))/10);
            tmpMap.put("currentb",(atisPumpCurrent.attr("currentb",Integer.class))/10);
            tmpMap.put("currentc",(atisPumpCurrent.attr("currentc",Integer.class))/10);
            tmpMap.put("yggl",atisPumpCurrent.attr("yggl",Integer.class));
            tmpMap.put("ggRunTime",atisPumpCurrent.attr("ggRunTime",Integer.class));
            tmpMap.put("plRunTime",atisPumpCurrent.attr("plRunTime",Integer.class));
            tmpMap.put("subPumpId",atisPumpCurrent.id());
            tmpMap.put("ggFlow",atisPumpCurrent.attr("ggRunTime",Integer.class)*60*6.3);
            tmpMap.put("plFlow",atisPumpCurrent.attr("plRunTime",Integer.class)*60*6.3);
            tmpMap.put("isRemote",atisPumpCurrent.attr("isRemote",Integer.class));
            tmpMap.put("isAuto",atisPumpCurrent.attr("isAuto",Integer.class));
            tmpMap.put("closeDelayTime",atisPumpCurrent.attr("closeDelayTime",String.class));
            DecimalFormat df   = new DecimalFormat("######0.00");
            tmpMap.put("closeWater",df.format(atisPumpCurrent.attr("closeWater",Float.class)));
            tmpMap.put("openWater",df.format(atisPumpCurrent.attr("openWater",Float.class)));
            tmpMap.put("currentWater",df.format(atisPumpCurrent.attr("currentWater",Float.class)));
            returnMapList.add(tmpMap);
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }
}
