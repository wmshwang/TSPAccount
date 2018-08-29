package com.desaysv.secureapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MaQiang on 2018-4-25.
 * 非默认支持的数据类型的序列化操作
 * Result实现了Parcelable的接口，
 */

public class Result implements Parcelable {

    private int errCode;
    private String reqBuffer;
    private byte[] plainText;
    private byte[] cipherTextAndMac;
    private String encSK;
    private String encSKCV;
    private String macSK;
    private String macSKCV;
    private byte[] msgData;
    private byte[] msgSignVal;
    private byte[] regData;
    private byte[] confirmResult;
    private byte[] authResult;
    private byte[] test;

    public Result() {

    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getReqBuffer() {
        return reqBuffer;
    }

    public void setReqBuffer(String reqBuffer) {
        this.reqBuffer = reqBuffer;
    }

    public byte[] getCipherTextAndMac() {
        return cipherTextAndMac;
    }

    public void setCipherTextAndMac(byte[] cipherTextAndMac) {
        this.cipherTextAndMac = cipherTextAndMac;
    }

    public byte[] getPlainText() {
        return plainText;
    }

    public void setPlainText(byte[] plainText) {
        this.plainText = plainText;
    }


    public String getEncSK() {
        return encSK;
    }

    public void setEncSK(String encSK) {
        this.encSK = encSK;
    }

    public String getEncSKCV() {
        return encSKCV;
    }

    public void setEncSKCV(String encSKCV) {
        this.encSKCV = encSKCV;
    }

    public String getMacSK() {
        return macSK;
    }

    public void setMacSK(String macSK) {
        this.macSK = macSK;
    }

    public String getMacSKCV() {
        return macSKCV;
    }

    public void setMacSKCV(String macSKCV) {
        this.macSKCV = macSKCV;
    }

    public byte[] getMsgData() {
        return msgData;
    }

    public void setMsgData(byte[] msgData) {
        this.msgData = msgData;
    }

    public byte[] getMsgSignVal() {
        return msgSignVal;
    }

    public void setMsgSignVal(byte[] msgSignVal) {
        this.msgSignVal = msgSignVal;
    }

    public byte[] getRegData() {
        return regData;
    }

    public void setRegData(byte[] regData) {
        this.regData = regData;
    }

    public byte[] getAuthResult() {
        return authResult;
    }

    public void setAuthResult(byte[] authResult) {
        this.authResult = authResult;
    }

    protected Result(Parcel in) {
        this.errCode = in.readInt();
        this.reqBuffer = in.readString();
        this.cipherTextAndMac = in.createByteArray();
        this.plainText = in.createByteArray();
        this.encSK = in.readString();
        this.encSKCV = in.readString();
        this.macSK = in.readString();
        this.macSKCV = in.readString();
        this.msgData = in.createByteArray();
        this.msgSignVal = in.createByteArray();
        this.regData = in.createByteArray();
        this.confirmResult = in.createByteArray();
        this.authResult = in.createByteArray();
        this.test = in.createByteArray();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.errCode);
        parcel.writeString(this.reqBuffer);
        parcel.writeByteArray(this.cipherTextAndMac);
        parcel.writeByteArray(this.plainText);
        parcel.writeString(this.encSK);
        parcel.writeString(this.encSKCV);
        parcel.writeString(this.macSK);
        parcel.writeString(this.macSKCV);
        parcel.writeByteArray(this.msgData);
        parcel.writeByteArray(this.msgSignVal);
        parcel.writeByteArray(this.regData);
        parcel.writeByteArray(this.confirmResult);
        parcel.writeByteArray(this.authResult);
        parcel.writeByteArray(this.test);
    }

    /**
     * 参数parcel，用来存储和传输数据
     *
     * @param parcel
     */
    public void readFromParcel(Parcel parcel) {

        errCode = parcel.readInt();
        reqBuffer = parcel.readString();
        parcel.readByteArray(plainText);
        parcel.readByteArray(cipherTextAndMac);
        encSK = parcel.readString();
        encSKCV = parcel.readString();
        macSK = parcel.readString();
        macSKCV = parcel.readString();
        parcel.readByteArray(msgData);
        parcel.readByteArray(msgSignVal);
        parcel.readByteArray(regData);
        parcel.readByteArray(confirmResult);
        parcel.readByteArray(authResult);
        parcel.readByteArray(test);
    }

    @Override
    public String toString() {
        return "Result{" +
                ": errCode='" + errCode + '\'' +
                ", reqBuffer='" + reqBuffer + '\'' +
                ", plainText='" + plainText + '\'' +
                ", cipherTextAndMac='" + cipherTextAndMac + '\'' +
                ", encSK='" + encSK + '\'' +
                ", encSKCV='" + encSKCV + '\'' +
                ", macSK='" + macSK + '\'' +
                ", macSKCV='" + macSKCV + '\'' +
                ", msgData='" + msgData + '\'' +
                ", msgSignVal='" + msgSignVal + '\'' +
                ", regData='" + regData + '\'' +
                ", confirmResult='" + confirmResult + '\'' +
                ", authResult='" + authResult + '\'' +
                ", test=" + test + '\'' +
                '}';
    }
}
