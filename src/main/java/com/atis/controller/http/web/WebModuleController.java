package com.atis.controller.http.web;

import com.atis.controller.BController;
import com.atis.model.AtisIntro;
import com.atis.model.AtisModule;
import com.atis.model.AtisPump;
import com.atis.model.AtisStrobe;
import com.atis.util.OutPutUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/12/8.
 */
public class WebModuleController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("")));
    }

    @At(path = "/module/webQueryModule", types = {RestRequest.Method.POST})
    public void queryModule() {
        List<AtisModule> atisModules = AtisModule.where(map("parentId",0,"type",1)).order("sort ASC").fetch();
        for(AtisModule atisModule:atisModules){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("moduleName",atisModule.attr("moduleName",String.class));
            map.put("id",atisModule.id());
            map.put("enName",atisModule.attr("enName",String.class));
            map.put("imgUrl",atisModule.attr("imgUrl",String.class));
            returnMapList.add(map);
        }
        AtisIntro atisIntro = AtisIntro.limit(1).single_fetch();
        Map<String,Object> parameterMap =new HashMap<String, Object>();
        parameterMap.put("guideDesc",atisIntro.attr("guideDesc",String.class));
        parameterMap.put("guideVideoUrl",atisIntro.attr("guideVideoUrl",String.class));
        parameterMap.put("totalStrobeNum", AtisStrobe.where(map("isAvailable",1)).count_fetch());
        parameterMap.put("totalPumpNum", 4);
//        parameterMap.put("totalPumpNum", AtisPump.where(map("isAvailable",1)).count_fetch());
        DecimalFormat df   = new DecimalFormat("######0.00");
        AtisStrobe atisStrobe = AtisStrobe.find(25);
        AtisStrobe atisStrobe1 = AtisStrobe.find(1);
        parameterMap.put("turbidity2",df.format(atisStrobe1.attr("turbidity1",Float.class)));
//        parameterMap.put("turbidity2",13.5);
        parameterMap.put("turbidity1",df.format(atisStrobe.attr("turbidity1",Float.class)));
        parameterMap.put("strobeName",atisStrobe.attr("strobeName",String.class));
        parameterMap.put("chlorineion",df.format(atisStrobe.attr("chlorineion",Float.class)));
        parameterMap.put("cod",df.format(atisStrobe.attr("cod",Float.class)));
        parameterMap.put("an",df.format(atisStrobe.attr("an",Float.class)));
        parameterMap.put("strobeName",atisStrobe.attr("strobeName",String.class));
        parameterMap.put("phosphorus",df.format(atisStrobe.attr("phosphorus",Float.class)));
        parameterMap.put("waterLevelGauge",57);
        render(200, OutPutUtil.retunSuccMap(parameterMap,returnMapList,token));
    }

}
