package com.atis.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/8/9.
 */
public class EmojiUtil {
    public static String emojiConvert1(String str)
            throws UnsupportedEncodingException {

        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(
                        sb,
                        "[["
                                + URLEncoder.encode(matcher.group(1),
                                "UTF-8") + "]]");
            } catch(UnsupportedEncodingException e) {

                throw e;
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     * @param str
     *            转换后的字符串
     * @return 转换前的字符串
     * @throws UnsupportedEncodingException
     *             exception
     */
    public static String emojiRecovery2(String str)
            throws UnsupportedEncodingException {
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(sb,
                        URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch(UnsupportedEncodingException e) {

                throw e;
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

	public static String escape(String s) {       
		        StringBuffer sbuf = new StringBuffer();       
		        int len = s.length();       
		        for (int i = 0; i < len; i++) {       
		            int ch = s.charAt(i);
		           if (ch == ' ') {                        // space : map to '+'
		                sbuf.append("%20");
		            } else if ('A' <= ch && ch <= 'Z') {    // 'A'..'Z' : as it was       
		                sbuf.append((char)ch);       
		            } else if ('a' <= ch && ch <= 'z') {    // 'a'..'z' : as it was       
		                sbuf.append((char)ch);       
		            } else if ('0' <= ch && ch<='9'){  
		            	// '0'..'9' : as it was       
		                sbuf.append((char)ch);       
		            } else if (ch == '-' || ch == '_'       // unreserved : as it was       
		                || ch == '.' || ch == '!'       
		                || ch == '~' || ch == '*'       
		                || ch == '\'' || ch == '('       
		                || ch == ')'|| ch=='@') {       
		                sbuf.append((char)ch);       
		            } else if (ch <= 0x007F) {              // other ASCII : map to %XX       
		                sbuf.append((char)ch);       
		            } else {                                // unicode : map to %uXXXX       
						sbuf.append('\\');
		                sbuf.append('u');       
		                sbuf.append(hex[(ch >>> 8)]);       
		                sbuf.append(hex[(0x00FF & ch)]);       
		            }       
		        }       
		        return sbuf.toString();       
		    }       
			
			 
			private final static String[] hex = {       
		        "00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",       
		        "10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",       
		        "20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",       
		        "30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",       
		        "40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",       
		        "50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",       
		        "60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",       
		        "70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",       
		        "80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",       
		        "90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",       
		        "A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",       
		        "B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",       
		        "C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",       
		        "D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",       
		        "E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",       
		        "F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"      
		    };
}
