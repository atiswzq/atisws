package com.atis.controller.http.web;

import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.*;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/8.
 */
public class WebWaterLevelController extends BController {
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("")));
    }

    @At(path = "/strobe/webQueryWarnLevel", types = {RestRequest.Method.POST})
    public void queryWarnLevel() {
        List<AtisWarnLevel> atisWarnLevelList = AtisWarnLevel.findAll();
        for(AtisWarnLevel atisWarnLevel : atisWarnLevelList){
            Map<String,Object> returnMap = new HashMap<String, Object>();
            int warnLevelId = atisWarnLevel.id();
            DecimalFormat df   = new DecimalFormat("######0.00");
            //历史最高最低闸前水位
            Map histroyHighMap =AtisWaterLog.nativeSqlClient().defaultMysqlService().query("select strobe_id,water from atis_water_log where strobe_id in (select id from atis_strobe where warn_level_id = ? and is_available = 1) and type = 0 ORDER BY water desc limit 1",warnLevelId).get(0);
            Map histroyLowMap =AtisWaterLog.nativeSqlClient().defaultMysqlService().query("select strobe_id,water from atis_water_log where strobe_id in (select id from atis_strobe where warn_level_id = ? and is_available = 1) and type = 0 and water > 0.5 ORDER BY water asc limit 1",warnLevelId).get(0);
            AtisStrobe histroyHighStrobe = AtisStrobe.find(Integer.parseInt(histroyHighMap.get("strobe_id").toString()));
            AtisStrobe histroyLowStrobe = AtisStrobe.find(Integer.parseInt(histroyLowMap.get("strobe_id").toString()));

            returnMap.put("histroyHighestBeforeLevel",df.format(Float.parseFloat(histroyHighMap.get("water").toString())));
            returnMap.put("histroyHighestName",histroyHighStrobe.attr("strobeName",String.class));

            returnMap.put("histroyLowestBeforeLevel",df.format(Float.parseFloat(histroyLowMap.get("water").toString())));
            returnMap.put("histroyLowestName",histroyLowStrobe.attr("strobeName",String.class));




            //当前最高最低闸前水位
            AtisStrobe highStrobe = AtisStrobe.where(map("warnLevelId",warnLevelId,"isAvailable",1)).order("beforeLevel DESC").limit(1).single_fetch();
            AtisStrobe lowStrobe = AtisStrobe.where(map("warnLevelId",warnLevelId,"isAvailable",1)).where("beforeLevel>0.5").order("beforeLevel ASC").limit(1).single_fetch();

            returnMap.put("currentHighestBeforeLevel",df.format(Float.parseFloat(highStrobe.attr("beforeLevel",Float.class).toString())));
            returnMap.put("currentHighestAfterLevel",df.format(Float.parseFloat(highStrobe.attr("afterLevel",Float.class).toString())));
            returnMap.put("currentHighestName",highStrobe.attr("strobeName",String.class));

//            returnMap.put("currentHighestName",highStrobe.attr("strobeName",String.class).charAt(0)==highStrobe.attr("location",String.class).charAt(0)?
//                    highStrobe.attr("strobeName",String.class):highStrobe.attr("strobeName",String.class)+"("+highStrobe.attr("location",String.class)+")");

            returnMap.put("currentLowestBeforeLevel",df.format(Float.parseFloat(lowStrobe.attr("beforeLevel",Float.class).toString())));
            returnMap.put("currentLowestAfterLevel",df.format(Float.parseFloat(lowStrobe.attr("afterLevel",Float.class).toString())));
            returnMap.put("currentLowestName",lowStrobe.attr("strobeName",String.class));

            String highLevelWarn = atisWarnLevel.attr("highLevel",Float.class).toString();
            String lowLevelWarn = atisWarnLevel.attr("lowLevel",Float.class).toString();

            returnMap.put("highLevelWarnLevel",highLevelWarn);
            returnMap.put("id",warnLevelId);
            returnMap.put("lowLevelWarnLevel",lowLevelWarn);
            returnMap.put("areaName",atisWarnLevel.attr("areaName",String.class));
            returnMapList.add(returnMap);
        }
        render(200,OutPutUtil.retunSuccMap(null,returnMapList,token));
    }

    @At(path = "/strobe/webQueryStrobeWaterLevel", types = {RestRequest.Method.POST})
    public void queryStrobeWaterLevel() {
        List<AtisStrobe> atisStrobeList = AtisStrobe.where(map("isAvailable",1)).order("waterLevelSort ASC").fetch();
        for(AtisStrobe atisStrobe:atisStrobeList){
            float beforeLevel = StringUtil.getFloatData(redisClient,"RE,1",atisStrobe.id().toString(),"1");
            float afterLevel = StringUtil.getFloatData(redisClient,"RE,3",atisStrobe.id().toString(),"1");



            Map<String,Object> map = new HashMap<String, Object>();
            DecimalFormat df   = new DecimalFormat("######0.00");
//            map.put("beforeLevel",atisStrobe.attr("beforeLevel",Float.class).toString());
//            map.put("afterLevel",atisStrobe.attr("afterLevel",Float.class).toString());
            map.put("beforeLevel",df.format(beforeLevel));
            map.put("afterLevel",df.format(afterLevel));

            map.put("strobeName",atisStrobe.attr("strobeName",String.class));
            AtisWarnLevel atisWarnLevel = AtisWarnLevel.find(atisStrobe.attr("warnLevelId",Integer.class));
            //西公园闸水位警戒线超过4米报警
            if(atisStrobe.id()==12) {
                if (beforeLevel >= 4f) {
                    map.put("beforeLevelWarnType", ConstantUtil.warnType);
                }else {
                    map.put("beforeLevelWarnType", ConstantUtil.nromalWarnType);
                }

                if (afterLevel >= 4f) {
                    map.put("afterLevelWarnType", ConstantUtil.warnType);
                }else {
                    map.put("afterLevelWarnType", ConstantUtil.nromalWarnType);
                }

            }else {
                if (beforeLevel >= atisWarnLevel.attr("highLevel", Float.class)) {
                    map.put("beforeLevelWarnType", ConstantUtil.warnType);
                } else if (beforeLevel <= atisWarnLevel.attr("lowLevel", Float.class)) {
                    map.put("beforeLevelWarnType", ConstantUtil.warnType);
                } else {
                    map.put("beforeLevelWarnType", ConstantUtil.nromalWarnType);
                }
                if (afterLevel >= atisWarnLevel.attr("highLevel", Float.class)) {
                    map.put("afterLevelWarnType", ConstantUtil.warnType);
                } else if (afterLevel <= atisWarnLevel.attr("lowLevel", Float.class)) {
                    map.put("afterLevelWarnType", ConstantUtil.warnType);
                } else {
                    map.put("afterLevelWarnType", ConstantUtil.nromalWarnType);
                }
            }
            returnMapList.add(map);
//        List<AtisStrobe> atisStrobeList = AtisStrobe.where(map("isAvailable",1)).order("warnLevelId ASC,sort ASC").fetch();
//        for(AtisStrobe atisStrobe:atisStrobeList){
//            Map<String,Object> map = new HashMap<String, Object>();
//            map.put("beforeLevel",atisStrobe.attr("beforeLevel",Float.class).toString());
//            map.put("afterLevel",atisStrobe.attr("afterLevel",Float.class).toString());
//            map.put("strobeName",atisStrobe.attr("strobeName",String.class).charAt(0)==atisStrobe.attr("location",String.class).charAt(0)?
//                    atisStrobe.attr("strobeName",String.class):atisStrobe.attr("strobeName",String.class)+"|"+atisStrobe.attr("location",String.class));
//            AtisWarnLevel atisWarnLevel = AtisWarnLevel.find(atisStrobe.attr("warnLevelId",Integer.class));
////            西公园闸水位警戒线超过4米报警
//            if(atisStrobe.id()==12) {
//                if (atisStrobe.attr("beforeLevel", Float.class) >= 4f) {
//                    map.put("beforeLevelWarnType", ConstantUtil.warnType);
//                }else {
//                    map.put("beforeLevelWarnType", ConstantUtil.nromalWarnType);
//                }
//
//                if (atisStrobe.attr("afterLevel", Float.class) >= 4f) {
//                    map.put("afterLevelWarnType", ConstantUtil.warnType);
//                }else {
//                    map.put("afterLevelWarnType", ConstantUtil.nromalWarnType);
//                }
//
//            }else {
//                if (atisStrobe.attr("beforeLevel", Float.class) >= atisWarnLevel.attr("highLevel", Float.class)) {
//                    map.put("beforeLevelWarnType", ConstantUtil.warnType);
//                } else if (atisStrobe.attr("beforeLevel", Float.class) <= atisWarnLevel.attr("lowLevel", Float.class)) {
//                    map.put("beforeLevelWarnType", ConstantUtil.warnType);
//                } else {
//                    map.put("beforeLevelWarnType", ConstantUtil.nromalWarnType);
//                }
//                if (atisStrobe.attr("afterLevel", Float.class) >= atisWarnLevel.attr("highLevel", Float.class)) {
//                    map.put("afterLevelWarnType", ConstantUtil.warnType);
//                } else if (atisStrobe.attr("afterLevel", Float.class) <= atisWarnLevel.attr("lowLevel", Float.class)) {
//                    map.put("afterLevelWarnType", ConstantUtil.warnType);
//                } else {
//                    map.put("afterLevelWarnType", ConstantUtil.nromalWarnType);
//                }
//            }
//            returnMapList.add(map);
        }
        //850排涝闸水位
        AtisStrobe atisStrobe = AtisStrobe.find(26);
        Map<String ,Object> plStrobeMap = new HashMap<String,Object>();
        plStrobeMap.put("beforeLevel",atisStrobe.attr("beforeLevel",Float.class).toString());
        plStrobeMap.put("afterLevel",atisStrobe.attr("afterLevel",Float.class).toString());
        plStrobeMap.put("strobeName",atisStrobe.attr("strobeName",String.class));
        plStrobeMap.put("beforeLevelWarnType", ConstantUtil.nromalWarnType);
        returnMapList.add(plStrobeMap);


        Map<String,Object> parameterMap = new HashMap<String,Object>();
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
        parameterMap.put("rainList", RainUtil.queryRain());
        render(200,OutPutUtil.retunSuccMap(parameterMap,returnMapList,token));
    }
}
