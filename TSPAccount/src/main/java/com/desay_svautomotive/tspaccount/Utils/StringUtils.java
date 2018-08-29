package com.desay_svautomotive.tspaccount.Utils;

import java.text.SimpleDateFormat;

/**
 * @author 王漫生
 * @date 2018-3-8
 * @project：个人中心
 */
public class StringUtils {

    /** yyyy-MM-dd HH:mm:ss */
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取时间差
     * @return
     */
    public static long getTimeCom(String time1,String time2) {
        long between = 0;
        try {
            java.util.Date begin = sdf3.parse(time1);
            java.util.Date end = sdf3.parse(time2);
            between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return between;
    }

    //16进制转换为ASCII
    public static String convertHexToString(String hex){
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }
}
