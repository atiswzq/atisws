package com.atis.util;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.atis.controller.BController;
import com.atis.model.AtisModbusLog;
import com.atis.model.AtisStrobeOperaLog;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadInputDiscretesRequest;
import net.wimpi.modbus.msg.ReadInputDiscretesResponse;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadInputRegistersResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

/*import com.dn9x.modbus.procimg.UnityRegister;*/

public class ModbusUtil{
	/**
	 * 查询Function 为Input Status的寄存器
	 *
	 * @param ip
	 * @param address
	 * @param
	 * @param slaveId
	 * @return
	 * @throws ModbusIOException
	 * @throws ModbusSlaveException
	 * @throws ModbusException
	 */
	public static String readDigitalInput(String ip, int port, String address, int slaveId) throws Exception{
		int data = 0;
		TCPMasterConnection con =null;
		try {
			InetAddress addr = InetAddress.getByName(ip);

			// 建立连接
			con = new TCPMasterConnection(addr);

			con.setPort(port);

			con.connect();

			// 第一个参数是寄存器的地址，第二个参数时读取多少个
			ReadInputDiscretesRequest req = new ReadInputDiscretesRequest(Integer.parseInt(address.split(",")[1]), 1);

			// 这里设置的Slave Id, 读取的时候这个很重要
			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);

			trans.setRequest(req);

			// 执行查询
			trans.execute();

			// 得到结果
			ReadInputDiscretesResponse res = (ReadInputDiscretesResponse) trans.getResponse();

			if(res.getDiscretes().getBit(0)){
				data = 1;
			}

			// 关闭连接
			con.close();

		} catch (Exception e) {
			System.out.print("DI address"+address+"出错错误信息为："+e.toString()+"  ip为"+ip);
			if(con!=null){
				con.close();
			}
			throw e;
		}
		return String.valueOf(data);
	}

	public static String readInputRegister(String name,String ip, int port, String address,
										   int slaveId) throws Exception{
		float data = 0f;
		int tmpData = 0;
		int tmpData1 = 0;
		TCPMasterConnection con =null;
		try {
			con = TCPConnManager.getInstance().getConnection(name);
			con.setPort(port);
			con.connect();

			ReadInputRegistersRequest req = new ReadInputRegistersRequest(Integer.parseInt(address.split(",")[1]), 1);
			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);


			trans.setRequest(req);

			trans.execute();

			ReadInputRegistersResponse res = (ReadInputRegistersResponse) trans.getResponse();
			tmpData = res.getRegister(0).getValue();
//			if(address.equals("RE,1")&&name.equals("lhq")){
			tmpData1 = res.getRegister(1).getValue();
//				System.out.println("222");
			byte[] tmp ={((byte)(255 & (short)tmpData1)),(byte)(255 & (short)tmpData1 >> 8),((byte)(255 & (short)tmpData)),(byte)(255 & (short)tmpData >> 8)};
//				byte[] tmp1 ={0,0,((byte)(255 & (short)tmpData)),(byte)(255 & (short)tmpData >> 8)};
			data = byte2float(tmp,0);
//				float data1 = byte2float(tmp1,0);
//				System.out.println(data);
//				System.out.println(data1);
//				System.out.println("2222");
//			}
//			byte[] tmp ={0,0,((byte)(255 & (short)tmpData)),(byte)(255 & (short)tmpData >> 8)};
//			byte[] tmp ={((byte)(255 & (short)tmpData1)),(byte)(255 & (short)tmpData1 >> 8),((byte)(255 & (short)tmpData)),(byte)(255 & (short)tmpData >> 8)};

//			data = byte2float(tmp,0);
			TCPConnManager.getInstance().freeConnection(name,con);
		} catch (Exception e) {
			TCPConnManager.getInstance().freeConnection(name,con);
			TCPConnManager.getInstance().releaseAll(name);
			throw e;
		}

		return String.valueOf(data);

	}


	public static String readRegister(String name,String ip, int port, String address,
									  int slaveId) throws Exception{
		float data = 0f;
		int tmpData = 0;
		int tmpData1 = 0;
		TCPMasterConnection con =null;
		try {
			con = TCPConnManager.getInstance().getConnection(name);
			con.setPort(port);
			con.connect();

			ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(Integer.parseInt(address.split(",")[1]), 1);
			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);


			trans.setRequest(req);

			trans.execute();

			ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();
			tmpData = res.getRegister(0).getValue();
//			tmpData1 = res.getRegister(1).getValue();

			byte[] tmp ={0,0,((byte)(255 & (short)tmpData)),(byte)(255 & (short)tmpData >> 8)};
//			byte[] tmp ={((byte)(255 & (short)tmpData1)),(byte)(255 & (short)tmpData1 >> 8),((byte)(255 & (short)tmpData)),(byte)(255 & (short)tmpData >> 8)};
			data = byte2float(tmp,0);
			TCPConnManager.getInstance().freeConnection(name,con);
		} catch (Exception e) {
			TCPConnManager.getInstance().freeConnection(name,con);
			TCPConnManager.getInstance().releaseAll(name);
			throw e;
		}
		return String.valueOf(data);
	}


	public static String readDigitalOutput(String name,String ip, int port, String address,
										   int slaveId) throws Exception{
		int data = 0;
		TCPMasterConnection con =null;
		try {

			con = TCPConnManager.getInstance().getConnection(name);
			con.setPort(port);
			con.connect();
			ReadCoilsRequest req = new ReadCoilsRequest(Integer.parseInt(address.split(",")[1]), 1);

			req.setUnitID(slaveId);

			ModbusTCPTransaction trans = new ModbusTCPTransaction(con);
			trans.setRequest(req);

			trans.execute();

			ReadCoilsResponse res = ((ReadCoilsResponse) trans.getResponse());

			if(res.getCoils().getBit(0)){
				data = 1;
			}
			TCPConnManager.getInstance().freeConnection(name,con);
		} catch (Exception ex) {
			TCPConnManager.getInstance().freeConnection(name,con);
			TCPConnManager.getInstance().releaseAll(name);
			throw ex;
		}

		return String.valueOf(data);
	}

	public static float byte2float(byte[] b, int index) {
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index + 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index + 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index + 3] << 24);
		return Float.intBitsToFloat(l);
	}

	public static byte[] float2byte(float f) {

		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}

		// 翻转数组
		int len = b.length;
		// 建立一个与源数组元素类型相同的数组
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝一份副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交换
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}

		return dest;

	}

	/**
	 * 写入数据到真机，数据类型是RE
	 *
	 * @param ip
	 * @param port
	 * @param slaveId
	 * @param address
	 * @param value
	 */
	public static void writeRegister(int userId,String name,String ip, int port, int slaveId,
									 String address, String value) throws Exception{
		TCPMasterConnection connection =null;
		try {
//			InetAddress addr = InetAddress.getByName(ip);

//			connection = new TCPMasterConnection(addr);
			connection = TCPConnManager.getInstance().getConnection(name);
			connection.setPort(port);
			connection.connect();
			ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

			float f = Float.parseFloat(value);

			int data = (float2byte(f)[3] & 255) << 8 | (float2byte(f)[2]) & 255;

			UnityRegister register = new UnityRegister(String.valueOf(data));

			WriteSingleRegisterRequest req = new WriteSingleRegisterRequest(Integer.parseInt(address.split(",")[1]), register);

			req.setUnitID(slaveId);
			trans.setRequest(req);

			trans.execute();
			TCPConnManager.getInstance().freeConnection(name, connection);
		}catch (Exception e) {
			TCPConnManager.getInstance().freeConnection(name,connection);
			TCPConnManager.getInstance().releaseAll(name);
			throw e;
		}
	}

	/**
	 * 写入数据到真机的DO类型的寄存器上面
	 *
	 * @param ip
	 * @param port
	 * @param slaveId
	 * @param address
	 * @param value1
	 */
	public static void writeDigitalOutput(int userId ,String name ,String ip, int port, int slaveId,
										  String address, String value1) throws Exception{
		int value =Integer.parseInt(value1);
		TCPMasterConnection connection = null;
//			InetAddress addr = InetAddress.getByName(ip);
		try {
//			connection = new TCPMasterConnection(addr);
			connection = TCPConnManager.getInstance().getConnection(name);
			connection.setPort(port);
			connection.connect();
			connection.setTimeout(3000);
			ModbusTCPTransaction trans = new ModbusTCPTransaction(connection);

			boolean val = true;

			if (value == 0) {
				val = false;
			}

			WriteCoilRequest req = new WriteCoilRequest(Integer.parseInt(address.split(",")[1]), val);

			req.setUnitID(slaveId);
			trans.setRequest(req);

			trans.execute();
			TCPConnManager.getInstance().freeConnection(name, connection);
			if(address.equals("DO,0")||(address.equals("DO,15"))){
				AtisStrobeOperaLog.nativeSqlClient().defaultMysqlService().execute("INSERT INTO atis_strobe_opera_log (user_id, type, strobe_id, create_date, create_month, create_year) VALUES (?, ?, ?, ?, ?, ?)",
						userId,1,slaveId,DateUtil.getStandDate(),DateUtil.getStandMonth(),DateUtil.getStandYear());
			}else if(address.equals("DO,2")||address.equals("DO,17")){
				AtisStrobeOperaLog.nativeSqlClient().defaultMysqlService().execute("INSERT INTO atis_strobe_opera_log (user_id, type, strobe_id, create_date, create_month, create_year) VALUES (?, ?, ?, ?, ?, ?)",
						userId,3,slaveId,DateUtil.getStandDate(),DateUtil.getStandMonth(),DateUtil.getStandYear());
			}
		}catch (Exception e){
			if(address.equals("DO,0")||(address.equals("DO,15"))){
				AtisStrobeOperaLog.nativeSqlClient().defaultMysqlService().execute("INSERT INTO atis_strobe_opera_log (user_id, type, strobe_id, create_date, create_month, create_year) VALUES (?, ?, ?, ?, ?, ?)",
						userId,0,slaveId,DateUtil.getStandDate(),DateUtil.getStandMonth(),DateUtil.getStandYear());
			}else if(address.equals("DO,2")||address.equals("DO,17")){
				AtisStrobeOperaLog.nativeSqlClient().defaultMysqlService().execute("INSERT INTO atis_strobe_opera_log (user_id, type, strobe_id, create_date, create_month, create_year) VALUES (?, ?, ?, ?, ?, ?)",
						userId,2,slaveId,DateUtil.getStandDate(),DateUtil.getStandMonth(),DateUtil.getStandYear());
			}
			throw e;
		}
	}
}