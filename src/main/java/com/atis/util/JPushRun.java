package com.atis.util;

/**
 * Created by Administrator on 2016/9/20.
 */
public class JPushRun implements  Runnable {
    String JPushType ;
    String fromNickname;
    String userId;
    String date;
    String title;
    public JPushRun(String JPushType,String fromNickname,
             String title,String userId ,String date){
        this.JPushType = JPushType;
        this.fromNickname = fromNickname;
        this.userId=userId;
        this.date=date;
        this.title = title;
    }
    public void jpush(String JPushType,String fromNickname,String title,
                       String userId,String date) throws Exception{
            JPushUtil.JPush(JPushType,fromNickname,title,userId,date);
    }

    public void run() {
        try {
            jpush(JPushType, fromNickname, title,userId,date);
        }catch (Exception e){

        }
    }
}
