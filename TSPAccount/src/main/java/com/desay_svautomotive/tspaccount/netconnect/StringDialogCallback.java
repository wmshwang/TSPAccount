package com.desay_svautomotive.tspaccount.netconnect;

import android.content.Context;

import com.desay_svautomotive.tspaccount.widget.LoadingDialog;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.base.Request;

/**
 * 自定义的OKGo回调类（添加接口请求时加载对话框）
 * @author 王漫生
 * @date 2018-5-15
 * @project：个人中心
 */
public abstract class StringDialogCallback extends StringCallback {

    private static Context context;
    private static LoadingDialog loadingDialog;//
    private boolean isShowDialog;//是否显示加载中对话框

    /**
     * @param context      上下文
     * @param isShowDialog 是否显示加载对话框false：不显示，true显示
     */
    public StringDialogCallback(Context context, boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
        if (isShowDialog == true) {
            this.context = context;
        }
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        if (isShowDialog ==true) {
            try{dialogShow();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }
    }

    @Override
    public void onFinish() {
        if (isShowDialog ==true) {
            try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }
    }

    public static  void dialogShow(){
        loadingDialog = new LoadingDialog.Builder(context).setShowMessage(false).setCancelable(true).setCancelOutside(false).create();
        loadingDialog.show();
    }

    public static  void dialogCancel(){
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
