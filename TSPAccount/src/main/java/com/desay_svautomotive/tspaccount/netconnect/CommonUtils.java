package com.desay_svautomotive.tspaccount.netconnect;

import android.os.SystemProperties;
import android.text.TextUtils;

import com.desay_svautomotive.tspaccount.Utils.Constant;

/**
 * @author 王漫生
 * @date 2018-3-16
 * @project：个人中心
 */
public class CommonUtils {

    public static String getServerName() {
        // value 为“0”则是正式生产环境，为“1”则是德赛西威开发测试环境,为“2”则是腾讯云沙箱测试环境，由于第一次开机还未进行设置，get到的值是为空的，所以默认值按需设置为“0”，或者“1”，或者“2”
        String mTempMode = SystemProperties.get("persist.sv.EnvironmentMode", "0");
        if (TextUtils.isEmpty(mTempMode)) {
            return Constant.TSP_SERVER_NAME;
        } else {
            if (mTempMode.equals("0")) {
                return Constant.TSP_SERVER_NAME;
            }else if (mTempMode.equals("1")) {
                return Constant.TSP_TEST_SERVER_NAME;
            }  else {
                return Constant.TSP_SANDBOX_SERVER_NAME;
            }
        }
    }

    //环境判断
    public static  String contextSwitch() {
        // value 为“0”则是正式生产环境，为“1”则是德赛西威开发测试环境,为“2”则是腾讯云沙箱测试环境，由于第一次开机还未进行设置，get到的值是为空的，所以默认值按需设置为“0”，或者“1”，或者“2”
        String mTempMode = SystemProperties.get("persist.sv.EnvironmentMode", "0");
        if (TextUtils.isEmpty(mTempMode)) {
            mTempMode = "0";
        }
        return mTempMode;
    }

}
