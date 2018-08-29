package com.desay_svautomotive.tspaccount.EngineeringBasic.base;

import android.view.View;

/**
 * @author 王漫生
 * @date 2018-7-10
 * @project：个人中心
 */

public interface UiDelegate {

    void resume();
    void pause();
    void destory();

    void visible(boolean flag, View view);
    void gone(boolean flag, View view);
    void inVisible(View view);

    void toastShort(String msg);
    void toastLong(String msg);

}
