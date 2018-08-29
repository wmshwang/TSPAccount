package com.desay_svautomotive.tspaccount.bean;

/**
 * @author 王漫生
 * @date 2018-4-2
 * @project：个人中心
 */
public class PurchasedServiceBean {
    private String PRODUCT_ID ;//产品编码
    private String PRODUCT_PIC;//产品图片 URL
    private String PRODUCT_NAME;//产品名称
    private String PRODUCT_DESC;//产品描述
    private String CATEGORY;//产品类型
    private String PUTAWAY_TIME;//上架时间
    private String PULLOFF_TIME;//下架时间
    private String ORDER_STATUS;//订购关系 0：不存在订购关系 1：存在订购关系

    public PurchasedServiceBean() {

    }

    public PurchasedServiceBean(String PRODUCT_ID, String PRODUCT_PIC, String PRODUCT_NAME, String PRODUCT_DESC, String CATEGORY, String PUTAWAY_TIME, String PULLOFF_TIME, String ORDER_STATUS) {
        this.PRODUCT_ID = PRODUCT_ID;
        this.PRODUCT_PIC = PRODUCT_PIC;
        this.PRODUCT_NAME = PRODUCT_NAME;
        this.PRODUCT_DESC = PRODUCT_DESC;
        this.CATEGORY = CATEGORY;
        this.PUTAWAY_TIME = PUTAWAY_TIME;
        this.PULLOFF_TIME = PULLOFF_TIME;
        this.ORDER_STATUS = ORDER_STATUS;
    }

    public String getPRODUCT_ID() {
        return PRODUCT_ID;
    }

    public void setPRODUCT_ID(String PRODUCT_ID) {
        this.PRODUCT_ID = PRODUCT_ID;
    }

    public String getPRODUCT_PIC() {
        return PRODUCT_PIC;
    }

    public void setPRODUCT_PIC(String PRODUCT_PIC) {
        this.PRODUCT_PIC = PRODUCT_PIC;
    }

    public String getPRODUCT_NAME() {
        return PRODUCT_NAME;
    }

    public void setPRODUCT_NAME(String PRODUCT_NAME) {
        this.PRODUCT_NAME = PRODUCT_NAME;
    }

    public String getPRODUCT_DESC() {
        return PRODUCT_DESC;
    }

    public void setPRODUCT_DESC(String PRODUCT_DESC) {
        this.PRODUCT_DESC = PRODUCT_DESC;
    }

    public String getCATEGORY() {
        return CATEGORY;
    }

    public void setCATEGORY(String CATEGORY) {
        this.CATEGORY = CATEGORY;
    }

    public String getPUTAWAY_TIME() {
        return PUTAWAY_TIME;
    }

    public void setPUTAWAY_TIME(String PUTAWAY_TIME) {
        this.PUTAWAY_TIME = PUTAWAY_TIME;
    }

    public String getPULLOFF_TIME() {
        return PULLOFF_TIME;
    }

    public void setPULLOFF_TIME(String PULLOFF_TIME) {
        this.PULLOFF_TIME = PULLOFF_TIME;
    }

    public String getORDER_STATUS() {
        return ORDER_STATUS;
    }

    public void setORDER_STATUS(String ORDER_STATUS) {
        this.ORDER_STATUS = ORDER_STATUS;
    }

    @Override
    public String toString() {
        return "PurchasedServiceBean{" +
                "PRODUCT_ID='" + PRODUCT_ID + '\'' +
                ", PRODUCT_PIC='" + PRODUCT_PIC + '\'' +
                ", PRODUCT_NAME='" + PRODUCT_NAME + '\'' +
                ", PRODUCT_DESC='" + PRODUCT_DESC + '\'' +
                ", CATEGORY='" + CATEGORY + '\'' +
                ", PUTAWAY_TIME='" + PUTAWAY_TIME + '\'' +
                ", PULLOFF_TIME='" + PULLOFF_TIME + '\'' +
                ", ORDER_STATUS='" + ORDER_STATUS + '\'' +
                '}';
    }
}
