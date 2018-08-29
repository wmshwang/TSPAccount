package com.desay_svautomotive.tspaccount.Utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.desay_svautomotive.tspaccount.Activities.PersonCenterActivity;

/**
 * @author 王漫生
 * @date 2018-7-7
 * @project：个人中心
 */

public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
    private int refCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        refCount++;
        if(refCount==1){
//            XLog.e("LifecycleListener","   -----  前台  "+refCount);
            PersonCenterActivity.upData(0);//埋点
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        refCount--;
        if(refCount == 0){
//            XLog.e("LifecycleListener","   -----  后台  "+refCount);
            PersonCenterActivity.upData(99);//埋点
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
