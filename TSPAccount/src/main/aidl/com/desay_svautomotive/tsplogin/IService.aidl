// IService.aidl
package com.desay_svautomotive.tsplogin;

//导入所需要使用的非默认支持数据类型的包
import com.desay_svautomotive.tsplogin.MessageCenter;

interface IService {
         oneway void registerCallback(MessageCenter cb);//注册

         oneway void unregisterCallback(MessageCenter cb);//反注册

         oneway void getLoginBean();//主动获取token

         oneway void toLogin();//token不可用时请求登录
}
