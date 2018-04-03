package com.atis.util;

import java.net.URL;

public class HttpUtil {

	public static String getReturnData(String urlString){
		String res = ""; 
		try {
			URL url = new URL(urlString);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.connect();
			java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				res += line;
			}
			in.close();
		} catch (Exception e) {
			res = "";
			return res;
		}
		return res;
	}
	
	public static String getReturnDataTmp(String urlString) throws Exception{
		String res = ""; 
		URL url = new URL(urlString);
		java.net.HttpURLConnection conn = (java.net.HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		conn.connect();
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(),"UTF-8"));
		String line;
		while ((line = in.readLine()) != null) {
			res += line;
		}
		in.close();
		return res;
	}

	public static String setXML(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA["+return_code+"]]></return_code><return_msg><![CDATA["+return_msg+"]]></return_msg></xml>";
	}
}
