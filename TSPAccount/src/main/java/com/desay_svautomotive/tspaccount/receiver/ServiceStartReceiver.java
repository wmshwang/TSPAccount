package com.desay_svautomotive.tspaccount.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author 王漫生
 * @date 2018-4-27
 * @project：个人中心
 */
public class ServiceStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            //启动内部服务
            Intent intentInternal = new Intent(context, InternalService.class);
            context.startService(intentInternal);
        }catch (Exception e){e.printStackTrace();}

        try{
            /* 服务开机自启动 */
            Intent service = new Intent(context, CallbackAIDLService.class);
            context.startService(service);
        }catch (Exception e){e.printStackTrace();}
    }
}
