package com.atis.util;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import java.util.Date;
import org.apache.commons.lang.time.FastDateFormat;
/**
 * Created by Administrator on 2016/6/23.
 */
public class TokenUtils {
    private static final String privateKey = "fdas34jjfr好sja@#8$%dfkl;js&4*daklfjsdl;akfjsa342";

    public static String getToken(String password, String date) {
        return Hashing.md5().newHasher().
                putString(password, Charsets.UTF_8).
                putString(privateKey, Charsets.UTF_8).
                putString(date, Charsets.UTF_8).hash().toString();
    }


    public static String getToken(String password, Date date) {
        return Hashing.md5().newHasher().
                putString(password, Charsets.UTF_8).
                putString(privateKey, Charsets.UTF_8).
                putString(getDate(date), Charsets.UTF_8).hash().toString();
    }


    public static String getToken(String password) {
        return Hashing.md5().newHasher().
                putString(password, Charsets.UTF_8).
                putString(privateKey, Charsets.UTF_8).hash().toString();
                //putString(privateKey, Charsets.UTF_8).putString(getDate(), Charsets.UTF_8).hash().toString();

    }

    public static boolean validToken(String token, String password) {
        String confirm = getToken(password);
        if (confirm.equals(token)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDate() {
        Date date = new Date(System.currentTimeMillis());
        return FastDateFormat.getInstance("yyyyMMddHH").format(date);

    }
    public static String getDate(Date now) {

        return FastDateFormat.getInstance("yyyyMMddHH").format(now);

    }
    public static String getNextHour(Date now) {
        Date date = new Date(now.getTime()+60*60*1000);

        return FastDateFormat.getInstance("yyyyMMddHH").format(date);

    }
}
