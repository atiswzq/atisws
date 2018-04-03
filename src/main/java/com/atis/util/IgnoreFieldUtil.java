package com.atis.util;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

/**
 * Created by Administrator on 2016/7/7.
 */
public class IgnoreFieldUtil implements PropertyFilter{
        /**
         * 忽略的属性名称
         */
        private String[] fields;

        /**
         * 是否忽略集合
         */
        private boolean ignoreColl = false;

        /**
         * 空参构造方法<br/>
         * 默认不忽略集合
         */
        public IgnoreFieldUtil() {
            // empty
        }

        /**
         * 构造方法
         * @param fields 忽略属性名称数组
         */
        public IgnoreFieldUtil(String[] fields) {
            this.fields = fields;
        }

        /**
         * 构造方法
         * @param ignoreColl	是否忽略集合
         * @param fields	忽略属性名称数组
         */
        public IgnoreFieldUtil(boolean ignoreColl, String[] fields) {
            this.fields = fields;
            this.ignoreColl = ignoreColl;
        }

        /**
         * 构造方法
         * @param ignoreColl 是否忽略集合
         */
        public IgnoreFieldUtil(boolean ignoreColl) {
            this.ignoreColl = ignoreColl;
        }

        public boolean apply(Object source, String name, Object value) {
            Field declaredField = null;
            //忽略值为null的属性
            if(value == null)
                return true;
            //剔除自定义属性，获取属性声明类型
            if(!"data".equals(name) && "data"!=name && !"totalSize".equals(name) && "totalSize"!=name ){
                try {
                    declaredField = source.getClass().getDeclaredField(name);
                } catch (NoSuchFieldException e) {

                }
            }
            // 忽略集合
            if (declaredField != null) {
                if(ignoreColl) {
                    if(declaredField.getType() == Collection.class
                            || declaredField.getType() == Set.class) {
                        return true;
                    }
                }
            }

            // 忽略设定的属性
            if(fields != null && fields.length > 0) {
                if(juge(fields,name)) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;
        }
        /**
         * 过滤忽略的属性
         * @param s
         * @param s2
         * @return
         */
        public boolean juge(String[] s,String s2){
            boolean b = false;
            for(String sl : s){
                if(s2.equals(sl)){
                    b=true;
                }
            }
            return b;
        }
        public String[] getFields() {
            return fields;
        }

        /**
         * 设置忽略的属性
         * @param fields
         */
        public void setFields(String[] fields) {
            this.fields = fields;
        }

        public boolean isIgnoreColl() {
            return ignoreColl;
        }

        /**
         * 设置是否忽略集合类
         * @param ignoreColl
         */
        public void setIgnoreColl(boolean ignoreColl) {
            this.ignoreColl = ignoreColl;
        }

        public static String ignoreField(String[] filed,Object object) {
            JsonConfig config = new JsonConfig();
            config.setJsonPropertyFilter(new IgnoreFieldUtil(true, filed)); // 忽略掉name属性及集合对象
            JSONObject fromObject = JSONObject.fromObject(object, config);
            return  fromObject.toString();
    }

        public static String toJSONArray(String[] filed,List list) {
            JsonConfig config = new JsonConfig();
            config.setJsonPropertyFilter(new IgnoreFieldUtil(true, filed)); // 忽略掉name属性及集合对象
//          JSONObject fromObject = JSONObject.fromObject(object, config);
            JSONArray jsonArray = JSONArray.fromObject(list,config);
            return  jsonArray.toString().replace("\"","");
    }

}
