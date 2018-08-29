package com.desay_svautomotive.tspaccount.Utils;

/**
 * @author 王漫生
 * @date 2018-3-16
 * @project：个人中心
 */
public class Constant {
    public static final String appKey = "2";//车机APP：2
    public static final String version = "1.0";//车机APP版本

    /******** Activity启动相关 ***************/
    public static final int REQ_CODE_1 = 0;
    public static final int REQ_CODE_2 = 1;
    public static final int REQ_CODE_3 = 2;

    //---------服务器-----------------------------------------------------------------------------------
    public static final  String   TSP_TEST_SERVER_NAME  = "http://10.219.14.20:8080";// 测试http://iovtest.desaysv.com
    public static final  String   TSP_SANDBOX_SERVER_NAME  = "http://193.112.148.71:8080";
    public static final  String   TSP_SERVER_NAME  = "https://app.desaysv-iov.com";// 生产环境 https   https://app.desaysv.com

    public static final String USER_ACCOUNT_SERVER = "/vehicle/tbox/acct/info";//用户基本信息查询
    public static final String CAR_LIST_SERVER = "/vehicle/app/car/list";//用户车辆列表查询
    public static final String DEVICE_LIST_SERVER = "/vehicle/app/device/list";//用户设备信息查询
    public static final String PRODUCT_SERVER = "/vehicle/tbox/product/info";//产品介绍
    public static final String LOGOUT_SERVER = "/vehicle/app/sys/logout";//退出登录
}
