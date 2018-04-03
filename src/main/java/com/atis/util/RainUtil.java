package com.atis.util;

import com.atis.util.SqlServerUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xshd000 on 2017/2/19.
 */
public class RainUtil {
    public static List<Map<String,Object>> queryRain(){
        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        try {
            Connection conn = SqlServerUtil.getSqlServerConn();
            String sSQL = "select Z ,'乔司监狱' riverName,'120.350654' longitude,'30.351718' latitude from (select top 1 Z from WATER where STCD = 2307 ORDER BY TM DESC) A\n" +
                    "UNION ALL\n" +
                    "select Z ,'白洋桥' riverName,'120.361377' longitude,'30.301582' latitude from (select top 1 Z from WATER where STCD = 1391 ORDER BY TM DESC) B\n" +
                    "UNION ALL\n" +
                    "select Z ,'星河渠' riverName,'120.331801' longitude,'30.313374' latitude from (select top 1 Z from WATER where STCD = 3474 ORDER BY TM DESC) C\n" +
                    "UNION ALL\n" +
                    "select Z ,'临江沪塘河' riverName,'120.325377' longitude,'30.282943' latitude from (select top 1 Z from WATER where STCD = 3478 ORDER BY TM DESC) D\n" +
                    "UNION ALL\n" +
                    "select Z ,'高教西公园泵' riverName,'120.354386' longitude,'30.316567' latitude from (select top 1 Z from WATER where STCD = 9525 ORDER BY TM DESC) E\n" +
                    "UNION ALL\n" +
                    "select 0 ,'幸福闸' riverName,'120.317652' longitude,'30.337025' latitude from (select top 1 Z from WATER where STCD = 2306 ORDER BY TM DESC) F\n" +
                    "UNION ALL\n" +
                    "select Z ,'创业二号闸' riverName,'120.372325' longitude,'30.334214' latitude from (select top 1 Z from WATER where STCD = 3373 ORDER BY TM DESC) G";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sSQL);
            while (rs.next()) {
                Map<String, Object> tmpMapa = new HashMap<String, Object>();
                tmpMapa.put("riverName", rs.getString("riverName"));
                tmpMapa.put("rn", rs.getString("Z"));
                tmpMapa.put("longitude", rs.getString("longitude"));
                tmpMapa.put("latitude", rs.getString("latitude"));
                returnList.add(tmpMapa);
            }
            conn.close();
            return returnList;
        }catch (Exception e){
            Map<String,Object> tmpMap = new HashMap<String,Object>();
            tmpMap.put("riverName","乔司监狱");
            tmpMap.put("rn",0);
            tmpMap.put("longitude","120.350654");
            tmpMap.put("latitude","30.351718");
            returnList.add(tmpMap);
            Map<String,Object> tmpMap1 = new HashMap<String,Object>();
            tmpMap1.put("riverName","白洋桥");
            tmpMap1.put("rn",0);
            tmpMap1.put("longitude","120.361377");
            tmpMap1.put("latitude","30.301582");
            returnList.add(tmpMap1);
            Map<String,Object> tmpMap2 = new HashMap<String,Object>();
            tmpMap2.put("riverName","星河渠");
            tmpMap2.put("rn",0);
            tmpMap2.put("longitude","120.331801");
            tmpMap2.put("latitude","30.313374");
            returnList.add(tmpMap2);
            Map<String,Object> tmpMap3 = new HashMap<String,Object>();
            tmpMap3.put("riverName","临江沪塘河");
            tmpMap3.put("rn",0);
            tmpMap3.put("longitude","120.325377");
            tmpMap3.put("latitude","30.282943");
            returnList.add(tmpMap3);
            Map<String,Object> tmpMap4 = new HashMap<String,Object>();
            tmpMap4.put("riverName","高教西公园泵");
            tmpMap4.put("rn",0);
            tmpMap4.put("longitude","120.354386");
            tmpMap4.put("latitude","30.316567");
            returnList.add(tmpMap4);
            Map<String,Object> tmpMap5 = new HashMap<String,Object>();
            tmpMap5.put("riverName","幸福闸");
            tmpMap5.put("rn",0);
            tmpMap5.put("longitude","120.317652");
            tmpMap5.put("latitude","30.337025");
            returnList.add(tmpMap5);
            Map<String,Object> tmpMap6 = new HashMap<String,Object>();
            tmpMap6.put("riverName","创业二号闸");
            tmpMap6.put("rn",0);
            tmpMap6.put("longitude","120.372325");
            tmpMap6.put("latitude","30.334214");
            returnList.add(tmpMap6);
            return returnList;
        }
    }

    public static Map<String,Object> queryXgyWater() throws Exception{
        Connection conn = SqlServerUtil.getSqlServerConn();
        String sSQL= "select top 1 Z from WATER where STCD = 9525 ORDER BY TM DESC ";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sSQL);
        List<Map<String,Object>> returnList = new ArrayList<Map<String, Object>>();
        while(rs.next()){
            Map<String,Object> tmpMap = new HashMap<String,Object>();
            tmpMap.put("rn",rs.getString("Z"));
//        tmpMap.put("rn","0");
            returnList.add(tmpMap);
        }
        conn.close();
        return (Map<String,Object>)returnList.get(0);
    }
}
