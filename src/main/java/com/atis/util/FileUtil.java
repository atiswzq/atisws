package com.atis.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/11/12.
 */
public class FileUtil {
    public static void doPost(HttpServletRequest request, HttpServletResponse response,String pathString) throws ServletException, IOException {
        //String pathString="F:\\作业\\基于ARM11的嵌入式系统开发方法.docx";
        //要下载的文件路径，本文为绝对路径
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");


        //String pathString="D:\\我的文档\\桌面\\新建文件夹<a target=_blank href="file://\\Android">\\Android</a> 4编程入门经典.pdf";
        InputStream inputStream=null;
        OutputStream outputStream=null;
        File file=new File(pathString);
        inputStream=new BufferedInputStream(new FileInputStream(file));

        //设置为流下载
        response.setContentType("application/octet-sream");
        //设置响应大小
        response.setContentLength((int) file.length());

        response.setHeader("Content-type", "text/html;charset=UTF-8");
        //这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        String fileName=file.getName();
        //注意这里一般都用URLEncoder的encode方法进行对文件名进行编码
        String enFileName = URLEncoder.encode(fileName, "utf-8");
        System.out.println(enFileName);
        //浏览器下载
        response.addHeader("Content-Disposition", "attachment;filename="+ enFileName);

        outputStream=new BufferedOutputStream(response.getOutputStream());

        // 缓冲区大小1024
        byte[] s=new byte[10240];
        int len=0;
        //避免最后一次读取数据时，不满10240b的数据被填充，造成数据不准确性
        while((len=inputStream.read(s))!=-1)
        {
            outputStream.write(s, 0, len);

        }
        if (inputStream!=null) {
            inputStream.close();
        }
        response.flushBuffer();
        if (outputStream!=null) {
            outputStream.close();
        }
    }
}
