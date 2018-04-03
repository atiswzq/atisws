package com.atis.controller.http;

import com.atis.controller.BController;
import com.atis.model.AtisIntro;
import com.atis.model.AtisModule;
import com.atis.model.AtisStrobe;
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
public class ModuleController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("queryModule","querySubModule")));
    }

    @At(path = "/module/queryModule", types = {RestRequest.Method.POST})
    public void queryModule() {
        List<AtisModule> atisModules = AtisModule.where(map("parentId",0)).fetch();
        for(AtisModule atisModule:atisModules){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("moduleName",atisModule.attr("moduleName",String.class));
            map.put("id",atisModule.id());
            map.put("enName",atisModule.attr("enName",String.class));
            returnMapList.add(map);
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,token));
    }

    @At(path = "/module/querySubModule", types = {RestRequest.Method.POST})
    public void querySubModule() {
        int moduleId = paramAsInt("moduleId");
        String type = param("type");
        List<AtisModule> atisModules = new ArrayList<AtisModule>();
        if(type==null){
            atisModules = AtisModule.where(map("parentId",moduleId,"type",0)).order("sort DESC").fetch();
        }else{
            atisModules = AtisModule.where(map("parentId",moduleId)).order("sort DESC").fetch();
        }

        List<AtisStrobe> atisStrobeList = AtisStrobe.where("id =1 or id = 25 ").fetch();
        for(AtisModule atisModule:atisModules){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("moduleName",atisModule.attr("moduleName",String.class));
            map.put("id",atisModule.id());
            map.put("enName",atisModule.attr("enName",String.class));
            returnMapList.add(map);
        }
        List<Map<String,Object>> parameterMapList = new ArrayList<Map<String, Object>>();
        for(AtisStrobe atisStrobe:atisStrobeList){
            Map<String,Object> map = new HashMap<String, Object>();
            DecimalFormat    df   = new DecimalFormat("######0.00");
            map.put("turbidity1",df.format(atisStrobe.attr("turbidity1",Float.class)));
            map.put("turbidity2",atisStrobe.attr("turbidity2",Float.class));
            map.put("strobeName",atisStrobe.attr("strobeName",String.class));
            map.put("chlorineion",atisStrobe.attr("chlorineion",Float.class));
            parameterMapList.add(map);
        }
        List<AtisStrobe> atisStrobeList1 = AtisStrobe.where("cod is not null").fetch();
        for(AtisStrobe atisStrobe:atisStrobeList1){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("cod",atisStrobe.attr("cod",Float.class));
            map.put("an",atisStrobe.attr("an",Float.class));
            map.put("strobeName",atisStrobe.attr("strobeName",String.class));
            map.put("phosphorus",atisStrobe.attr("phosphorus",Float.class));
            map.put("isOverproof",0);
            parameterMapList.add(map);
        }
        render(200, OutPutUtil.retunSuccMap(parameterMapList,returnMapList,token));
    }
}
