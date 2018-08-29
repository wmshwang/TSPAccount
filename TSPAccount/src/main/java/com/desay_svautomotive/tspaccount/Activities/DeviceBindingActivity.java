package com.desay_svautomotive.tspaccount.Activities;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.RemoteException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.desay_svautomotive.tspaccount.receiver.InternalService;
import com.desay_svautomotive.tspaccount.widget.LoadingDialog;
import com.desay_svautomotive.tspaccount.EngineeringBasic.base.XActivity;
import com.desay_svautomotive.tspaccount.EngineeringBasic.cache.SharedPreferencesHelper;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tspaccount.Utils.ACache;
import com.desay_svautomotive.tspaccount.Utils.Constant;
import com.desay_svautomotive.tspaccount.Utils.QRCodeUtil;
import com.desay_svautomotive.tspaccount.Utils.ToastUtil;
import com.desay_svautomotive.tspaccount.netconnect.CommonUtils;
import com.desay_svautomotive.tspaccount.netconnect.StringDialogCallback;
import com.desaysv.secureapp.Result;
import com.google.zxing.WriterException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * @author 王漫生
 * @date 2018-3-31
 * @project：个人中心
 */
public class DeviceBindingActivity extends XActivity {
    @BindView(R.id.ll_left)
    LinearLayout ll_left;//返回按钮
    @BindView(R.id.tv_title)
    TextView tv_title;//标题
    @BindView(R.id.has_binding)
    LinearLayout has_binding;//已绑定布局
    @BindView(R.id.device_num)
    TextView device_num;//设备编号
    @BindView(R.id.sim_num)
    TextView sim_num;//SIM卡号
    @BindView(R.id.imei_num)
    TextView imei_num;//IMEI号
    @BindView(R.id.no_binding)
    LinearLayout no_binding;//未绑定布局
    @BindView(R.id.app_qr_iv)
    ImageView app_qr_iv;//二维码

    private boolean isBind = false,isToBind = false,hasQr = false;
    private String token = "",IMEI = "",ICCID = "",lastIMEI = "",lastICCID = "";
    private String device_id = "",secret = "",VIN = "",OLD_VIN = "";
    private LoadingDialog loadingDialog;
    private Bitmap bitmap = null;
    private MyTask mTask;
    private boolean runPull = false,isFirst = true;
    private final Handler handlerPull = new Handler();
    private ACache aCache;
    private SimStateReceive simStateReceive;
    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private boolean mSIMReceiverTag = false; //广播接受者标识位
    private CountDownTimer mTimer;

    @Override
    public void initData(Bundle savedInstanceState) {
        initDatas();
        initDeviceData();
        setBroadcast();//设置广播
        toCountDown();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_device_binding;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        runPull = true;
        if(isFirst==true){
            isFirst = false;
        }else{
            if(hasQr == true){
                handlerPull.postDelayed(taskPull, 1000);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        runPull = false;
    }

    @Override
    protected void onDestroy() {
        runPull = false;
        try{handlerPull.removeCallbacks(taskPull);}catch (Exception e){e.printStackTrace();}
        unregisterReceiver(broadcastReceiver);
        if(mSIMReceiverTag==true){
            mSIMReceiverTag = false;
            try{unregisterReceiver(simStateReceive);}catch (IllegalArgumentException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onDestroy();
    }

    private void initDatas(){
        aCache = ACache.get(DeviceBindingActivity.this);
        tv_title.setText(getResources().getString(R.string.bind_device));

        isBind = getIntent().getBooleanExtra("bind",false);
        ICCID = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("ICCID", "");
        if(TextUtils.isEmpty(ICCID)){
            new ToastUtil(DeviceBindingActivity.this,R.string.get_ICCID, Toast.LENGTH_SHORT,0).show();
            getSimStateReceive();//SIM卡状态监听
        }
    }

    //设置广播
    private void setBroadcast(){
        IntentFilter filter = new IntentFilter("com.desay_svautomotive.tspaccount.readyService");
        registerReceiver(broadcastReceiver, filter);
    }

    //广播
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runPull  = false;
            handlerPull.postDelayed(taskPull, 50);
            try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
            initDeviceData();
        }
    };

    //获取设备编号及ICCID
    @SuppressLint("MissingPermission")
    private void getIMEIandICCID(){
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager.getDeviceId() != null){
            IMEI = telephonyManager.getDeviceId();
        }
        if(telephonyManager.getSimSerialNumber() != null){
            ICCID =telephonyManager.getSimSerialNumber();  //取出ICCID:集成电路卡识别码（固化在手机SIM卡中,就是SIM卡的序列号）
            ICCID = ICCID.toUpperCase();
        }
        lastIMEI = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("IMEI", "");
        lastICCID = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("ICCID", "");
        SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("IMEI", IMEI);
        SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("ICCID", ICCID);
        SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("lastIMEI", lastIMEI);
        SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("lastICCID", lastICCID);
    }

    //SIM卡状态监听
    private void getSimStateReceive(){
        mSIMReceiverTag = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SIM_STATE_CHANGED);//设置了系统网络

        simStateReceive = new SimStateReceive();
        registerReceiver(simStateReceive, intentFilter);
    }

