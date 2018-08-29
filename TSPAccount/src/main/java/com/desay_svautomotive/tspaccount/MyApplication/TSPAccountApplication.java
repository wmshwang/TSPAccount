package com.desay_svautomotive.tspaccount.MyApplication;

import android.app.Application;
import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;

import com.desay_svautomotive.tspaccount.EngineeringBasic.BaseConfig;
import com.desay_svautomotive.tspaccount.Utils.ActivityLifecycleListener;
import com.desay_svautomotive.tspaccount.netconnect.SslContextFactory;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * @author 王漫生
 * @date 2018-3-7
 * @project：个人中心
 */
public class TSPAccountApplication extends Application {
    public static Context applicationContext;
    private static TSPAccountApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationContext = this;

        initOkGo();
        registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
        XDroid();//基础配置
    }

    /**
     * 获取Application
     * @return TSPAccountApplication
     */
    public synchronized static TSPAccountApplication getInstance() {
        if (instance == null) {
            instance = new TSPAccountApplication();
        }
        return instance;
    }

    //基础配置
    private void XDroid(){
        BaseConfig.getInstance()
                .setLog(true)//是否显示log输出
                .setDefLogTag("TSPAccount")//默认的日志输出tag
                .setCacheSpName("TSPAccount")//配置sharedPref的文件名
                .build();//设置生效
    }

    /**
     * 获取上下文
     * @return getInstance()
     */
    public static TSPAccountApplication getContext() {
        return getInstance();
    }

    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //超时时间设置，默认60秒
        builder.readTimeout(10000L, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(10000L, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(10000L, TimeUnit.MILLISECONDS);   //全局的连接超时时间


        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        // CustomTrust ct = new CustomTrust();
        builder .addInterceptor(loggingInterceptor);

        // value 为“0”则是正式生产环境，为“1”则是德赛西威开发测试环境,为“2”则是腾讯云沙箱测试环境,由于第一次开机还未进行设置，get到的值是为空的，所以默认值按需设置为“0”，或者“1”
        String mTempMode = SystemProperties.get("persist.sv.EnvironmentMode", "0");
        if (TextUtils.isEmpty(mTempMode)) {
            try {
                SslContextFactory.SSLParams sslParams = SslContextFactory.getSslSocketFactory();
                builder.sslSocketFactory(sslParams.sSLSocketFactory,sslParams.trustManager);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            if (mTempMode.equals("0")) {
                try {
                    SslContextFactory.SSLParams sslParams = SslContextFactory.getSslSocketFactory();
                    builder.sslSocketFactory(sslParams.sSLSocketFactory,sslParams.trustManager);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        builder.hostnameVerifier(new UnSafeHostnameVerifier());
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setRetryCount(1)         ;                      //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }

    public class UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
