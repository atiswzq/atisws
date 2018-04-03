package com.atis.controller.http;


import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.*;

import com.atis.util.RedisClient;
import com.dn9x.modbus.controller.IController;
import com.dn9x.modbus.entity.ControllerEntity;
import com.dn9x.modbus.entity.RegisterEntity;
import com.dn9x.modbus.util.Constant;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.cache.*;
import net.csdn.modules.cache.SerializeUtil;
import net.csdn.modules.http.RestRequest;
import redis.clients.jedis.Jedis;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Kevin on 2016/11/14.
 */
public class ModbusController extends BController implements IController, Runnable {

    private ControllerEntity ctorEntity;
    private RedisClient redisClient = new RedisClient(settings);

    static {
        UnityModbus um = new UnityModbus();
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("strobeOpera","pumpOpera")));
        beforeFilter("getUserId", WowCollections.map(only, WowCollections.list("strobeOpera","pumpOpera")));
    }
    public void showTest() {

    }

    @At(path = "/modbus/strobeOpera", types = {RestRequest.Method.POST})
    public void strobeOpera() {
        Map  parameterMap= new HashMap();
        parameterMap.put("info","操作成功,请勿重复点击");
        int strobeId = paramAsInt("strobeId");
        String value = param("value");
        int type = paramAsInt("type");
        int subStrobeId = paramAsInt("subStrobeId");
        AtisStrobeModbusConfig  atisStrobeModbusConfig =AtisStrobeModbusConfig.where(map("strobeId",strobeId,"type",type,"subStrobeId",subStrobeId)).single_fetch();
        try {
            if(type==6){
                AtisStrobeModbusConfig gatageConfig = AtisStrobeModbusConfig.where(map("strobeId", strobeId, "type", 5, "subStrobeId", subStrobeId)).single_fetch();
                AtisStrobeModbusConfig gatageOpera = AtisStrobeModbusConfig.where(map("strobeId", strobeId, "type", 6, "subStrobeId", subStrobeId)).single_fetch();
                setRegisterValue(gatageConfig.attr("name", String.class), gatageConfig.attr("ip", String.class), gatageConfig.attr("port", Integer.class),
                        gatageConfig.attr("controllerId", Integer.class),
                        gatageConfig.attr("registerAddress", String.class), value);
                setDigitalValue(gatageOpera.attr("name", String.class), gatageOpera.attr("ip", String.class), gatageOpera.attr("port", Integer.class),
                        gatageOpera.attr("controllerId", Integer.class),
                        gatageOpera.attr("registerAddress", String.class), "1");
                if(gatageOpera.attr("controllerId",Integer.class)==19){
                    setDigitalValue(gatageOpera.attr("name", String.class), gatageOpera.attr("ip", String.class), gatageOpera.attr("port", Integer.class),
                            gatageOpera.attr("controllerId", Integer.class),
                            "DO,21", "1");
                }
            }else {
                if (atisStrobeModbusConfig.attr("registerAddress", String.class).contains("DO")) {
                    setDigitalValue(atisStrobeModbusConfig.attr("name", String.class), atisStrobeModbusConfig.attr("ip", String.class), atisStrobeModbusConfig.attr("port", Integer.class),
                            atisStrobeModbusConfig.attr("controllerId", Integer.class),
                            atisStrobeModbusConfig.attr("registerAddress", String.class), value);
                } else if (atisStrobeModbusConfig.attr("registerAddress", String.class).contains("RE")) {
                    setRegisterValue(atisStrobeModbusConfig.attr("name", String.class), atisStrobeModbusConfig.attr("ip", String.class), atisStrobeModbusConfig.attr("port", Integer.class),
                            atisStrobeModbusConfig.attr("controllerId", Integer.class),
                            atisStrobeModbusConfig.attr("registerAddress", String.class), value);
                }
            }
        }catch(Exception e){
            parameterMap.put("info",AtisStrobe.find(strobeId).attr("strobeName",String.class)+" PLC异常,请检查");
            render(200, OutPutUtil.retunSuccMap(parameterMap, null, token));
        }
        render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
    }

    @At(path = "/modbus/pumpOpera", types = {RestRequest.Method.POST})
    public void pumpOpera() {
        Map  parameterMap= new HashMap();
        parameterMap.put("info","操作成功,请勿重复点击");
        int strobeId = paramAsInt("strobeId");
        String value = param("value");
        int type = paramAsInt("type");
        int subStrobeId = paramAsInt("subStrobeId");
        AtisStrobeModbusConfig  atisStrobeModbusConfig =AtisStrobeModbusConfig.where(map("strobeId",strobeId,"type",type,"subStrobeId",subStrobeId)).single_fetch();
        try {
            if(type==12||type==13||type==14||type==15){
                setRegisterValue(atisStrobeModbusConfig.attr("name", String.class), atisStrobeModbusConfig.attr("ip", String.class), atisStrobeModbusConfig.attr("port", Integer.class),
                        atisStrobeModbusConfig.attr("controllerId", Integer.class),
                        atisStrobeModbusConfig.attr("registerAddress", String.class), value);
            }else {
                setDigitalValue(atisStrobeModbusConfig.attr("name", String.class), atisStrobeModbusConfig.attr("ip", String.class), atisStrobeModbusConfig.attr("port", Integer.class),
                        atisStrobeModbusConfig.attr("controllerId", Integer.class),
                        atisStrobeModbusConfig.attr("registerAddress", String.class), value);
            }
        }catch(Exception e){
            parameterMap.put("info","西公园泵异常,请检查");
            render(200, OutPutUtil.retunSuccMap(parameterMap, null, token));
        }
        render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));
    }

    public void setDigitalValue(String name,String ip, int port, int slaveId, String address,
                                String value) throws Exception{
        ModbusUtil.writeDigitalOutput(userId,name,ip, port, slaveId, address, value);
    }

    public void setRegisterValue(String name,String ip, int port, int slaveId, String address,
                                 String value) throws Exception{
        ModbusUtil.writeRegister(userId,name,ip, port, slaveId, address, value);
    }

    public void setValue(Map<Integer, String> data) throws Exception {

        Map<String, RegisterEntity> map = this.ctorEntity.getRegisters();

        for (String key : map.keySet()) {
            for (int key2 : data.keySet()) {
                // 如果这个address已经存在，那就先覆盖本地，再写入到真机上面
                if (Integer.parseInt(key.split(",")[1]) == key2) {
                    // 只处理类型是读写的，只读的不处理
                    if (map.get(key).getType().equals("DO")
                            || map.get(key).getType().equals("RE")) {
                        map.get(key).setValue(data.get(key2));
                    } else {
                        if (Constant.ENV == "local") {
                            throw new Exception("只能对写入类型的寄存器写入值");
                        } else {
                            System.err.println("只能对写入类型的寄存器写入值");
                        }
                    }
                }
            }
        }

        // 覆盖完成之后，写入到真机，循环写入
        for (String key : map.keySet()) {
            if (map.get(key).getType().equals("DO")) {
                setDigitalValue(this.ctorEntity.getName(),this.ctorEntity.getIp(),
                        this.ctorEntity.getPort(), this.ctorEntity.getId(), map
                                .get(key).getAddress(), map.get(key).getValue());
            } else if (map.get(key).getType().equals("RE")) {
                setRegisterValue(this.ctorEntity.getName(),this.ctorEntity.getIp(),
                        this.ctorEntity.getPort(), this.ctorEntity.getId(), map
                                .get(key).getAddress(), map.get(key).getValue());
            }
        }

    }

    public void setValue(String address, String value) throws Exception {

        Map<String, RegisterEntity> map = this.ctorEntity.getRegisters();

        for (String key : map.keySet()) {
            if (key == address) {
                if (map.get(key).getType().equals("DO")) {
                    map.get(key).setValue(value);
                    setDigitalValue(this.ctorEntity.getName(),this.ctorEntity.getIp(),
                            this.ctorEntity.getPort(), this.ctorEntity.getId(),
                            address, value);
                } else if (map.get(key).getType().equals("RE")) {
                    map.get(key).setValue(value);
                    setRegisterValue(this.ctorEntity.getName(),this.ctorEntity.getIp(),
                            this.ctorEntity.getPort(), this.ctorEntity.getId(),
                            address, value);
                } else {
                    if (Constant.ENV == "local") {
                        throw new Exception("只能对写入类型的寄存器写入值");
                    } else {
                        System.err.println("只能对写入类型的寄存器写入值");
                    }
                }
            }
        }
    }

    public void run() {
        int i = 0;
        //一直循环
        do {
            try {
                if (this.ctorEntity != null
                        && this.ctorEntity.getRegisters().size() > 0) {

                    boolean ctroChange = false;

                    for (String key : this.ctorEntity.getRegisters().keySet()) {
                        String data = "0";
                        String type = this.ctorEntity.getRegisters().get(key)
                                .getType().toUpperCase();
                        RegisterEntity re = this.ctorEntity.getRegisters().get(key);



                        if (type.equals("DI")) {
                            data = ModbusUtil.readDigitalInput(
                                    this.ctorEntity.getIp(),
                                    this.ctorEntity.getPort(), re.getAddress(),
                                    this.ctorEntity.getId());
                        } else if (type.toUpperCase().equals("DO")) {
                            data = ModbusUtil.readDigitalOutput(this.ctorEntity.getName(),
                                    this.ctorEntity.getIp(),
                                    this.ctorEntity.getPort(), re.getAddress(),
                                    this.ctorEntity.getId());
                        } else if (type.toUpperCase().equals("IR")) {
                            data = ModbusUtil.readInputRegister(this.ctorEntity.getName(), this.ctorEntity.getIp(),
                                    this.ctorEntity.getPort(), re.getAddress(),
                                    this.ctorEntity.getId());
                        } else if (type.toUpperCase().equals("RE")){
                            data = ModbusUtil.readRegister(this.ctorEntity.getName(), this.ctorEntity.getIp(),
                                    this.ctorEntity.getPort(), re.getAddress(),
                                    this.ctorEntity.getId());
                        }

                        // 说明变化了
                        if (!re.getValue().equals(data)) {

                            // 改变掉值
                            re.setValue(data);

                            // 通知

                            sendControllerRegisterStateChangedNotification(
                                    this.ctorEntity.getId(),
                                    this.ctorEntity.getRegisters().get(key)
                                            .getAddress(),
                                    this.ctorEntity.getIp(),
                                    this.ctorEntity.getPort(), data, this.ctorEntity.getRegisters().get(key)
                                            .getStrobeId(), this.ctorEntity.getRegisters().get(key)
                                            .getSubStrobeId());

                            // 外围也要通知
                            ctroChange = true;
                        }
                    }

                    if (ctroChange) {
//                    AtisModbusLog.getJPAContext().em().getTransaction().commit();
//                    AtisModbusLog.getJPAContext().em().getTransaction().begin();
                        sendControllerStatesChangedNotification(this.ctorEntity
                                .getId());
                    }
                }


                //这里每个controller为一个单位，一次完整的检测之后暂停一秒
                Thread.sleep(1000);
            } catch (java.net.ConnectException e1){
                AtisModbusLog atisModbusLog = new AtisModbusLog();
                atisModbusLog.setStrobeId(this.ctorEntity.getId());
                atisModbusLog.setSubStrobeId(1);
                atisModbusLog.setControllerId(this.ctorEntity.getId());
                atisModbusLog.setCreateDate(System.currentTimeMillis() / 1000);
                atisModbusLog.setCreatedBy(userId);
                atisModbusLog.setData("0");
                atisModbusLog.setIp(this.ctorEntity.getIp());
                atisModbusLog.setPort(this.ctorEntity.getPort());
                atisModbusLog.setRegisterAddress("DO,5");
                atisModbusLog.setModifyDate(System.currentTimeMillis() / 1000);
//        RedisClient redisClient = new RedisClient(settings);
//        redisClient.setObject(atisModbusLog,registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId);
                Jedis jedis = redisClient.borrow();
                jedis.set(("DO,5"+ConstantUtil.separator+this.ctorEntity.getId()+ConstantUtil.separator+"1").getBytes(), net.csdn.modules.cache.SerializeUtil.serialize(atisModbusLog));
//        redisClient.revert(jedis);
                redisClient.revert(jedis);


                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + this.ctorEntity.getId() + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", this.ctorEntity.getId());
                logger.info("运行update strobe 的id 为 "+this.ctorEntity.getId());
            } catch (net.wimpi.modbus.ModbusSlaveException e) {
                    logger.info("PLC线程" + Thread.currentThread().getName() + "异常 ip:" + this.ctorEntity.getIp() + " Controller Id为" + this.ctorEntity.getId() + e.toString());
                    System.out.println("PLC线程" + Thread.currentThread().getName() + "异常 ip:" + this.ctorEntity.getIp() + " Controller Id为" + this.ctorEntity.getId() + e.toString());
                    System.out.println(this.redisClient);
                    redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + this.ctorEntity.getId() + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", this.ctorEntity.getId());
                    logger.info(e.toString());
                    logger.info(" Controller Id为" + this.ctorEntity.getId() + "redisClient ===>" + this.redisClient);
                    e.printStackTrace();
            }catch(java.lang.Exception e2){
                logger.info("java.lang.Exception e2");
            }

        } while (1 == 1);
    }

    public ControllerEntity getCtorEntity() {
        return ctorEntity;
    }

    public void setCtorEntity(ControllerEntity ctorEntity) {
        this.ctorEntity = ctorEntity;
    }

    private void sendControllerRegisterStateChangedNotification(int controllerID, String registerAddress,String ip,int port,String data,int strobeId,int subStrobeId){
        if(redisClient==null){
            redisClient = new RedisClient(settings);
            logger.info("new RedisClient");
        }
        if(registerAddress .equals("DO,5")){
            System.out.println("2222");
        }
        AtisModbusLog atisModbusLog = new AtisModbusLog();
        atisModbusLog.setStrobeId(strobeId);
        atisModbusLog.setSubStrobeId(subStrobeId);
        atisModbusLog.setControllerId(controllerID);
        atisModbusLog.setCreateDate(System.currentTimeMillis() / 1000);
        atisModbusLog.setCreatedBy(userId);
        atisModbusLog.setData(data);
        atisModbusLog.setIp(ip);
        atisModbusLog.setPort(port);
        atisModbusLog.setRegisterAddress(registerAddress);
        atisModbusLog.setModifyDate(System.currentTimeMillis() / 1000);
//        RedisClient redisClient = new RedisClient(settings);
//        redisClient.setObject(atisModbusLog,registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId);
        Jedis jedis = redisClient.borrow();
        jedis.set((registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId).getBytes(), net.csdn.modules.cache.SerializeUtil.serialize(atisModbusLog));
//        redisClient.revert(jedis);
        initStrobeInfo(strobeId,subStrobeId,data,registerAddress,jedis);
        redisClient.revert(jedis);
    }

    private void initStrobeInfo( int strobeId,int subStrobeId,String data,String registerAddress,Jedis redisClient) {
        if(strobeId==50){
            if (registerAddress.equals("RE,1")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_delay_time =  ? where id = 9", data);
            }else if (registerAddress.equals("RE,3")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_delay_time =  ? where id = 10", data);
            }else if (registerAddress.equals("RE,5")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_delay_time =  ? where id = 17", data);
            }else if (registerAddress.equals("RE,7")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_delay_time =  ? where id = 18", data);
            }else if (registerAddress.equals("RE,13")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set open_water =  ? where id = 9", data);
            }else if (registerAddress.equals("RE,15")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set open_water =  ? where id = 10", data);
            }else if (registerAddress.equals("RE,17")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set open_water =  ? where id = 17", data);
            }else if (registerAddress.equals("RE,19")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set open_water =  ? where id = 18", data);
            }else if (registerAddress.equals("RE,21")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_water =  ? where id = 9", data);
            }else if (registerAddress.equals("RE,23")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_water =  ? where id = 10", data);
            }else if (registerAddress.equals("RE,25")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_water =  ? where id = 17", data);
            }else if (registerAddress.equals("RE,27")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_water =  ? where id = 18", data);
            }else if (registerAddress.equals("DO,0")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_remote =  ? where id = 9", data);
            }else if (registerAddress.equals("DO,6")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_remote =  ? where id = 10", data);
            }else if (registerAddress.equals("DO,12")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_remote =  ? where id = 17", data);
            }else if (registerAddress.equals("DO,18")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_remote =  ? where id = 18", data);
            }else if (registerAddress.equals("DO,5")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_running =  ? where id = 9", data);
            }else if (registerAddress.equals("DO,11")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_running =  ? where id = 10", data);
            }else if (registerAddress.equals("DO,17")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_running =  ? where id = 17", data);
            }else if (registerAddress.equals("DO,23")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_running =  ? where id = 18", data);
            }else if (registerAddress.equals("RE,9")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set current_water =  ? where parent_id = 3", data);
            }else if (registerAddress.equals("RE,30")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set currenta =  ? where id = 9", data);
            }else if (registerAddress.equals("RE,32")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set currenta =  ? where id = 10", data);
            }else if (registerAddress.equals("RE,34")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set currenta =  ? where id = 17", data);
            }else if (registerAddress.equals("RE,36")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set currenta =  ? where id = 18", data);
            }else if (registerAddress.equals("RE,38")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set voltageab =  ? where parent_id = 3", data);
            }else if (registerAddress.equals("RE,40")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set voltagebc =  ? where parent_id = 3", data);
            }else if (registerAddress.equals("RE,42")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set voltageca =  ? where parent_id = 3", data);
            }else if(registerAddress.equals("DO,38")){
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_auto =  ? where id = 9", data);
            }else if(registerAddress.equals("DO,39")){
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_auto =  ? where id = 10", data);
            }else if(registerAddress.equals("DO,40")){
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_auto =  ? where id = 17", data);
            }else if(registerAddress.equals("DO,41")){
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_auto =  ? where id = 18", data);
            }
            //高教东渠
        }else if(strobeId==51) {
            if (registerAddress.equals("RE,1")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_delay_time =  ? where id = 7", data);
            }else if (registerAddress.equals("RE,3")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_delay_time =  ? where id = 8", data);
            }else if (registerAddress.equals("RE,13")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set open_water =  ? where id = 7", data);
            }else if (registerAddress.equals("RE,15")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set open_water =  ? where id = 8", data);
            }else if (registerAddress.equals("RE,21")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_water =  ? where id = 7", data);
            }else if (registerAddress.equals("RE,23")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set close_water =  ? where id = 8", data);
            }else if (registerAddress.equals("DO,0")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_remote =  ? where id = 7", data);
            }else if (registerAddress.equals("DO,6")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_remote =  ? where id = 8", data);
            }else if (registerAddress.equals("DO,5")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_running =  ? where id = 7", data);
            }else if (registerAddress.equals("DO,11")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_running =  ? where id = 8", data);
            }else if (registerAddress.equals("RE,9")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set current_water =  ? where parent_id = 4", data);
            }else if (registerAddress.equals("RE,30")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set currenta =  ? where id = 7", data);
            }else if (registerAddress.equals("RE,32")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set currenta =  ? where id = 8", data);
            }else if (registerAddress.equals("RE,38")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set voltageab =  ? where parent_id = 4", data);
            }else if (registerAddress.equals("RE,40")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set voltagebc =  ? where parent_id = 4", data);
            }else if (registerAddress.equals("RE,42")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set voltageca =  ? where parent_id = 4", data);
            }else if(registerAddress.equals("DO,38")){
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_auto =  ? where id = 7", data);
            }else if(registerAddress.equals("DO,39")) {
                AtisPumpCurrent.nativeSqlClient().defaultMysqlService().execute("update atis_pump_current set is_auto =  ? where id = 8", data);
            }
        }else {
            int strobeNum = Integer.parseInt(redisClient.get(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.strobeNum).toString());
//            Map atisStrobe = AtisStrobe.nativeSqlClient().single_query("select * from atis_strobe where id = ?", strobeId);
//            int strobeNum = Integer.parseInt(atisStrobe.g et("strobe_num").toString());
            //.attr("strobeNum",Integer.class);
            if (registerAddress.equals("RE,1")) {
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set before_level =  ? where id = ?", Float.parseFloat(data), strobeId);
                int count = Integer.parseInt(AtisStrobe.nativeSqlClient().defaultMysqlService().query("select count(1) count from atis_water_log where strobe_id = ? and type =?",strobeId,0).get(0).get("count").toString());
                if(count>0){
                    Map atisWaterLogMap = AtisStrobe.nativeSqlClient().defaultMysqlService().query("select water from atis_water_log where strobe_id = ? and type = ? order by create_date DESC limit 1",strobeId,0).get(0);
                    if(Float.parseFloat(atisWaterLogMap.get("water").toString())-Float.parseFloat(data)> ConstantUtil.waterTrigger||Float.parseFloat(data)-Float.parseFloat(atisWaterLogMap.get("water").toString())>ConstantUtil.waterTrigger){
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("insert into atis_water_log(strobe_id,water,type,create_date,modify_date) values(?,?,?,?,?)",
                                strobeId,Float.parseFloat(data),0, System.currentTimeMillis()/1000,System.currentTimeMillis()/1000);
                    }
                }else {
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("insert into atis_water_log(strobe_id,water,type,create_date,modify_date) values(?,?,?,?,?)",
                            strobeId,Float.parseFloat(data),0, System.currentTimeMillis()/1000,System.currentTimeMillis()/1000);
                }


            } else if (registerAddress.equals("RE,3")) {
                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set after_level =  ? where id = ?", Float.parseFloat(data), strobeId);
                int count = Integer.parseInt(AtisStrobe.nativeSqlClient().defaultMysqlService().query("select count(1) count from atis_water_log where strobe_id = ? and type =?",strobeId,1).get(0).get("count").toString());
                if(count>0){
                    Map atisWaterLogMap = AtisStrobe.nativeSqlClient().defaultMysqlService().query("select water from atis_water_log where strobe_id = ? and type = ? order by create_date DESC limit 1",strobeId,1).get(0);
                    if(Float.parseFloat(atisWaterLogMap.get("water").toString())-Float.parseFloat(data)> ConstantUtil.waterTrigger||Float.parseFloat(data)-Float.parseFloat(atisWaterLogMap.get("water").toString())>ConstantUtil.waterTrigger){
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("insert into atis_water_log(strobe_id,water,type,create_date,modify_date) values(?,?,?,?,?)",
                                strobeId,Float.parseFloat(data),1, System.currentTimeMillis()/1000,System.currentTimeMillis()/1000);
                    }
                }else {
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("insert into atis_water_log(strobe_id,water,type,create_date,modify_date) values(?,?,?,?,?)",
                            strobeId,Float.parseFloat(data),1, System.currentTimeMillis()/1000,System.currentTimeMillis()/1000);
                }
            };
            if (strobeNum == 1) {
//                if (registerAddress.equals("DO,5")) {
//                    redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
//                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);
//                }else
                  if (registerAddress.equals("RE,5")) {
                    redisClient.set(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.gatage,subStrobeId + "," + (int) Float.parseFloat(data));
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set gatage =  ? where id = ?", subStrobeId + "," + (int) Float.parseFloat(data), strobeId);
                    if (Float.parseFloat(data) >= 10f) {
//                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
//                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);

                        int is_remote = getIntData(redisClient, "DO,5", String.valueOf(strobeId), String.valueOf(subStrobeId));
                        if(is_remote ==1) {
                            redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
                            AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);
                        }else{
                            redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                            AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", strobeId);
                        }
                    } else{
//                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
//                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);

                        int is_remote = getIntData(redisClient, "DO,5", String.valueOf(strobeId), String.valueOf(subStrobeId));
                        if(is_remote ==1) {
                            redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
                            AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);
                        }else{
                            redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                            AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", strobeId);
                        }

                    }
                } else if (registerAddress.equals("DO,8")) {
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set is_opening =  ? where id = ?", data, strobeId);
                } else if (registerAddress.equals("DO,9")) {
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set is_closing =  ? where id = ?", data, strobeId);
                } else if (registerAddress.equals("RE,17")) {
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set turbidity1 =  ? where id = ?", data, strobeId);
                } else if (registerAddress.equals("RE,19")) {
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set chlorineion =  ? where id = ?", data, strobeId);
                }else if(registerAddress.equals("DO,5")&&Integer.parseInt(data)==0){
                    redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + this.ctorEntity.getId() + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                    AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", this.ctorEntity.getId());
                }else if(registerAddress.equals("DO,5")&&Integer.parseInt(data)==1){
                      float gatage = getFloatData(redisClient, "RE,5", String.valueOf(strobeId), String.valueOf(subStrobeId));
                      if(gatage>=10f){
                          redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
                          AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);
                      }else{
                          redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
                          AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);
                      }
                }


            } else {
                if (subStrobeId == 1) {
                    if (registerAddress.equals("RE,5")) {
                        String gatage = redisClient.get(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.gatage).toString();
//                        String gatage = atisStrobe.get("gatage").toString();
                        String[] gatageArray = gatage.split("@");
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set gatage =  ? where id = ?", subStrobeId + "," + (int) Float.parseFloat(data) + "@" + gatageArray[1], strobeId);
                        redisClient.set(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.gatage,subStrobeId + "," + (int) Float.parseFloat(data) + "@" + gatageArray[1]);
                        if (Float.parseFloat(data) >= 10f) {
//                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
//                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);

                            int is_remote = getIntData(redisClient, "DO,5", String.valueOf(strobeId), String.valueOf(subStrobeId));
                            if(is_remote ==1) {
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);
                            }else{
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", strobeId);
                            }
                        } else{
//                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
//                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);

                            int is_remote = getIntData(redisClient, "DO,5", String.valueOf(strobeId), String.valueOf(subStrobeId));
                            if(is_remote ==1) {
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);
                            }else{
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", strobeId);
                            }

                        }
                    } else if (registerAddress.equals("DO,8")) {
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set is_opening =  ? where id = ?", data, strobeId);
                    } else if (registerAddress.equals("DO,9")) {
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set is_closing =  ? where id = ?", data, strobeId);
                    }else if(registerAddress.equals("DO,5")&&Integer.parseInt(data)==0){
                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + this.ctorEntity.getId() + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", this.ctorEntity.getId());
                    }else if(registerAddress.equals("DO,5")&&Integer.parseInt(data)==1){
                        float gatage = getFloatData(redisClient, "RE,5", String.valueOf(strobeId), String.valueOf(subStrobeId));
                        if(gatage>=10f){
                            redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
                            AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);
                        }else{
                            redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
                            AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);
                        }
                    }
                } else if (subStrobeId == 2) {
                    if (registerAddress.equals("RE,7")) {
                        String gatage = redisClient.get(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.gatage).toString();
//                        String gatage = atisStrobe.get("gatage").toString();
                        String[] gatageArray = gatage.split("@");
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set gatage =  ? where id = ?", gatageArray[0] + "@" + subStrobeId + "," + (int) Float.parseFloat(data), strobeId);
                        redisClient.set(ConstantUtil.strobeId+ConstantUtil.separator+strobeId+ConstantUtil.separator+ConstantUtil.gatage,gatageArray[0] + "@" + subStrobeId + "," + (int) Float.parseFloat(data));
                        if (Float.parseFloat(data) >= 10f) {
//                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
//                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);

                            int is_remote = getIntData(redisClient, "DO,20", String.valueOf(strobeId), String.valueOf(subStrobeId));
                            if(is_remote ==1) {
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "1");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  1 where id = ?", strobeId);
                            }else{
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", strobeId);
                            }
                        } else{
//                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
//                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);

                            int is_remote = getIntData(redisClient, "DO,20", String.valueOf(strobeId), String.valueOf(subStrobeId));
                            if(is_remote ==1) {
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "0");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  0 where id = ?", strobeId);
                            }else{
                                redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + strobeId + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                                AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", strobeId);
                            }

                        }
                    } else if (registerAddress.equals("DO,23")) {
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set is_opening =  ? where id = ?", data, strobeId);
                    } else if (registerAddress.equals("DO,24")) {
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set is_closing =  ? where id = ?", data, strobeId);
                    }else if(registerAddress.equals("DO,20")&&Integer.parseInt(data)==0){
                        redisClient.set(ConstantUtil.strobeId + ConstantUtil.separator + this.ctorEntity.getId() + ConstantUtil.separator + ConstantUtil.strobeStatus, "2");
                        AtisStrobe.nativeSqlClient().defaultMysqlService().execute("update atis_strobe set strobe_status =  2 where id = ?", this.ctorEntity.getId());
                    }
                }
            }
        }
    }


    /**
     * 检测到有多个寄存器的值变化的时候就调用这个方法
     * @param controllerID
     */
    private void sendControllerStatesChangedNotification(int controllerID){
//        System.out.println(">>>>>>>controllID:" + controllerID + " 里面有值改变了");
    }

    public int getIntData(Jedis jedis, String registerAddress, String strobeId, String subStrobeId)
    {
        byte[] value = jedis.get((registerAddress+ConstantUtil.separator+strobeId+ConstantUtil.separator+subStrobeId).getBytes());
        Object obj = SerializeUtil.unserialize(value);
        return obj==null?0:
                (int)Float.parseFloat(((AtisModbusLog)obj).getData());
    }

    public float getFloatData(Jedis jedis, String registerAddress, String strobeId, String subStrobeId)
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