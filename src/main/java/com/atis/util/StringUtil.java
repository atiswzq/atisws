package com.atis.util;

import com.atis.model.AtisModbusLog;
import net.csdn.modules.cache.SerializeUtil;
import redis.clients.jedis.Jedis;
import com.atis.util.RedisClient;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/7/6.
 */
public class StringUtil {
    public static boolean checkEmpty(String string){
        if(string==null||string.equals("")){
            return true;
        }
        return false;
    }

    public static boolean equals(int[] a,int[] b){
        for (int i=0;i!=(a.length<b.length?a.length:b.length);i++)
            if (a[i]!=b[i]) return false;
        return true;
    }

    public static boolean equals(String[] a,String b)
    {
        int j= 0 ;
        for (int i=0;i<a.length;i++){
            if(b.contains(a[i])){
                j++;
            }
        }
        if(j>0) {
            return true;
        }else{
            return false;
        }
    }

    public static int getIntData(RedisClient redisClient, String registerAddress, String strobeId, String subStrobeId)
    {
        return ((AtisModbusLog)redisClient.getObj(registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId))==null?0:
                (int)Float.parseFloat(((AtisModbusLog)redisClient.getObj(registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId)).getData());
    }

    public static float getFloatData(RedisClient redisClient, String registerAddress, String strobeId, String subStrobeId)
    {

        if(((AtisModbusLog)redisClient.getObj(registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId))==null){
            return 0f;
        }else{
            Float f =Float.parseFloat(((AtisModbusLog)redisClient.getObj(registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId)).getData());
            return f;
        }

    }

    public static int getIntData(Jedis jedis, String registerAddress, String strobeId, String subStrobeId)
    {
        byte[] value = jedis.get((registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId).getBytes());
        Object obj = SerializeUtil.unserialize(value);
        return obj==null?0:
                (int)Float.parseFloat(((AtisModbusLog)obj).getData());
    }

    public static float getFloatData(Jedis jedis, String registerAddress, String strobeId, String subStrobeId)
    {
        byte[] value = jedis.get((registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId).getBytes());
        Object obj = SerializeUtil.unserialize(value);
        if(obj==null){
            return 0f;
        }else{
            Float f =Float.parseFloat(((AtisModbusLog)obj).getData());
            return f;
        }

    }

}
