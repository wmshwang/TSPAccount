package com.desay_svautomotive.tspaccount.receiver;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.desay_svautomotive.tspaccount.EngineeringBasic.cache.SharedPreferencesHelper;
import com.desay_svautomotive.tspaccount.EngineeringBasic.log.XLog;
import com.desay_svautomotive.tspaccount.Utils.StringUtils;
import com.desay_svautomotive.tspaccount.netconnect.CommonUtils;
import com.desaysv.secureapp.ITaseSdkAidlInterface;
import com.desaysv.vehicle.theft.Theft;
import com.desaysv.vehicle.theft.TheftListener;
import com.desaysv.vehicle.theft.TheftService;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 王漫生
 * @date 2018-7-19
 * @project：个人中心
 */

public class InternalService extends Service implements TheftListener {
    private static final String TAG = "InternalService";
    public ITaseSdkAidlInterface iTaseSdkAidlInterface = null;

    private TheftService theftService;
    private ArrayList<Integer> send_list;
    private static InternalService instance;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public static InternalService getInstance() {
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
        XLog.e(TAG, "--------->onCreate: ");

        instance = this;
        theftService = Theft.getTheftService();
        theftService.setOnTheftListener(this);

        startTSPAccount();//异步
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
        super.onDestroy();
        unbindService(safeConnection);
        mCompositeDisposable.clear();
    }

    //环境判断
    public void contextSwitch() {
        String mTempMode = CommonUtils.contextSwitch();
        SharedPreferencesHelper.getInstance(getApplicationContext()).putString("mTempMode", mTempMode);
    }

    private ServiceConnection safeConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            XLog.e(TAG, "  safeConnection onServiceConnected");
            iTaseSdkAidlInterface = ITaseSdkAidlInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iTaseSdkAidlInterface = null;
//            XLog.e(TAG, "  safeConnection onServiceDisconnected");
        }
    };

    private void bindSafe(){
        //启动服务
        Intent intent = new Intent();
        intent.setAction("android.intent.action.TaseSdkServicetest");
        intent.setPackage("com.desaysv.secureapp");
        bindService(intent, safeConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onCmdProc(int i, ArrayList<Integer> arrayList) {
        if (i == 0x72A1) {
            StringBuffer sn = new StringBuffer();
            for (Integer j : arrayList){
                sn.append(StringUtils.convertHexToString(Integer.toHexString(j)));
            }
            if(sn.toString().length()>0){
                SharedPreferencesHelper.getInstance(getApplicationContext()).putString("device_id", sn.toString());
            }
        }else if(i == 0x741E){
            StringBuffer vin = new StringBuffer();
            for (Integer j : arrayList){
                vin.append(StringUtils.convertHexToString(Integer.toHexString(j)));
            }
            if(vin.toString().length()>0){
                String NEW_VIN = SharedPreferencesHelper.getInstance(getApplicationContext()).getString("NEW_VIN", "");
                SharedPreferencesHelper.getInstance(getApplicationContext()).putString("NEW_VIN", vin.toString());
                SharedPreferencesHelper.getInstance(getApplicationContext()).putString("OLD_VIN", NEW_VIN);
            }else{
                SharedPreferencesHelper.getInstance(getApplicationContext()).putString("NEW_VIN", "");
            }
        }
    }

    private void sendMsgToMcu(int... values) {
        send_list = new ArrayList<>();
        if (values.length > 1) {
            for (int i = 1; i < values.length; i++) {
                send_list.add(values[i]);
            }
        }
        theftService.sendCmd(values[0], send_list);
    }

    //异步
    private void startTSPAccount() {
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                try {
                    sendMsgToMcu(0x72A1);//sn
                    sendMsgToMcu(0x7243,16);//vin
                    bindSafe();//安全服务
                    contextSwitch();//环境判断
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
//                XLog.e(TAG, "  onError=" + e);
            }

            @Override
            public void onComplete() {
                XLog.e(TAG, "  onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }
}
