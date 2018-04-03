package com.atis.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Created by Administrator on 2016/11/12.
 */
public class SqlServerUtil {

    public static Connection getSqlServerConn() throws Exception{
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //加载JDBC驱动

        String dbURL = "jdbc:sqlserver://202.91.229.156:1433; DatabaseName=yurun";   //连接服务器和数据库sample

        String userName = "xsdata";   //默认用户名

        String userPwd = "Aa1234!";   //密码

        Connection dbConn;
        try {
            Class.forName(driverName);
            dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
//            System.out.println("Connection Successful!");   //如果连接成功 控制台输出Connection Successful!
            return dbConn;
        } catch (Exception e) {
            throw e;
        }
    }
}
