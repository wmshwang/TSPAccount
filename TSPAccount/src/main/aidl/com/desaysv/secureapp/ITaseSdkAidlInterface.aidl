// ITaseSdkAidlInterface.aidl
package com.desaysv.secureapp;

import com.desaysv.secureapp.Result;
// Declare any non-default types here with import statements


interface ITaseSdkAidlInterface {
        //3.单向消息认证请求
        Result Security_onewayMsgAuthenReq (String authData);
        //4.身份认证初始化_v2.0
        Result Security_authenReq();
        //5.身份认证响应_v2.0
        Result Security_authenAns(inout byte[] ansBuffer);
        //6.会话秘钥加密数据_v1.3
        Result Security_doEncryptWithSK (inout byte[] data);
        //7.会话秘钥解密数据_v1.2.3
        Result Security_doDecryptWithSK (inout byte[] cipherTextAndMac);
        //8.终端信息验签
        Result Security_terminalMsgVerify (String srcFilePath,String fileVerifyCode);
        //9.终端信息签名
        Result Security_terminalMsgSign (String msg);
        //10.指定密钥加密数据_v1.3
        Result Security_doEncryptWithClearKey (String key,inout byte[] data);
        //11.指定密钥解密数据_v1.2.3
        Result Security_doDecryptWithClearKey (String key,inout byte[] cipherTextAndMac);
        //14.二维码数据加密
        Result Security_encodeQRcode (String QRcode);
        //验证终端是否做完身份认证
        boolean Security_CheckIsAuthen();
}
