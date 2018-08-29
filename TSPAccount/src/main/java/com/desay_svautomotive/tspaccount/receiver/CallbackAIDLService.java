package com.desay_svautomotive.tspaccount.receiver;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.desay_svautomotive.tspaccount.EngineeringBasic.cache.SharedPreferencesHelper;
import com.desay_svautomotive.tspaccount.EngineeringBasic.log.XLog;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tsplogin.IService;
import com.desay_svautomotive.tsplogin.MessageCenter;
import com.desay_svautomotive.tsplogin.bean.LoginBean;
import com.tencent.android.tpush.XGCustomPushNotificationBuilder;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 王漫生
 * @date 2018-5-10
 * @project：个人中心
 */
public class CallbackAIDLService extends Service {
    public IService mRemoteService = null;
    private boolean mBind = false;
    private static CallbackAIDLService instance;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public static CallbackAIDLService getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        XLog.e("TSPAccount--- ", " startTSPAccount");
        startTSPAccount();//异步
    }

    //服务启动完毕
    private void readyService(){
        Intent intentMsg = new Intent("com.desay_svautomotive.tspaccount.readyService");
        getApplicationContext().sendBroadcast(intentMsg);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        if (mBind) {
            unbindService(serviceConnection);
            mBind = false;
        }
        if (mRemoteService != null) {
            try {
                mRemoteService.unregisterCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCompositeDisposable.clear();
        super.onDestroy();
    }


    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mRemoteService == null) {
                return;
            }
            mRemoteService.asBinder().unlinkToDeath(deathRecipient, 0);
            mRemoteService = null;
            //TODO:这里重写绑定远程Service
            bindLoginService();
        }
    };


    private void bindLoginService() {
        Intent intent = new Intent();
        intent.setAction("com.desay.tsplogin.aidl");
        intent.setClassName("com.desay_svautomotive.tsplogin", "com.desay_svautomotive.tsplogin.receiver.CallbackAIDLService");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            XLog.e("TSPAccount--- ", " serviceConnection onServiceConnected");
            mRemoteService = IService.Stub.asInterface(service);
            try {
                mRemoteService.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBind = true;


            try {
                service.linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
//            XLog.e("TSPAccount--- ", " serviceConnection onServiceDisconnected");
            mRemoteService = null;
            mBind = false;
        }
    };

    /**
     * service的回调方法
     */
    private MessageCenter.Stub mCallback = new MessageCenter.Stub() {
        @Override
        public LoginBean addLoginBean(LoginBean loginBean) throws RemoteException {//保存登录信息
            XLog.e("CallbackAIDL---- ",mBind+", "+loginBean.getToken()+", "+loginBean.getACCT_ID()+"  acct_id");
            SharedPreferencesHelper.getInstance(getApplicationContext()).putString("token", loginBean.getToken());
            SharedPreferencesHelper.getInstance(getApplicationContext()).putString("expires", loginBean.getExpires()+"");
            SharedPreferencesHelper.getInstance(getApplicationContext()).putString("nowTime", loginBean.getLoginTime());
            SharedPreferencesHelper.getInstance(getApplicationContext()).putString("phone", loginBean.getPhoneNum());
            SharedPreferencesHelper.getInstance(getApplicationContext()).putString("ACCT_ID", loginBean.getACCT_ID());

//            Intent intentMsg = new Intent("com.desay_svautomotive.tspaccount.sendAIDL");
//            getApplicationContext().sendBroadcast(intentMsg);


            //先注释，以后再打开
//            if(loginBean.getACCT_ID()!=null){
//                initMsg(loginBean.getACCT_ID());//注册推送
//            }


            return null;
        }
    };

    private void initMsg(String ACCT_ID){
        /*
        注册信鸽服务的接口
        如果仅仅需要发推送消息调用这段代码即可
        */
        if(TextUtils.isEmpty(ACCT_ID)){
            ACCT_ID = "*";
        }
        XGPushManager.registerPush(getApplicationContext(),ACCT_ID,
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        XLog.e(Constants.LogTag, " +++ register push sucess. token:" + data + "flag" + flag);
                    }
                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        XLog.e(Constants.LogTag, " +++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg);
                    }
                });

        // 获取token
        XGPushConfig.getToken(this);
    }

    /**
     * 设置通知自定义View，这样在下发通知时可以指定build_id。编号由开发者自己维护,build_id=0为默认设置
     *
     * @param context
     */
    @SuppressWarnings("unused")
    private void initCustomPushNotificationBuilder(Context context) {
        XGCustomPushNotificationBuilder build = new XGCustomPushNotificationBuilder();
        int id = context.getResources().getIdentifier("tixin", "raw", context.getPackageName());
        String uri = "android.resource://" + context.getPackageName() + "/" + id;
        build.setSound(Uri.parse(uri));
        // 设置自定义通知layout,通知背景等可以在layout里设置
        build.setLayoutId(R.layout.notification);
        // 设置自定义通知内容id
        build.setLayoutTextId(R.id.content);
        // 设置自定义通知标题id
        build.setLayoutTitleId(R.id.title);
        // 设置自定义通知图片id
        build.setLayoutIconId(R.id.icon);
        // 设置自定义通知图片资源
        build.setLayoutIconDrawableId(R.mipmap.ic_launcher);
        // 设置状态栏的通知小图标
        //build.setbigContentView()
        build.setIcon(R.mipmap.ic_launcher);
        // 设置时间id
        build.setLayoutTimeId(R.id.time);
        // 若不设定以上自定义layout，又想简单指定通知栏图片资源
        //build.setNotificationLargeIcon(R.drawable.ic_action_search);
        // 客户端保存build_id
        XGPushManager.setPushNotificationBuilder(this, 1, build);
        XGPushManager.setDefaultNotificationBuilder(this, build);
    }



    //异步
    private void startTSPAccount() {
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                try {
//                    XLog.e("TSPAccount--- ", " startTSPAccount 异步");
                    bindLoginService();//绑定服务
                    readyService();//服务启动完毕
                } catch (Exception exception) {
                    if (!e.isDisposed()) {
                        e.onError(exception);
                    }
                }
                e.onNext(0);
                e.onComplete();
            }

        });
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {

            }

            @Override
            public void onError(Throwable e) {
//                XLog.e("TSPAccount--- ", " onError=" + e);
            }

            @Override
            public void onComplete() {
                XLog.e("TSPAccount--- ", " onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }
}
