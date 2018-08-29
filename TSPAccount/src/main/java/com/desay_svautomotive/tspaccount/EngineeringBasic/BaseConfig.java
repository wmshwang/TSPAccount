package com.desay_svautomotive.tspaccount.EngineeringBasic;

import com.desay_svautomotive.tspaccount.EngineeringBasic.cache.SharedPreferencesHelper;
import com.desay_svautomotive.tspaccount.EngineeringBasic.log.XLog;

/**
 * @author 王漫生
 * @date 2018-7-10
 * @project：个人中心
 */

public class BaseConfig {
    // #log
    private boolean isLog = true;
    private String logTag = "BaseConfig";

    // #cache
    private String cacheSpName = "config";

    private BaseConfig() {

    }

    private static BaseConfig xDroidBaseConf;

    public static BaseConfig getInstance() {
        if (xDroidBaseConf == null)
            xDroidBaseConf = new BaseConfig();
        return xDroidBaseConf;
    }

    public BaseConfig setLog(boolean log) {
        this.isLog = log;
        return this;
    }

    public BaseConfig setDefLogTag(String defLogTag) {
        this.logTag = defLogTag;
        return this;
    }

    public BaseConfig setCacheSpName(String cacheSpName) {
        this.cacheSpName = cacheSpName;
        return this;
    }

    public void build() {
        XLog.init(isLog, logTag);
        SharedPreferencesHelper.init(cacheSpName);
    }
}
