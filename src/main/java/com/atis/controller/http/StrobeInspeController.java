package com.atis.controller.http;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.atis.controller.BController;
import com.atis.model.AtisInspeLog;
import com.atis.model.AtisStrobeInspeInfo;
import com.atis.model.AtisUser;
import com.atis.model.AtisWarnLevel;
import com.atis.util.*;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.common.env.Environment;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import scala.Int;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/9.
 */
public class StrobeInspeController extends BController{
    static {
        beforeFilter("paginate", WowCollections.map(only, WowCollections.list("queryInspeInfo","inspeLineDate")));
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("queryInspeInfo","queryInspeInfoDetail","saveInspeInfo","queryInspeFlow","inspeLineDate")));
    }


    @At(path = "/inspe/uploadDir", types = {RestRequest.Method.POST})
    public void uploadSong() {
        String userId = redisClient.get(param("token"));
        Map resultMap = new HashMap();
        try {
            resultMap.put("voiceUrl", AliyunOssUtil.baseDir + AliyunOssUtil.separator + userId + AliyunOssUtil.separator + AliyunOssUtil.audioDir + AliyunOssUtil.separator);
            resultMap.put("photoUrl",AliyunOssUtil.baseDir + AliyunOssUtil.separator + userId + AliyunOssUtil.separator + AliyunOssUtil.imgDir + AliyunOssUtil.separator);
        }catch (Exception e){
            e.printStackTrace();
        }
        render(200,OutPutUtil.retunSuccMap(resultMap,null,null));
    }

    /**
     *
     * */

    @At(path = "/inspe/saveInspeInfo", types = {RestRequest.Method.POST})
    public void saveInspeInfo() {
        token =param("token");
        String voice = param("voiceUrl");
        String userId = redisClient.get(token);
        int inspeInfoId = paramAsInt("inspeInfoId");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        try {
            AtisStrobeInspeInfo atisStrobeInspeInfo = AtisStrobeInspeInfo.create(map("title",param("title"),
                                                                                    "userLocation", param("userLocation"),
                                                                                    "isNormal", param("isNormal"),
                                                                                    "voiceUrl",param("voiceUrl"),
                                                                                    "photoUrl",param("photoUrl"),
                                                                                    "status",0,"orgId",inspeInfoId,
                                                                                    "description", param("desc"),
                                                                                    "createDate",System.currentTimeMillis()/1000,
                                                                                    "createBy",userId,
                                                                                    "modifyDate",System.currentTimeMillis()/1000,
                                                                                    "inspeDate",dateFormat.format( new Date() ).toString(),
                                                                                    "inspeMonth",monthFormat.format( new Date() ).toString()));
            atisStrobeInspeInfo.save();
            if(inspeInfoId!=0){
                AtisStrobeInspeInfo atisStrobeInspeInfo1 = AtisStrobeInspeInfo.find(inspeInfoId);
                atisStrobeInspeInfo1.attr("status",paramAsInt("status"));
                atisStrobeInspeInfo1.update();
                AtisInspeLog atisInspeLog = AtisInspeLog.create(map("inspeInfoId",atisStrobeInspeInfo.id(),"orgInspeId",inspeInfoId,"userId",userId,"type",2,"modifyDate",System.currentTimeMillis()/1000,
                        "createDate",System.currentTimeMillis()/1000));
                atisInspeLog.save();
            }else{
                AtisInspeLog atisInspeLog = AtisInspeLog.create(map("inspeInfoId",atisStrobeInspeInfo.id(),"orgInspeId",atisStrobeInspeInfo.id(),"userId",userId,"type",0,"modifyDate",System.currentTimeMillis()/1000,
                        "createDate",System.currentTimeMillis()/1000));
                atisInspeLog.save();
            }
        }catch(Exception e){
            e.printStackTrace();
            render(200,OutPutUtil.retunSuccMap(0,null,null));
        }
        AtisUser atisUser = AtisUser.find(Integer.parseInt(userId));
        long time = System.currentTimeMillis()/1000;
        List<AtisUser> pushUserList = AtisUser.where(map("roleIdGroup",String.class)).fetch();
        for(AtisUser atisUser1 : pushUserList){
            if(atisUser1.attr("roleIdGroup",String.class).contains("2")) {
                int pushUserId = atisUser1.id();
                Thread jPushTh = new Thread(new JPushRun(JPushUtil.JPushBug, atisUser.attr("name", String.class),
                        param("title"), String.valueOf(pushUserId), String.valueOf(time)));
                jPushTh.start();
            }
        }

        render(200,OutPutUtil.retunSuccMap(1,null,null));
    }


    @At(path = "/inspe/disInspeInfo", types = {RestRequest.Method.POST})
    public void disInspeInfo() {
        token =param("token");
        int userId = Integer.parseInt(redisClient.get(token));
        int inspeInfoId = paramAsInt("inspeInfoId");
        int orgId = paramAsInt("orgId");
        AtisInspeLog atisInspeLog = AtisInspeLog.create(map("inspeInfoId",inspeInfoId,"orgInspeId",inspeInfoId,"userId",userId,"type",1,"modifyDate",System.currentTimeMillis()/1000,
                "createDate",System.currentTimeMillis()/1000));
        atisInspeLog.save();
        AtisStrobeInspeInfo atisStrobeInspeInfo = AtisStrobeInspeInfo.find(inspeInfoId);
        atisStrobeInspeInfo.attr("status",1);
        atisStrobeInspeInfo.attr("orgId",orgId);
        atisStrobeInspeInfo.update();
        render(200,OutPutUtil.retunSuccMap(1,null,null));
    }

    @At(path = "/inspe/queryInspeInfo", types = {RestRequest.Method.POST})
    public void queryInspeInfo() {
        token =param("token");
        int status = paramAsInt("status");
        int userId = Integer.parseInt(redisClient.get(token));
        AtisUser atisUser = AtisUser.find(userId);
        Map parameterMap = new HashMap();
        String roleIdGroup = atisUser.attr("roleIdGroup",String.class);
        List<AtisStrobeInspeInfo> atisStrobeInspeInfoList = new ArrayList<AtisStrobeInspeInfo>();
        if(roleIdGroup.contains(ConstantUtil.inspeSubmitRole)){
            atisStrobeInspeInfoList = AtisStrobeInspeInfo.order("createDate DESC").where(map("createBy",userId,"status",status)).offset((Integer) paginate.pageCalc()._1())
                    .limit(paramAsInt("pageSize", 15)).fetch();
            parameterMap.put("totalPage", (AtisStrobeInspeInfo.where(map("createBy", userId,"status",status)).count_fetch()-1)/paramAsInt("pageSize",15)+1);
        }else if(roleIdGroup.contains(ConstantUtil.inspeOperatorRole)){
            atisStrobeInspeInfoList = AtisStrobeInspeInfo.order("createDate DESC").where(map("orgId",atisUser.attr("orgId",Integer.class),"status",status)).offset((Integer) paginate.pageCalc()._1())
                    .limit(paramAsInt("pageSize", 15)).fetch();
            parameterMap.put("totalPage", (AtisStrobeInspeInfo.where(map("createBy", userId,"status",status)).count_fetch()-1)/paramAsInt("pageSize",15)+1);
        }else {
            atisStrobeInspeInfoList = AtisStrobeInspeInfo.order("createDate DESC").where(map("status",status)).offset((Integer) paginate.pageCalc()._1())
                    .limit(paramAsInt("pageSize", 15)).fetch();
            parameterMap.put("totalPage", (AtisStrobeInspeInfo.where(map("status",status)).count_fetch()-1)/paramAsInt("pageSize",15)+1);
        }
        for(AtisStrobeInspeInfo atisStrobeInspeInfo:atisStrobeInspeInfoList){
            Map<String,Object> map =new HashMap<String, Object>();
            map.put("title",atisStrobeInspeInfo.attr("title",String.class));
            map.put("id",atisStrobeInspeInfo.id());
            map.put("name",AtisUser.find(atisStrobeInspeInfo.attr("createBy",Integer.class)).attr("name",String.class));
            map.put("createDate",atisStrobeInspeInfo.attr("createDate",Long.class));
            returnMapList.add(map);
        }
        render(200,OutPutUtil.retunSuccMap(parameterMap,returnMapList,null));
    }

    @At(path = "/inspe/queryInspeInfoDetail", types = {RestRequest.Method.POST})
    public void queryInspeInfoDetail() {
        token =param("token");
        int userId = Integer.parseInt(redisClient.get(token));
        int inspeInfoId = paramAsInt("inspeInfoId");
        AtisStrobeInspeInfo atisStrobeInspeInfo = AtisStrobeInspeInfo.find(inspeInfoId);
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("title", atisStrobeInspeInfo.attr("title", String.class));
        parameterMap.put("userLocation", atisStrobeInspeInfo.attr("userLocation", String.class));
        parameterMap.put("voiceUrl", atisStrobeInspeInfo.attr("voiceUrl", String.class));
        parameterMap.put("photoUrl", atisStrobeInspeInfo.attr("photoUrl", String.class));
        parameterMap.put("isNormal", atisStrobeInspeInfo.attr("isNormal", Integer.class));
        parameterMap.put("status", atisStrobeInspeInfo.attr("status", Integer.class));
        parameterMap.put("createDate", atisStrobeInspeInfo.attr("createDate", Long.class));
        parameterMap.put("description", atisStrobeInspeInfo.attr("description", String.class));
        parameterMap.put("name",AtisUser.find(atisStrobeInspeInfo.attr("createBy", Integer.class)).attr("name",String.class));
        render(200,OutPutUtil.retunSuccMap(parameterMap,null,null));
    }

    @At(path = "/inspe/queryInspeFlow", types = {RestRequest.Method.POST})
    public void queryInspeFlow() {
        token =param("token");
        int userId = Integer.parseInt(redisClient.get(token));
        int inspeInfoId = paramAsInt("inspeInfoId");
        AtisUser atisUser = AtisUser.find(userId);
        String roleIdGroup = atisUser.attr("roleIdGroup",String.class);
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        if(roleIdGroup.contains(ConstantUtil.inspeObserverRole)) {
            List<AtisInspeLog> atisInspeLogList = AtisInspeLog.where(map("orgInspeId", inspeInfoId)).fetch();
            for (AtisInspeLog atisInspeLog : atisInspeLogList) {
                AtisStrobeInspeInfo atisStrobeInspeInfo1 = AtisStrobeInspeInfo.find(atisInspeLog.attr("inspeInfoId", Integer.class));
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", atisStrobeInspeInfo1.attr("title", String.class));
                map.put("userLocation", atisStrobeInspeInfo1.attr("userLocation", String.class));
                map.put("voiceUrl", atisStrobeInspeInfo1.attr("voiceUrl", String.class));
                map.put("photoUrl", atisStrobeInspeInfo1.attr("photoUrl", String.class));
                map.put("isNormal", atisStrobeInspeInfo1.attr("isNormal", Integer.class));
                map.put("status", atisStrobeInspeInfo1.attr("status", Integer.class));
                map.put("createDate", atisStrobeInspeInfo1.attr("createDate", Long.class));
                map.put("inspeInfoId", atisStrobeInspeInfo1.id());
                map.put("name",AtisUser.find(atisStrobeInspeInfo1.attr("createBy", Integer.class)).attr("name",String.class));
                returnMapList.add(map);
            }
            parameterMap.put("isObserver",1);
        }else{
            parameterMap.put("isObserver",0);
        }
        render(200,OutPutUtil.retunSuccMap(parameterMap,returnMapList,token));
    }


    @At(path = "/inspe/inspeLineDate", types = {RestRequest.Method.POST})
    public void inspeLineDate() {
        token =param("token");
        int userId = Integer.parseInt(redisClient.get(token));
        if(!StringUtil.checkEmpty(param("userId"))) {
            userId = Integer.parseInt(param("userId"));
        }
        AtisUser atisUser = AtisUser.find(userId);
        String roleIdGroup = atisUser.attr("roleIdGroup",String.class);
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        if(roleIdGroup.contains(ConstantUtil.inspeSubmitRole)) {
            List dateList = AtisStrobeInspeInfo.where(map("createBy",userId)).select("DISTINCT inspeDate").offset((Integer) paginate.pageCalc()._1())
                    .limit(paramAsInt("pageSize", 15)).fetch();
            for(int i = 0; i<dateList.size();i++){
                Map map = new HashMap();
                map.put("time",dateList.get(i).toString());
                map.put("name",atisUser.attr("name",String.class));
                returnMapList.add(map);
            }
            parameterMap.put("totalPage", (AtisStrobeInspeInfo.where(map("createBy",userId)).select("DISTINCT inspeDate").count_fetch()-1)/paramAsInt("pageSize",15)+1);
        }
        render(200,OutPutUtil.retunSuccMap(parameterMap,returnMapList,null));
    }

    @At(path = "/inspe/inspeLineDetail", types = {RestRequest.Method.POST})
    public void inspeLineDetail() {
        token =param("token");
        int userId = Integer.parseInt(redisClient.get(token));
        if(param("userId")!=null) {
            userId = Integer.parseInt(param("userId"));
        }
        AtisUser atisUser = AtisUser.find(userId);
        String date = param("date");
        List<AtisStrobeInspeInfo> atisStrobeInspeInfoList = AtisStrobeInspeInfo.where(map("inspeDate",date,"createBy",userId)).order("createDate ASC").fetch();
        for(AtisStrobeInspeInfo atisStrobeInspeInfo : atisStrobeInspeInfoList){
            Map map =new HashMap();
            map.put("name",atisUser.attr("name",String.class));
            map.put("createDate",atisStrobeInspeInfo.attr("createDate",Long.class));
            map.put("userLocation",atisStrobeInspeInfo.attr("userLocation",String.class));
            map.put("inspeInfoId",atisStrobeInspeInfo.id());
            map.put("isNormal",atisStrobeInspeInfo.attr("isNormal",Integer.class));
            returnMapList.add(map);
        }
        render(200,OutPutUtil.retunSuccMap(null,returnMapList,null));
    }


    @At(path = "/inspe/phoneInspeLineDate", types = {RestRequest.Method.POST})
    public void phoneInspeLineDate() {
        token =param("token");
        int userId = Integer.parseInt(redisClient.get(token));
        String inspeMonth = param("inspeMonth");
        if(param("userId")!=null) {
            userId = Integer.parseInt(param("userId"));
        }
        AtisUser atisUser = AtisUser.find(userId);
        String roleIdGroup = atisUser.attr("roleIdGroup",String.class);
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        if(roleIdGroup.contains(ConstantUtil.inspeSubmitRole)) {
            List dateList = AtisStrobeInspeInfo.where(map("createBy",userId,"inspeMonth",inspeMonth)).select("DISTINCT inspeDate").fetch();
            for(int i = 0; i<dateList.size();i++){
                Map map = new HashMap();
                map.put("time",dateList.get(i).toString());
                map.put("name",atisUser.attr("name",String.class));
                returnMapList.add(map);
            }
        }
        render(200,OutPutUtil.retunSuccMap(null,returnMapList,null));
    }

}