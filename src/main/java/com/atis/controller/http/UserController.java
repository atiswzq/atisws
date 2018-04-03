package com.atis.controller.http;
import com.atis.controller.BController;
import com.atis.model.*;
import com.atis.util.ConstantUtil;
import com.atis.util.OutPutUtil;
import com.atis.util.TokenUtils;
import com.atis.util.UnityModbus;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;
/**
 * Created by kewj on 2016/6/22.
 */
public class UserController extends BController {
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("queryOrgUser","queryOrgUser")));
    }
    @At(path = "/user/userLogin", types = {RestRequest.Method.POST})
    public void userLogin() {
        String userName = param("username");
        String pass = param("pass");
        if(AtisUser.where(map("username",userName)).count_fetch()==0){
            render(200, OutPutUtil.retunSuccMap(4,null,token));
        }
        AtisUser atisUser = AtisUser.where(map("username",userName)).single_fetch();
        if(!DigestUtils.md5Hex(pass).equals(atisUser.attr("pass",String.class))) {
            render(200, OutPutUtil.retunSuccMap(4,null,token));
        }
        token = TokenUtils.getToken(userName);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name",atisUser.attr("name",String.class));
        map.put("roleIdGroup",atisUser.attr("roleIdGroup",String.class));
        render(200, OutPutUtil.retunSuccMap(map,null,token));
    }

    @At(path = "/user/registe", types = {RestRequest.Method.GET})
    public void registe() {
        String userName = param("username");
        String pass = param("pass");
        token = TokenUtils.getToken(userName);
        pass = DigestUtils.md5Hex(pass);
        AtisUser atisUser = AtisUser.create(map("username",userName,"pass",pass));
        atisUser.save();
        redisClient.set(token,atisUser.id().toString());
        render(200, OutPutUtil.retunSuccMap(null,null,token));
    }


    @At(path = "/user/queryOrg", types = {RestRequest.Method.POST})
    public void queryOrg() {
        token =param("token");
        List<AtisOrg> atisOrgsList = AtisOrg.where(map("type",ConstantUtil.disOrg)).fetch();
        for(AtisOrg atisOrg:atisOrgsList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id",atisOrg.id());
            map.put("orgName",atisOrg.attr("orgName",String.class));
            List<AtisUser> atisUserList = AtisUser.where(map("orgId",atisOrg.id())).fetch();
            List<Map<String,Object>> subList = new ArrayList<Map<String, Object>>();
            for(AtisUser atisUser:atisUserList){
                Map<String,Object> subMap = new HashMap<String, Object>();
                subMap.put("userId",atisUser.id());
                subMap.put("name",atisUser.attr("name",String.class));
                subList.add(subMap);
            }
            map.put("userList",subList);
            returnMapList.add(map);
        }
        render(200,OutPutUtil.retunSuccMap(null,returnMapList,null));
    }

    @At(path = "/user/queryInspeOrg", types = {RestRequest.Method.POST})
    public void queryInspeOrg() {
        token =param("token");
        List<AtisOrg> atisOrgsList = AtisOrg.where(map("type",ConstantUtil.inspeOrg)).fetch();
        for(AtisOrg atisOrg:atisOrgsList){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id",atisOrg.id());
            map.put("orgName",atisOrg.attr("orgName",String.class));
            List<AtisUser> atisUserList = AtisUser.where(map("orgId",atisOrg.id())).fetch();
            List<Map<String,Object>> subList = new ArrayList<Map<String, Object>>();
            for(AtisUser atisUser:atisUserList){
                Map<String,Object> subMap = new HashMap<String, Object>();
                subMap.put("userId",atisUser.id());
                subMap.put("name",atisUser.attr("name",String.class));
                subList.add(subMap);
            }
            map.put("userList",subList);
            returnMapList.add(map);
        }
        render(200,OutPutUtil.retunSuccMap(null,returnMapList,null));
    }

}
