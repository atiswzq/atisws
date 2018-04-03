package com.atis.controller.http;

import com.atis.controller.BController;
import com.atis.model.AtisIntro;
import com.atis.util.FileUtil;
import com.atis.util.OutPutUtil;
import net.csdn.annotation.rest.At;
import net.csdn.common.collections.WowCollections;
import net.csdn.modules.http.RestRequest;
import scala.Int;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * Created by Administrator on 2016/11/9.
 */
public class IntroController extends BController{
    static {
        beforeFilter("check_token", WowCollections.map(only, WowCollections.list("queryVideoIntro")));
    }
    @At(path = "/intro/queryVideoIntro", types = {RestRequest.Method.POST})
    public void queryVideoIntro() throws Exception{
        //这个是收到请求后进入的第一步,上面的/intro/queryVideoIntro就是请求地址
        AtisIntro atisIntro = AtisIntro.limit(1).single_fetch();//这一步为数据库操作，查询数据库里面宣传食品的url地址
        Map<String,Object> parameterMap =new HashMap<String, Object>();
        parameterMap.put("guideDesc",atisIntro.attr("guideDesc",String.class));
        parameterMap.put("guideVideoUrl",atisIntro.attr("guideVideoUrl",String.class));
        //把地址放到map里面
        render(200, OutPutUtil.retunSuccMap(parameterMap,null,token));//转化成json对象返回给客户端
        //流程结束 所有的其它方法都是这么做的
//        renderHtml(200,"hello.vm",map("msg", "s"));
    }
}
