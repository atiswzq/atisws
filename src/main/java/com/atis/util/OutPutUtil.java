package com.atis.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/27.
 */
public class OutPutUtil {
    public static Map retunSuccMap(Object object,Object object1,String token){
        Map map = new HashMap();
        map.put("code","1");
        map.put("msg","成功");
        map.put("token",token);
        if(object instanceof Integer) {
            if(((Integer)object)==0){
                map.put("code","0");
                map.put("msg","网络连接不稳定，请稍后再试！");
            }else if(((Integer)object)==2){
                map.put("code","0");
                map.put("msg","用户已存在！");
            }else if(((Integer)object)==3){
                map.put("code","3");
                map.put("msg","权限不足！");
            }else if(((Integer)object)==4){
                map.put("code","3");
                map.put("msg","用户名或者密码错误");
            }
            return map;
        }
//        if(object instanceof Map) {
//            if(((Map) object).get("info")!=null){
//                map.put("code","4");
//                map.put("msg",((Map) object).get("info"));
//                map.put("parameter",null);
//                map.put("dataset",object1);
//                return  map;
//            }
//        }
        map.put("parameter",object);
        map.put("dataset",object1);
        return map;
//        if(object instanceof java.util.List) {
//            map.put("dataset", object);
//            map.put("parameter",null);
//        }else{
//            map.put("parameter",object);
//            map.put("dataset",null);
//        }
//        return map;
    }
}
