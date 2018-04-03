package com.atis.model;

import net.csdn.jpa.model.Model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/14.
 */
public class AtisModbusLog implements Serializable {
    private int strobeId ;
    private int subStrobeId;
    private String ip;
    private int port;
    private int controllerId;
    private String registerAddress;
    private String data;
    private int createdBy;
    private long createDate;
    private long modifyDate;

    public int getStrobeId() {
        return strobeId;
    }

    public int getSubStrobeId() {
        return subStrobeId;
    }

    public String getIp() {
        return ip;
    }

    public int getControllerId() {
        return controllerId;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public String getData() {
        return data;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public long getCreateDate() {
        return createDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setStrobeId(int strobeId) {
        this.strobeId = strobeId;
    }

    public void setSubStrobeId(int subStrobeId) {
        this.subStrobeId = subStrobeId;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
