package com.atis.util;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/26.
 */
public class JPushUtil {
    private static String jPushURL="http://www.52umusic.com/jpush/pushMsg?";
    public static String JPushBug = "1";
    public static String JPushDis = "2";
    public static String JPushSys = "3";

    public static void JPush(String JPushType,String fromNickname,String title,
            String userId ,String date) throws Exception{
        Map<String,Object> jpushMap = new HashMap<String, Object>();
        if(JPushType.equals(JPushBug)) {
            jpushMap.put("msg", fromNickname+"提交了巡检异常： "+title+"请尽快处理！");
            jpushMap.put("jPushType", JPushSys);
            jpushMap.put("msgId", date+Math.round(Math.random()*1000));
            jpushMap.put("redirectContent", "");
            Map<String,String> map = new HashMap<String, String>();
            map.put("date",date);
            jpushMap.put("jPushExtra", map);
            jpush(userId, jpushMap);
        }else if(JPushType.equals(JPushDis)){
            jpushMap.put("msg", fromNickname + "分派了巡检异常： "+title+"请尽快处理！");
            jpushMap.put("jPushType", JPushSys);
            jpushMap.put("msgId", date+Math.round(Math.random()*1000));
            jpushMap.put("redirectContent", "");
            Map<String,String> map = new HashMap<String, String>();
            map.put("date",date);
            jpushMap.put("jPushExtra", map);
            jpush(userId, jpushMap);
        }
    }
    private static void jpush(String userId,Map params) throws Exception{
        JSONObject jsonObject = JSONObject.fromObject(params);
        String jsonString = jsonObject.toString();
        String jpushUrl = jPushURL+"userid="+userId+"&"+"content="+jsonString;
        HttpUtil.getReturnData(EmojiUtil.escape(jpushUrl));
    }
}
