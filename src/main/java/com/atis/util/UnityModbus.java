
package com.atis.util;

import com.atis.controller.http.ModbusController;
import com.atis.model.AtisStrobeModbusConfig;
import com.dn9x.modbus.UnityBridge;
import com.dn9x.modbus.entity.ControllerEntity;
import com.dn9x.modbus.entity.RegisterEntity;
import com.dn9x.modbus.util.CommonUtil;
import com.dn9x.modbus.util.ModbusUtil;
import com.dn9x.modbus.util.SimulationMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/14.
 */

public class UnityModbus implements UnityBridge {
    private static Map<Integer, ModbusController> controllers = new HashMap<Integer, ModbusController>();

    public UnityModbus() {
        initControllerFromXml();
    }

    void initControllerFromXml() {
        String path = System.getProperty("user.dir") + "/xml/";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            List<File> files = CommonUtil.getFiles(path);

            System.out.println(">>>>>>>>>>>>>>>file count:" + files.size());

            for (int i = 0; i < files.size(); i++) {

                Document doc = db.parse(files.get(i));

                Element rootEle = doc.getDocumentElement();

                String id = rootEle.getAttribute("id");

                NodeList registers = doc.getElementsByTagName("register");

                String mode = doc.getElementsByTagName("mode").item(0)
                        .getFirstChild().getNodeValue();
                String controller = doc.getElementsByTagName("ctorName")
                        .item(0).getFirstChild().getNodeValue();
                String ip = doc.getElementsByTagName("ip").item(0)
                        .getFirstChild().getNodeValue();
                String port = doc.getElementsByTagName("port").item(0)
                        .getFirstChild().getNodeValue();
                String name1 = doc.getElementsByTagName("name").item(0)
                        .getFirstChild().getNodeValue();

                Map<String, RegisterEntity> map = new HashMap<String, RegisterEntity>();
                ControllerEntity ce = new ControllerEntity();
                if (mode.toLowerCase().equals("simulateandforwarding")) {
                    ce.setMode(SimulationMode.SimulateAndForwarding);
                } else {
                    ce.setMode(SimulationMode.SimulateOnly);
                }

                ModbusController ctor = (ModbusController) Class
                        .forName(controller).newInstance();

                // 启动每个controller的线程
                Thread demo = new Thread(ctor);
                demo.start();

                ce.setController(ctor);
                ce.setIp(ip);
                ce.setPort(Integer.parseInt(port));
                ce.setId(Integer.parseInt(id));
                ce.setRegisters(map);
                ce.setName(name1);

                for (int j = 0; j < registers.getLength(); j++) {
                    Node register = registers.item(j);
                    RegisterEntity re = new RegisterEntity();

                    for (Node node = register.getFirstChild(); node != null; node = node
                            .getNextSibling()) {
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            String name = node.getNodeName();
                            String value = node.getFirstChild().getNodeValue();
                            if (name.equals("address")) {
                                re.setAddress(value);
                            } else if (name.equals("defaultValue")) {
                                re.setValue(value);
                            } else if(name.equals("type")){
                                re.setType(value);
                            } else if(name.equals("subStrobeId")){
                                re.setSubStrobeId(Integer.parseInt(value));
                            } else if(name.equals("strobeId")){
                                re.setStrobeId(Integer.parseInt(value));
                            }
                        }
                    }

                    map.put(re.getAddress(), re);
                }

                ctor.setCtorEntity(ce);

                controllers.put(Integer.parseInt(id), ctor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void initControllerFromXmlById(int controllerId) {

        // 清除这个controller然后重新添加
        controllers.remove(controllerId);

        String path = System.getProperty("user.dir") + "/xml/";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            List<File> files = CommonUtil.getFiles(path);

            for (int i = 0; i < files.size(); i++) {
                Document doc = db.parse(files.get(i));

                Element rootEle = doc.getDocumentElement();

                String id = rootEle.getAttribute("id");

                if (Integer.parseInt(id) == controllerId) {
                    NodeList registers = doc.getElementsByTagName("register");

                    String mode = doc.getElementsByTagName("mode").item(0)
                            .getFirstChild().getNodeValue();
                    String controller = doc.getElementsByTagName("ctorName")
                            .item(0).getFirstChild().getNodeValue();
                    String ip = doc.getElementsByTagName("ip").item(0)
                            .getFirstChild().getNodeValue();
                    String port = doc.getElementsByTagName("port").item(0)
                            .getFirstChild().getNodeValue();

                    Map<String, RegisterEntity> map = new HashMap<String, RegisterEntity>();
                    ControllerEntity ce = new ControllerEntity();
                    if (mode.toLowerCase().equals("simulateandforwarding")) {
                        ce.setMode(SimulationMode.SimulateAndForwarding);
                    } else {
                        ce.setMode(SimulationMode.SimulateOnly);
                    }

                    ModbusController ctor = (ModbusController) Class.forName(
                            controller).newInstance();

                    // 启动每个controller的线程
                    Thread demo = new Thread(ctor);
                    demo.start();

                    ce.setController(ctor);
                    ce.setIp(ip);
                    ce.setPort(Integer.parseInt(port));
                    ce.setId(Integer.parseInt(id));
                    ce.setRegisters(map);

                    for (int j = 0; j < registers.getLength(); j++) {
                        Node register = registers.item(j);
                        RegisterEntity re = new RegisterEntity();

                        for (Node node = register.getFirstChild(); node != null; node = node
                                .getNextSibling()) {
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                String name = node.getNodeName();
                                String value = node.getFirstChild()
                                        .getNodeValue();

                                if (name.equals("address")) {
                                    re.setAddress(value);
                                } else if (name.equals("defaultValue")) {
                                    re.setValue(value);
                                } else if(name.equals("type")){
                                    re.setType(value);
                                } else if(name.equals("subStrobeId")){
                                    re.setSubStrobeId(Integer.parseInt(value));
                                } else if(name.equals("strobeId")){
                                    re.setStrobeId(Integer.parseInt(value));
                                }
                            }
                        }

                        map.put(re.getAddress(), re);
                    }

                    controllers.put(Integer.parseInt(id), ctor);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getAllRegisterState(int controllerId) {
        ModbusController bc = controllers.get(controllerId);

        Map<String, RegisterEntity> map = bc.getCtorEntity().getRegisters();

        Map<String, Integer> res = new HashMap<String, Integer>();

        for (String key : map.keySet()) {
            res.put(key, Integer.parseInt(map.get(key).getValue()));
        }

        return res;
    }

    public void setAllRegisterState(int controllerId, Map<Integer, String> data) {
        try {
            controllers.get(controllerId).setValue(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTotalRegisters(int controllerId) {
        int count = controllers.get(controllerId).getCtorEntity()
                .getRegisters().size();

        return count;
    }

    public int getRegisterState(int controllerId, int registerAddress) {
        int value =Integer.parseInt( controllers.get(controllerId).getCtorEntity()
                .getRegisters().get(registerAddress).getValue());

        return value;
    }

    public void setRegisterState(int controllerId, String registerAddress,
                                 int value) {
        try {
            controllers.get(controllerId).setValue(registerAddress, String.valueOf(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRegisterWritable(int controllerId, int registerAddress) {
        String type = controllers.get(controllerId).getCtorEntity()
                .getRegisters().get(registerAddress).getType();

        if (type.toUpperCase().equals("DO") || type.toUpperCase().equals("RE")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isRegisterReadOnly(int controllerId, int registerAddress) {
        String type = controllers.get(controllerId).getCtorEntity()
                .getRegisters().get(registerAddress).getType();

        if (type.toUpperCase().equals("DI") || type.toUpperCase().equals("IR")) {
            return true;
        } else {
            return false;
        }
    }

    public int getTotalControllers() {
        return controllers.size();
    }

    public void setSimulationMode(SimulationMode mode) {
        // 如果是SimulateOnly就清除掉所有的缓存数据，从新从配置文件读取
        if (mode == SimulationMode.SimulateOnly) {
            controllers.clear();
            initControllerFromXml();
        } else {
            // 否则就从真机中读取所有的数据到controller
            initControllerFromSlave();
        }
    }

    public boolean isSimulateOnly() {
        for (int key : controllers.keySet()) {
            if (controllers.get(key).getCtorEntity().getMode() == SimulationMode.SimulateAndForwarding) {
                return false;
            }
        }

        return true;
    }

    public void setSimulationMode(int controllerId, SimulationMode mode) {
        // 如果是SimulateOnly就清除掉所有的缓存数据，从新从配置文件读取
        if (mode == SimulationMode.SimulateOnly) {
            initControllerFromXmlById(controllerId);
        } else {
            // 否则就从真机中读取所有的数据到controller
            initControllerFromSlaveById(controllerId);
        }
    }

    public SimulationMode getSimulationMode(int controllerId) {
        return controllers.get(controllerId).getCtorEntity().getMode();
    }

    public Map<Integer, ModbusController> getControllers() {
        return controllers;
    }

    void initControllerFromSlave() {

        for (int key1 : controllers.keySet()) {
            ControllerEntity ce = controllers.get(key1).getCtorEntity();

            for (String key : ce.getRegisters().keySet()) {
                String data = "0";
                if (ce.getRegisters().get(key).getType().toUpperCase()
                        .equals("DI")) {
                    data = ModbusUtil.readDigitalInput(ce.getIp(),
                            ce.getPort(), ce.getRegisters().get(key)
                                    .getAddress(), ce.getId());
                } else if (ce.getRegisters().get(key).getType().toUpperCase()
                        .equals("DO")) {
                    data = ModbusUtil.readDigitalOutput(ce.getIp(),
                            ce.getPort(), ce.getRegisters().get(key)
                                    .getAddress(), ce.getId());
                } else if (ce.getRegisters().get(key).getType().toUpperCase()
                        .equals("IR")) {
                    data = ModbusUtil.readInputRegister(ce.getIp(),
                            ce.getPort(), ce.getRegisters().get(key)
                                    .getAddress(), ce.getId());
                } else {
                    data = ModbusUtil.readRegister(ce.getIp(), ce.getPort(), ce
                            .getRegisters().get(key).getAddress(), ce.getId());
                }

                System.out.println(">>>>>>>>>>>>>>read : "
                        + ce.getRegisters().get(key).getType() + " : " + data);

                ce.getRegisters().get(key).setValue(data);
            }
        }
    }

    void initControllerFromSlaveById(int controllerId) {
        ControllerEntity ce = controllers.get(controllerId).getCtorEntity();

        for (String key : ce.getRegisters().keySet()) {
            String data = "0";
            ArrayList<Integer> dataArray = new ArrayList<Integer>();
            if (ce.getRegisters().get(key).getType().toUpperCase().equals("DI")) {
                data = ModbusUtil.readDigitalInput(ce.getIp(), ce.getPort(), ce
                        .getRegisters().get(key).getAddress(), ce.getId());
            } else if (ce.getRegisters().get(key).getType().toUpperCase()
                    .equals("DO")) {
                data = ModbusUtil.readDigitalOutput(ce.getIp(), ce.getPort(),
                        ce.getRegisters().get(key).getAddress(), ce.getId());
            } else if (ce.getRegisters().get(key).getType().toUpperCase()
                    .equals("IR")) {
                data = ModbusUtil.readInputRegister(ce.getIp(), ce.getPort(),
                        ce.getRegisters().get(key).getAddress(), ce.getId());
            } else {
                data = ModbusUtil.readRegister(ce.getIp(), ce.getPort(), ce
                        .getRegisters().get(key).getAddress(), ce.getId());
            }

            System.out.println(">>>>>>>>>>>>>>read : "
                    + ce.getRegisters().get(key).getType() + " : " + data);

            ce.getRegisters().get(key).setValue(data);
        }
    }


}

*/