    private class SimStateReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
                TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
                int state = tm.getSimState();
                switch (state) {
                    case TelephonyManager.SIM_STATE_READY :
                        getIMEIandICCID();
                        runPull  = false;
                        handlerPull.postDelayed(taskPull, 50);
                        try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
                        initDeviceData();
                        break;
                    case TelephonyManager.SIM_STATE_UNKNOWN :
                    case TelephonyManager.SIM_STATE_ABSENT :
                    case TelephonyManager.SIM_STATE_PIN_REQUIRED :
                    case TelephonyManager.SIM_STATE_PUK_REQUIRED :
                    case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
                    default:
                        break;
                }
            }
        }
    }

    //初始化数据
    private void initDeviceData(){
        secret = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("deviceSecret", "");
        VIN = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("NEW_VIN", "");//LDC931L27B1693269
        OLD_VIN = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("OLD_VIN", "");
        device_id = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("device_id", "");
        IMEI = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("IMEI", "");
        lastIMEI = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("lastIMEI", "");
        ICCID = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("ICCID", "");
        lastICCID = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("lastICCID", "");
        if(TextUtils.isEmpty(ICCID)){
            getIMEIandICCID();
        }

        qrQuery();//二维码查询判断
    }

    //二维码查询判断
    private void qrQuery(){


//        //-----------设置VIN---------
//        if(VIN.equals("")){
//            String data = FileUtil.getFile("save_vin.txt");
//            if(!TextUtils.isEmpty(data)){
//                VIN = data;
//                SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("VIN", VIN);
//            }
//        }else{
//            String data = FileUtil.getFile("save_vin.txt");
//            if(!TextUtils.isEmpty(data)){
//                if(!VIN.equals(data)){
//                    VIN = data;
//                    SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("VIN", VIN);
//                }
//            }
//        }
//        //-----------设置VIN---------


        if (isBind){
            has_binding.setVisibility(View.VISIBLE);
            no_binding.setVisibility(View.GONE);

            device_num.setText(device_id);
            sim_num.setText(ICCID);
            imei_num.setText(IMEI);
        }else{
            has_binding.setVisibility(View.GONE);
            no_binding.setVisibility(View.VISIBLE);

            if(TextUtils.isEmpty(VIN)){
                app_qr_iv.setImageResource(R.mipmap.no_qr_code);
                new ToastUtil(DeviceBindingActivity.this,R.string.vin_empty, Toast.LENGTH_SHORT,0).show();
                return;
            }else if(!OLD_VIN.equals(VIN)){
                SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("OLD_VIN", VIN);
                mTask = new MyTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                mTask.execute("");
            }else{
                if(TextUtils.isEmpty(ICCID)){
                    app_qr_iv.setImageResource(R.mipmap.no_qr_code);
//                    new ToastUtil(DeviceBindingActivity.this,R.string.ICCID_empty, Toast.LENGTH_SHORT,0).show();
                    return;
                }else if(!lastICCID.equals(ICCID)){
                    SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("lastICCID", ICCID);
                    mTask = new MyTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                    mTask.execute("");
                }else{
                    if(!lastIMEI.equals(IMEI)){
                        SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("lastIMEI", IMEI);
                        mTask = new MyTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                        mTask.execute("");
                    }else{
                        String mTempMode = CommonUtils.contextSwitch();
                        String oldTempMode = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("mTempMode", "");
                        if(!oldTempMode.equals(mTempMode)){
                            SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("mTempMode", mTempMode);
                            mTask = new MyTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                            mTask.execute("");
                        }else{
                            if (InternalService.getInstance() != null&&InternalService.getInstance().iTaseSdkAidlInterface != null) {
                                try {
                                    Result deviceResult = InternalService.getInstance().iTaseSdkAidlInterface.Security_encodeQRcode(ICCID+ "|"+IMEI + "|" + VIN);
                                    if(deviceResult!=null&&deviceResult.getErrCode()==0&&deviceResult.getReqBuffer()!=null){
                                        String deviceSecret = deviceResult.getReqBuffer();
                                        if(!secret.equals(deviceSecret)){
                                            secret = deviceSecret;
                                            SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("deviceSecret", secret);
                                            HasSecretTask hasSecretTask = new HasSecretTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                                            hasSecretTask.execute("deviceSecret");
                                        }else{
                                            try{
                                                bitmap = aCache.getAsBitmap("deviceBitmap");
                                            }catch (NullPointerException e){
                                                e.printStackTrace();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            if (bitmap != null) {
                                                app_qr_iv.setImageBitmap(bitmap);
                                                hasQr = true;
                                                devicePull();// 轮询
                                            }else{
                                                secret = deviceSecret;
                                                SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("deviceSecret", secret);
                                                HasSecretTask hasSecretTask = new HasSecretTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                                                hasSecretTask.execute("deviceSecret");
                                            }
                                        }
                                    }else{
                                        new ToastUtil(DeviceBindingActivity.this,R.string.data_encryption_failed, Toast.LENGTH_SHORT,0).show();
                                        app_qr_iv.setImageResource(R.mipmap.no_qr_code);
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{
                                new ToastUtil(DeviceBindingActivity.this,R.string.data_encryption_failed, Toast.LENGTH_SHORT,0).show();
                                app_qr_iv.setImageResource(R.mipmap.no_qr_code);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 用户设备绑定轮询
     */
    private void devicePull(){
        token = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("token", "");
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        JSONObject privateData = new JSONObject();
        String CAR_ID = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("CAR_ID", "");
        try{
            publicData.put("appKey", Constant.appKey);
            publicData.put("version",Constant.version);
            publicData.put("token",token);
            privateData.put("CAR_ID",CAR_ID);
            privateData.put("DEVICE_ID",device_id);
            requestData.put("public",publicData);
            requestData.put("private",privateData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkGo.<String>post(CommonUtils.getServerName()+Constant.DEVICE_LIST_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringDialogCallback(DeviceBindingActivity.this,false) {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
//                            {"resultCode":"0000","resultDesc":"操作成功","resultList":[{"DEVICE_ID":"100000000000000004","REAL_NAME":null,"ICCID":"11","MSISDN":null,"DEVICE_TYPE":"a"}]}
                            if(resultCode.equals("0000")){
                                if(response.has("resultList")){
                                    JSONArray resultList = response.getJSONArray("resultList");
                                    if(resultList.length()>0){
                                        for(int i=0;i<resultList.length();i++){
                                            JSONObject dataItem = resultList.getJSONObject(i);
                                            if (dataItem.getString("DEVICE_ID").equals(device_id)){
                                                hasQr = false;
                                                runPull = false;
                                                has_binding.setVisibility(View.VISIBLE);
                                                no_binding.setVisibility(View.GONE);

                                                isToBind = true;
                                                device_num.setText(device_id);
                                                sim_num.setText(ICCID);
                                                imei_num.setText(IMEI);
                                                break;
                                            }
                                        }
                                    }else{
                                        runPull = true;
                                        handlerPull.postDelayed(taskPull, 5000);
                                    }
                                }else{
                                    //没绑定
                                    runPull = true;
                                    handlerPull.postDelayed(taskPull, 5000);
                                }
                            }else{
                                runPull = true;
                                handlerPull.postDelayed(taskPull, 5000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        runPull = true;
                        handlerPull.postDelayed(taskPull, 5000);
                    }
                });
    }

    //再次轮询
    private final Runnable taskPull = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (runPull) {
                devicePull();// 轮询
            }
        }
    };

    /**
     * 监听Back键按下事件:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if(isToBind==true){
            intent.putExtra("type",1);//已绑定
        }else{
            intent.putExtra("type",0);//未绑定
        }
        DeviceBindingActivity.this.setResult(RESULT_OK, intent);
        super.onBackPressed();
        overridePendingTransition(0,R.anim.activity_close);
    }

    //事件处理
    private void initEvent(){
        //跳转到下载界面下载手机APP
        ll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(isToBind==true){
                    intent.putExtra("type",1);//已绑定
                }else{
                    intent.putExtra("type",0);//未绑定
                }
                DeviceBindingActivity.this.setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(0,R.anim.activity_close);
            }
        });
    }

    public void dialogShow(){
        loadingDialog = new LoadingDialog.Builder(DeviceBindingActivity.this).setShowMessage(false).setCancelable(true).setCancelOutside(false).create();
        loadingDialog.show();
    }

    public void dialogCancel(){
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {//onPreExecute方法用于在执行后台任务前做一些UI操作
            try{dialogShow();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected String doInBackground(String... params) {//doInBackground方法内部执行后台任务,不可在此方法内修改UI
            JSONObject urlData = new JSONObject();
            try {
                if (InternalService.getInstance() != null&&InternalService.getInstance().iTaseSdkAidlInterface != null) {
                    Result deviceResult = InternalService.getInstance().iTaseSdkAidlInterface.Security_encodeQRcode(ICCID+ "|"+IMEI + "|" + VIN);
                    if(deviceResult!=null&&deviceResult.getErrCode()==0&&deviceResult.getReqBuffer()!=null){
                        String  deviceSecret = deviceResult.getReqBuffer();
                        SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).putString("deviceSecret", deviceSecret);
                        urlData.put("type", "device");
                        urlData.put("device_id",device_id);
                        urlData.put("secret",deviceSecret);
                        bitmap = QRCodeUtil.CreateTwoDCode(urlData.toString());
                        aCache.put("deviceBitmap",bitmap);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }catch (WriterException e) {
                e.printStackTrace();
            }catch (RemoteException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {//onProgressUpdate方法用于更新进度信息

        }

        @Override
        protected void onPostExecute(String result) {//onPostExecute方法用于在执行完后台任务后更新UI,显示结果
            if (bitmap != null) {
                app_qr_iv.setImageBitmap(bitmap);
                hasQr = true;
                devicePull();// 轮询
            }else{
                new ToastUtil(DeviceBindingActivity.this,R.string.data_encryption_failed, Toast.LENGTH_SHORT,0).show();
                app_qr_iv.setImageResource(R.mipmap.no_qr_code);
            }
            try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected void onCancelled() {//onCancelled方法用于在取消执行中的任务时更改UI

        }
    }

    private class HasSecretTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {//onPreExecute方法用于在执行后台任务前做一些UI操作
            try{dialogShow();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected String doInBackground(String... params) {//doInBackground方法内部执行后台任务,不可在此方法内修改UI
            JSONObject urlData = new JSONObject();
            try {
                urlData.put("type", "device");
                urlData.put("device_id",device_id);
                urlData.put("secret",secret);
                bitmap = QRCodeUtil.CreateTwoDCode(urlData.toString());
                aCache.put("deviceBitmap",bitmap);
            }catch (JSONException e){
                e.printStackTrace();
            }catch (WriterException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {//onProgressUpdate方法用于更新进度信息

        }

        @Override
        protected void onPostExecute(String result) {//onPostExecute方法用于在执行完后台任务后更新UI,显示结果
            if (bitmap != null) {
                app_qr_iv.setImageBitmap(bitmap);
                hasQr = true;
                devicePull();// 轮询
            }else{
                app_qr_iv.setImageResource(R.mipmap.no_qr_code);
            }
            try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected void onCancelled() {//onCancelled方法用于在取消执行中的任务时更改UI

        }
    }

    private void toCountDown(){
        if (mTimer == null) {
            mTimer = new CountDownTimer((long) (60 * 1000), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    ICCID = SharedPreferencesHelper.getInstance(DeviceBindingActivity.this).getString("ICCID", "");
                    if(TextUtils.isEmpty(ICCID)){
                        new ToastUtil(DeviceBindingActivity.this,R.string.ICCID_empty, Toast.LENGTH_SHORT,0).show();
                    }
                }
            };
            mTimer.start();
        }

    }
}
