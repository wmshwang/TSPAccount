// MessageCenter.aidl
package com.desay_svautomotive.tsplogin;

//导入所需要使用的非默认支持数据类型的包
import com.desay_svautomotive.tsplogin.bean.LoginBean;

interface MessageCenter {
       //传参时除了Java基本类型以及String，CharSequence之外的类型
       //都需要在前面加上定向tag，具体加什么量需而定
       LoginBean addLoginBean(inout LoginBean loginBean);
}
