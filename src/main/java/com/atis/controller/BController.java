package com.atis.controller;

import com.atis.model.AtisModule;
import com.atis.model.AtisStrobe;
import com.atis.model.AtisUser;
import com.atis.util.OutPutUtil;
import com.atis.util.RedisClient;
import com.atis.util.StringUtil;
import com.atis.util.UnityModbus;
import com.google.inject.Inject;
import net.csdn.annotation.rest.At;

import net.csdn.modules.http.ApplicationController;
import net.csdn.modules.http.RestRequest;
import net.csdn.modules.http.ViewType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kewj on 2016/6/23.
 */
public class BController extends ApplicationController{

    protected Paginate paginate;

    protected String[] ignoreFileds = {};

    protected String datasetOut = "";

    protected List<Map<String,Object>> returnMapList = new ArrayList<Map<String, Object>>();

    protected String parameterOut = "";

    private void paginate() {
        this.paginate = new Paginate(paramAsInt("page", 1), paramAsInt("pageSize", 15));
    }

    protected String token;

    protected int userId;
    @Inject
    protected RedisClient redisClient;
    private void check_token() {
        token = param("token");
        if(token==null||redisClient.get(token)==null)
        render(400, "token无效", ViewType.string);
    }

    private void getUserId() {
        token = param("token");
        userId = Integer.parseInt(redisClient.get(token));
    }


    private void check_permission(){
        token =param("token");
        String userId = redisClient.get(token);
        int moduleId = paramAsInt("moduleId");
        AtisModule atisModule = AtisModule.find(moduleId);
        AtisUser atisUser = AtisUser.find(Integer.parseInt(userId));
        String[] moduleRole = atisModule.attr("roleIdGroup",String.class).split(",");
        String userRole = atisUser.attr("roleIdGroup",String.class);
        if(!StringUtil.equals(moduleRole,userRole)){
            render(200, OutPutUtil.retunSuccMap(3,null,token));
        }
    }

    private void web() {
/*        try {
            this.restResponse.httpServletResponse().setHeader("Access-Control-Allow-Origin", "*");
            this.restResponse.httpServletResponse().setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            this.restResponse.httpServletResponse().setHeader("Access-Control-Max-Age", "3600");
            this.restResponse.httpServletResponse().setHeader("Access-Control-Allow-Headers", "x-requested-with");
        } catch (Exception e) {

        }*/
    }
}