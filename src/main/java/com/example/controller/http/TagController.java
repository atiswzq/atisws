package com.example.controller.http;

import com.example.model.Tag;
import net.csdn.annotation.rest.At;
import net.csdn.modules.http.ApplicationController;
import net.csdn.modules.http.RestRequest;
import net.csdn.modules.http.ViewType;
import redis.clients.jedis.JedisPool;

import static net.csdn.filter.FilterHelper.BeforeFilter.only;

/**
 * 12/25/13 WilliamZhu(allwefantasy@gmail.com)
 */
public class TagController extends ApplicationController {

    @At(path = "/tag", types = {RestRequest.Method.GET})
    public void save() {
        Tag tag = Tag.create(params());
        System.out.print(Tag.count("name","java1"));
        if (tag.save()) {
            System.out.print(tag.id());
            render(200, map("id",tag.id(),"id2",tag.id()),ViewType.json);
        }
        render(400, "失败", ViewType.string);
    }

    @At(path = "/tag/find", types = {RestRequest.Method.GET})
    public void find() {
        Tag tag = Tag.where(map("name", param("id"))).singleFetch();
        if (tag == null) {
            render(200, map());
        }
        render(200, list(tag));
    }
}