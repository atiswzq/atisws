package com.atis.controller.http.web;
import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.DateUtil;
import com.atis.util.FlowUtil;
import com.atis.util.OutPutUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.collections.map.HashedMap;

import java.util.*;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by xshd000 on 2017/2/19.
 */
public class WebStatisticController extends BController {
    @At(path = "/strobe/queryStrobeOperaStatistic", types = {RestRequest.Method.POST})
    public void queryStrobeOperaStatistic() {
            String datePara = param("date");
            List<AtisStrobe> atisStrobes = AtisStrobe.where(map("isAvailable",1)).fetch();
            for(AtisStrobe atisStrobe : atisStrobes){
                Map<String,Object> returnMap = new HashMap<String,Object>();
                returnMap.put("dateOpenTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createDate",   DateUtil.getDtDate(datePara), "type", 0)).count_fetch()+
                        AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createDate", DateUtil.getDtDate(datePara), "type", 1)).count_fetch());
                returnMap.put("dateOpenSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createDate", DateUtil.getDtDate(datePara), "type", 1)).count_fetch());
                returnMap.put("dateCloseTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createDate", DateUtil.getDtDate(datePara), "type", 2)).count_fetch()+
                        AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createDate", DateUtil.getDtDate(datePara), "type", 3)).count_fetch());
                returnMap.put("dateCloseSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createDate", DateUtil.getDtDate(datePara), "type", 3)).count_fetch());

                returnMap.put("monthOpenTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createMonth", DateUtil.getDtMonth(datePara), "type", 0)).count_fetch()+
                        AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createMonth", DateUtil.getDtMonth(datePara), "type", 1)).count_fetch());
                returnMap.put("monthOpenSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createMonth", DateUtil.getDtMonth(datePara), "type", 1)).count_fetch());
                returnMap.put("monthCloseTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createMonth", DateUtil.getDtMonth(datePara), "type", 2)).count_fetch()+
                        AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createMonth", DateUtil.getDtMonth(datePara), "type", 3)).count_fetch());
                returnMap.put("monthCloseSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createMonth", DateUtil.getDtMonth(datePara), "type", 3)).count_fetch());


                returnMap.put("yearOpenTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createYear", DateUtil.getDtYear(datePara), "type", 0)).count_fetch()+
                        AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createYear", DateUtil.getDtYear(datePara), "type", 1)).count_fetch());
                returnMap.put("yearOpenSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createYear", DateUtil.getDtYear(datePara), "type", 1)).count_fetch());
                returnMap.put("yearCloseTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createYear", DateUtil.getDtYear(datePara), "type", 2)).count_fetch()+
                        AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createYear", DateUtil.getDtYear(datePara), "type", 3)).count_fetch());
                returnMap.put("yearCloseSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisStrobe.id(), "createYear", DateUtil.getDtYear(datePara), "type", 3)).count_fetch());

                returnMap.put("strobeName",atisStrobe.attr("strobeName",String.class));
                returnMapList.add(returnMap);
                }
            render(200, OutPutUtil.retunSuccMap(null,returnMapList,null));
        }

        @At(path = "/strobe/queryWaterLineOperaStatistic", types = {RestRequest.Method.POST})
        public void queryWaterLineOperaStatistic() {
            String datePara = param("date");
            List<AtisWaterLine> atisWaterLines = AtisWaterLine.where("lineDesc is not null").fetch();
            for(AtisWaterLine atisWaterLine : atisWaterLines){
                Map<String,Object> returnMap = new HashMap<String,Object>();
                returnMap.put("dateOpenTotalTimes", AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaDate", DateUtil.getDtDate(datePara), "type", 0)).count_fetch()+
                        AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaDate", DateUtil.getDtDate(datePara), "type", 1)).count_fetch());
                returnMap.put("dateOpenSuccTimes", AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaDate", DateUtil.getDtDate(datePara), "type", 1)).count_fetch());
                if(atisWaterLine.attr("isCurrent",Integer.class)==1) {
                    returnMap.put("dateFlow", Float.parseFloat(FlowUtil.queryTotalSgGgFlow(datePara).get("ggFlow").toString()) + Float.parseFloat(FlowUtil.queryTotalSgPlFlow(datePara).get("plFlow").toString()));
                    returnMap.put("monthFlow", Float.parseFloat(FlowUtil.queryMonthTotalSgGgFlow(datePara).get("ggFlow").toString())+Float.parseFloat(FlowUtil.queryMonthTotalSgPlFlow(datePara).get("plFlow").toString()));
                    returnMap.put("yearFlow", Float.parseFloat(FlowUtil.queryYearTotalSgGgFlow(datePara).get("ggFlow").toString())+Float.parseFloat(FlowUtil.queryYearTotalSgPlFlow(datePara).get("plFlow").toString()));
                }else{
                    returnMap.put("dateFlow", 0);
                    returnMap.put("monthFlow",0);
                    returnMap.put("yearFlow",0);
                }
            returnMap.put("monthOpenTotalTimes", AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaMonth", DateUtil.getDtMonth(datePara), "type", 0)).count_fetch()+
                    AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaMonth", DateUtil.getDtMonth(datePara), "type", 1)).count_fetch());
            returnMap.put("monthOpenSuccTimes", AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaMonth", DateUtil.getDtMonth(datePara), "type", 1)).count_fetch());


            returnMap.put("yearOpenTotalTimes", AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaYear", DateUtil.getDtYear(datePara), "type", 0)).count_fetch()+
                    AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaYear", DateUtil.getDtYear(datePara), "type", 1)).count_fetch());
            returnMap.put("yearOpenSuccTimes", AtisWaterLineLog.where(map("waterLineId", atisWaterLine.id(), "operaYear", DateUtil.getDtYear(datePara), "type", 1)).count_fetch());


            returnMap.put("waterLineName",AtisWaterLine.find(atisWaterLine.attr("parentId",Integer.class))==null?atisWaterLine.attr("lineName",String.class):
                    atisWaterLine.attr("lineName",String.class));
            returnMapList.add(returnMap);
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,null));
    }

    @At(path = "/strobe/queryPumpOperaStatistic", types = {RestRequest.Method.POST})
    public void queryPumpOperaStatistic() {
        String datePara = param("date");
        List<AtisPump> atisPumps = AtisPump.where(map("isOpera",1)).fetch();
        for(AtisPump atisPump : atisPumps){
            Map<String,Object> returnMap = new HashMap<String,Object>();
            returnMap.put("dateOpenTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createDate",  DateUtil.getDtDate(datePara), "type", 0)).count_fetch()+
                    AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createDate",  DateUtil.getDtDate(datePara), "type", 1)).count_fetch());
            returnMap.put("dateOpenSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createDate",  DateUtil.getDtDate(datePara), "type", 1)).count_fetch());
            returnMap.put("dateCloseTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createDate",  DateUtil.getDtDate(datePara), "type", 2)).count_fetch()+
                    AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createDate",  DateUtil.getDtDate(datePara), "type", 3)).count_fetch());
            returnMap.put("dateCloseSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createDate",  DateUtil.getDtDate(datePara), "type", 3)).count_fetch());

            returnMap.put("monthOpenTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createMonth", DateUtil.getDtMonth(datePara), "type", 0)).count_fetch()+
                    AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createMonth", DateUtil.getDtMonth(datePara), "type", 1)).count_fetch());
            returnMap.put("monthOpenSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createMonth", DateUtil.getDtMonth(datePara), "type", 1)).count_fetch());
            returnMap.put("monthCloseTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createMonth", DateUtil.getDtMonth(datePara), "type", 2)).count_fetch()+
                    AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createMonth", DateUtil.getDtMonth(datePara), "type", 3)).count_fetch());
            returnMap.put("monthCloseSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createMonth", DateUtil.getDtMonth(datePara), "type", 3)).count_fetch());


            returnMap.put("yearOpenTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createYear", DateUtil.getDtYear(datePara), "type", 0)).count_fetch()+
                    AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createYear", DateUtil.getDtYear(datePara), "type", 1)).count_fetch());
            returnMap.put("yearOpenSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createYear", DateUtil.getDtYear(datePara), "type", 1)).count_fetch());
            returnMap.put("yearCloseTotalTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createYear", DateUtil.getDtYear(datePara), "type", 2)).count_fetch()+
                    AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createYear", DateUtil.getDtYear(datePara), "type", 3)).count_fetch());
            returnMap.put("yearCloseSuccTimes", AtisStrobeOperaLog.where(map("strobeId", atisPump.attr("strobeId",Integer.class), "createYear", DateUtil.getDtYear(datePara), "type", 3)).count_fetch());

            returnMap.put("strobeName",atisPump.attr("pumpName",String.class));
            returnMapList.add(returnMap);
        }
        render(200, OutPutUtil.retunSuccMap(null,returnMapList,null));
    }

}
