package com.atis.util;


import net.wimpi.modbus.net.TCPMasterConnection;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by Administrator on 2016/12/26.
 */
public class TCPConnManager {
    private static TCPConnManager tcm = null;
    private String poolName = null;
    private static int clients = 0;
    private Hashtable<String,TcpConnectionPool> pools = new Hashtable<String,TcpConnectionPool>();
   /* private TCPConnManager() {
        createPools();
    }
    private final Logger log = Logger.getLogger(TCPConnManager.class);
    private void createPools() {
        String path = System.getProperty("user.dir") + "/xml/";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            List<File> files = CommonUtil.getFiles(path);

            for (int i = 0; i < files.size(); i++) {

                Document doc = db.parse(files.get(i));

                String ip = doc.getElementsByTagName("ip").item(0)
                        .getFirstChild().getNodeValue();
                poolName = doc.getElementsByTagName("name")
                        .item(0).getFirstChild().getNodeValue();
                TcpConnectionPool pool = new TcpConnectionPool(ip,poolName);
                pools.put(poolName,pool);
//                log.info("成功创建PLC连接池" + poolName);
            }
        }catch(Exception e){
//            log.info("新建连接池" +  poolName + "失败，请检查PLC地址是否正确");
        }
    }*/

    public synchronized static TCPConnManager getInstance() {
        if(tcm == null) {
            tcm = new TCPConnManager();
        }
        clients++;
        return tcm;
    }

    public TCPMasterConnection getConnection(String poolName) {
        TcpConnectionPool pool = (TcpConnectionPool)pools.get(poolName);
        return pool.getConnection();
    }


    public TCPMasterConnection getConnection(String poolName,long timeout) {
        TcpConnectionPool  pool = (TcpConnectionPool)pools.get(poolName);
        return pool.getConnection(timeout);
    }

    public void freeConnection(String poolName,TCPMasterConnection conn) {
        TcpConnectionPool pool = (TcpConnectionPool)pools.get(poolName);
        if(pool != null) {
            pool.freeConnection(conn);
        }
//        log.error("找不到连接池，无法回收，请检查参数");
    }
    public void releaseAll(String poolName) {
        TcpConnectionPool pool = (TcpConnectionPool)pools.get(poolName);
        pool.releaseAll();
    }

    private class TcpConnectionPool {
        private int activeNum = 0;
        private int maxConn = 20;
        private String poolName = null;
        private String ip = null;
        private List<TCPMasterConnection> freeConnections = new ArrayList<TCPMasterConnection>();

        public TcpConnectionPool(String ip,String poolName) {
            super();
            this.poolName = poolName;
            this.ip = ip;
        }

        public synchronized TCPMasterConnection getConnection() {
            TCPMasterConnection conn = null;
            //空闲连接池中有空闲连接，直接取
            if(freeConnections.size() > 0) {
                //从空闲连接池中取出一个连接
                conn = freeConnections.get(0);
                freeConnections.remove(0);
                //检测连接有效性
                try{
                    if(conn==null) {
//                        log.info("从连接池" + poolName + "中取出的连接已关闭，重新获取连接");
                        conn = getConnection();
                    }
                }catch(Exception e) {
//                    log.info("从连接池" + poolName + "中取出的发生PLC访问错误，重新获取连接");
                    conn = getConnection();
                }
            } else if(activeNum < maxConn) {
                conn = newConnection();
            } else {
                //未获得连接
            }
            if(conn != null) {
                activeNum++;
            }
            return conn;
        }

        /**
         * 当无空闲连接而又未达到最大连接数限制时创建新的连接
         * @return  新创建的连接
         */
        private TCPMasterConnection newConnection() {
            TCPMasterConnection conn = null;
            try{
                InetAddress addr = InetAddress.getByName(ip);
                conn = new TCPMasterConnection(addr);
//                log.info("与PLC" + poolName + "创建一个新连接");
            }catch(Exception e) {
//                log.error("无法与PLC" + poolName + "建立新连接");
            }
            return conn;
        }

        public synchronized TCPMasterConnection getConnection(long timeout) {
            TCPMasterConnection conn = null;
            long startTime = System.currentTimeMillis();
            while((conn = getConnection()) == null) {
                try{
                    //被notify(),notifyALL()唤醒或者超时自动苏醒
                    wait(timeout);
                }catch(InterruptedException e) {

                }
                //若线程在超时前被唤醒，则不会返回null，继续循环尝试获取连接
                if(System.currentTimeMillis() - startTime > timeout*1000000)
                    return null;
            }
            return conn;
        }

        /**
         * 将释放的空闲连接加入空闲连接池，活跃连接数减一并激活等待连接的线程
         * @param conn  释放的连接
         */
        public synchronized void freeConnection(TCPMasterConnection conn) {
            freeConnections.add(conn);
            activeNum--;
            notifyAll();//通知正在由于达到最大连接数限制而wait的线程获取连接
        }

        /**
         * 关闭空闲连接池中的所有连接
         */
        public synchronized void releaseAll() {
            for(TCPMasterConnection conn:freeConnections) {
                try{
                    conn.close();
//                    log.info("关闭空闲连接池" + poolName + "中的一个连接");
                }catch(Exception e) {
//                    log.error("关闭空闲连接池" + poolName + "中的连接失败");
                }
            }
            freeConnections.clear();
        }
    }
}
