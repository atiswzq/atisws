package com.atis.util;

/**
 * Created by Administrator on 2016/11/8.
 */
public class ConstantUtil {
    public static final int highLevelWarnType = 0; //高水位报警
    public static final int lowLevelWarnType = 1; //低水位报警
    public static final int warnType = 0; //水位报警
    public static final int nromalWarnType = 2; //正常水位
    public static final String  inspeOperatorRole = "3"; //河道巡检操作员
    public static final String inspeDistributeRole = "2"; //河道巡检指派员
    public static final String inspeSubmitRole = "1"; //河道巡检问题提交员
    public static final String inspeObserverRole = "4"; //河道办领导
    public static final String waterOperatorRole = "5"; //配水控制员
    public static final int disOrg = 1; //是否为指派单位
    public static final int inspeOrg = 2; //是否为巡检单位
    public static final float waterTrigger = 0.2F; //水位数据相隔0.2
    public static final float pumpWaterTrigger = 0.2F; //西公园泵站控制水位间隔
    public static final String separator = ":";
    public static final String strobeId = "strobeid";
    public static final String gatage = "gatage";
    public static final String strobeNum = "strobeNum";
    public static final String strobeStatus = "strobeStatus";
    public static final String strobeDesc = "strobeDesc";
}
