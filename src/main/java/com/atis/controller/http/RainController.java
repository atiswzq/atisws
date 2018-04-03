package com.atis.controller.http;

import com.atis.util.ModbusUtil;
import com.atis.util.RainUtil;

import java.util.Date;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/30.
 */
public class RainController implements Runnable{
    static {
        try {
            RainController ctor = (RainController) Class
                    .forName("com.atis.controller.http.RainController").newInstance();
            Thread demo = new Thread(ctor);
            demo.start();
        }catch (Exception e) {
            System.out.println("RainController初始化异常"+e.toString());
        }
    }
    public void run() {
        do{
            try {
                Map<String,Object> map = RainUtil.queryXgyWater();
                ModbusUtil.writeRegister(1,"xgyb","33.73.203.34", 502, 50, "RE,9", map.get("rn").toString());
                Thread.sleep(100000);
            }catch (Exception e) {
                System.out.println("西公园泵水位查询异常"+e.toString());
            }

        }while(1==1);
    }
}
