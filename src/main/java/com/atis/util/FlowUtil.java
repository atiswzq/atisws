package com.atis.util;

import com.atis.model.AtisPumpLogSg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xshd000 on 2017/3/30.
 */
public class FlowUtil {
    public static Map<String,Object> querySgGgFlow(int id,String date){
        Map map = new HashMap();
        date = DateUtil.getDtDate(date);
        if(id == 1 ) {
             map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) ggRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 and pump_name ='四格一号水泵'" +
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 2){
             map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) ggRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 and pump_name ='四格二号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 3 ){
             map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) ggRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 and pump_name ='四格三号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id ==4){
             map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) ggRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 and pump_name ='四格四号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 5){
             map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) ggRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 and pump_name ='四格五号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 6){
             map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) ggRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 and pump_name ='四格六号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }
        return map;
    }

    public static Map<String,Object> querySgPlFlow(int id,String date){
        Map map = new HashMap();
        date = DateUtil.getDtDate(date);
        if(id == 1 ) {
            map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 and pump_name ='四格一号水泵'" +
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 2){
            map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 and pump_name ='四格二号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 3 ){
            map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 and pump_name ='四格三号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id ==4){
            map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 and pump_name ='四格四号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 5){
            map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 and pump_name ='四格五号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }else if(id == 6){
            map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 and pump_name ='四格六号水泵'"+
                    "and insert_time like '"+date+"%' and pump_status =1");
            return map;
        }
        return map;
    }


    public static Map<String,Object> queryTotalSgGgFlow(String date){
        Map map = new HashMap();
        date = DateUtil.getDtDate(date);
        map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 "+
                "and insert_time like '"+date+"%' and pump_status =1");
        return map;
    }

    public static Map<String,Object> queryTotalSgPlFlow(String date){
        Map map = new HashMap();
        date = DateUtil.getDtDate(date);
        map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 "+
                "and insert_time like '"+date+"%' and pump_status =1");
        return map;
    }


    public static Map<String,Object> queryMonthTotalSgGgFlow(String date){
        Map map = new HashMap();
        date = DateUtil.getDtMonth(date);
        map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 "+
                "and insert_time like '"+date+"%' and pump_status =1");
        return map;
    }

    public static Map<String,Object> queryMonthTotalSgPlFlow(String date){
        Map map = new HashMap();
        date = DateUtil.getDtMonth(date);
        map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 "+
                "and insert_time like '"+date+"%' and pump_status =1");
        return map;
    }

    public static Map<String,Object> queryYearTotalSgGgFlow(String date){
        Map map = new HashMap();
        date = DateUtil.getDtYear(date);
        map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 ggFlow from atis_pump_log_sg where pump_mode = 1 "+
                "and insert_time like '"+date+"%' and pump_status =1");
        return map;
    }

    public static Map<String,Object> queryYearTotalSgPlFlow(String date){
        Map map = new HashMap();
        date = DateUtil.getDtYear(date);
        map = AtisPumpLogSg.nativeSqlClient().defaultMysqlService().single_query("select count(1) plRunTime,count(1)*60*6.3 plFlow from atis_pump_log_sg where pump_mode = 0 "+
                "and insert_time like '"+date+"%' and pump_status =1");
        return map;
    }

}
