package com.desay_svautomotive.tspaccount.EngineeringBasic.base;

import android.os.Bundle;

/**
 * @author 王漫生
 * @date 2018-7-10
 * @project：个人中心
 */

public interface UiCallback {
    void initData(Bundle savedInstanceState);

    void setListener();

    int getLayoutId();
}
