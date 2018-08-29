package com.desay_svautomotive.tsplogin.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author 王漫生
 * @date 2018-5-2
 * @project：登录
 */
public class LoginBean implements Parcelable {
    String token;//用户登录token 过期传值为""
    int expires;//有效时间 过期传值为0
    String loginTime;//登录时间 过期传值为""
    String phoneNum;//登录账号 过期传值为""
    String ACCT_ID;//账号ID 过期传值为""

    public LoginBean() {
    }

    public LoginBean(String token, int expires, String loginTime, String phoneNum, String ACCT_ID) {
        this.token = token;
        this.expires = expires;
        this.loginTime = loginTime;
        this.phoneNum = phoneNum;
        this.ACCT_ID = ACCT_ID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getACCT_ID() {
        return ACCT_ID;
    }

    public void setACCT_ID(String ACCT_ID) {
        this.ACCT_ID = ACCT_ID;
    }

    public LoginBean(Parcel in) {
        token = in.readString();
        expires = in.readInt();
        loginTime = in.readString();
        phoneNum = in.readString();
        ACCT_ID = in.readString();
    }

    public static final Creator<LoginBean> CREATOR = new Creator<LoginBean>() {
        @Override
        public LoginBean createFromParcel(Parcel in) {
            return new LoginBean(in);
        }

        @Override
        public LoginBean[] newArray(int size) {
            return new LoginBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeInt(expires);
        dest.writeString(loginTime);
        dest.writeString(phoneNum);
        dest.writeString(ACCT_ID);
    }

    /**
     * 参数是一个Parcel,用它来存储与传输数据
     *
     * @param dest
     */
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        token = dest.readString();
        expires = dest.readInt();
        loginTime = dest.readString();
        phoneNum = dest.readString();
        ACCT_ID = dest.readString();
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "token='" + token + '\'' +
                ", expires=" + expires +
                ", loginTime='" + loginTime + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", ACCT_ID='" + ACCT_ID + '\'' +
                '}';
    }
}
