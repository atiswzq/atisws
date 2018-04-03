package com.atis.controller.http;

import com.atis.controller.BController;
import com.atis.model.AtisModule;
import com.atis.model.AtisUser;
import com.atis.util.OutPutUtil;
import com.atis.util.StringUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;
import static net.csdn.filter.FilterHelper.BeforeFilter.only;
/**
 * Created by Administrator on 2016/11/9.
 */
public class PermissionController extends BController{
    static {
        beforeFilter("check_permission",WowCollections.map(only, WowCollections.list("checkPermission")));
    }

    @At(path = "/permission/checkPermission", types = {RestRequest.Method.POST})
    public void checkPermission(){
        render(200, OutPutUtil.retunSuccMap(null,null,token));
    }
}
