package com.desay_svautomotive.tspaccount.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.desay_svautomotive.tspaccount.Utils.FileUtil;
import com.desay_svautomotive.tspaccount.widget.LoadingDialog;
import com.desay_svautomotive.tspaccount.EngineeringBasic.base.XActivity;
import com.desay_svautomotive.tspaccount.EngineeringBasic.cache.SharedPreferencesHelper;
import com.desay_svautomotive.tspaccount.EngineeringBasic.kit.Kits;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tspaccount.Utils.ACache;
import com.desay_svautomotive.tspaccount.Utils.Constant;
import com.desay_svautomotive.tspaccount.Utils.QRCodeUtil;
import com.desay_svautomotive.tspaccount.Utils.ToastUtil;
import com.desay_svautomotive.tspaccount.netconnect.CommonUtils;
import com.desay_svautomotive.tspaccount.netconnect.StringDialogCallback;
import com.desay_svautomotive.tspaccount.receiver.CallbackAIDLService;
import com.google.zxing.WriterException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * @author 王漫生
 * @date 2018-3-29
 * @project：个人中心
 */
public class VehicleBindingActivity extends XActivity {
    @BindView(R.id.ll_left)
    LinearLayout ll_left;//返回按钮
    @BindView(R.id.tv_title)
    TextView tv_title;//标题
    @BindView(R.id.has_binding)
    LinearLayout has_binding;//已绑定布局
    @BindView(R.id.car_num)
    TextView car_num;//车牌号码
    @BindView(R.id.engine)
    TextView engine;//发动机号
    @BindView(R.id.vin)
    TextView vin;//车架号
    @BindView(R.id.no_binding)
    LinearLayout no_binding;//未绑定布局
    @BindView(R.id.app_qr_iv)
    ImageView app_qr_iv;//二维码

    private boolean isBind = false,isToBind = false,hasQr = false;
    private JSONObject vehicleJson = new JSONObject();
    private String device_id = "",token = "",VIN = "";
    private LoadingDialog loadingDialog;//
    private Bitmap bitmap = null;
    private MyTask mTask;
    private boolean runPull = false,isFirst = true;
    private final Handler handlerPull = new Handler();
    private ACache aCache;

    @Override
    public void initData(Bundle savedInstanceState) {
        initVehicleData();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_vehicle_binding;
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
        try{unregisterReceiver(broadcastReceiver);}catch (IllegalArgumentException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        super.onDestroy();
    }

    //设置广播
    private void setBroadcast(){
        IntentFilter filter = new IntentFilter("com.desay_svautomotive.tspaccount.sendAIDL");
        registerReceiver(broadcastReceiver, filter);
    }

    //广播
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(hasQr == false){
                if( VINNotEmpty()==true){
                    getVehicle();//用户车辆列表查询
                }
            }
        }
    };

    //初始化数据
    private void initVehicleData(){
        aCache = ACache.get(VehicleBindingActivity.this);
        tv_title.setText(getResources().getString(R.string.bind_car));
        setBroadcast();//设置广播

        VIN = SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).getString("NEW_VIN", "");
        device_id = SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).getString("device_id", "");
        isBind = getIntent().getBooleanExtra("bind",false);

        qrQuery();//二维码查询判断
    }

    //二维码查询判断
    private void qrQuery(){


//        //-----------设置VIN---------
//        if(VIN.equals("")){
//            String data = FileUtil.getFile("save_vin.txt");
//            if(!TextUtils.isEmpty(data)){
//                VIN = data;
//                SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).putString("VIN", VIN);
//            }
//        }else{
//            String data = FileUtil.getFile("save_vin.txt");
//            if(!TextUtils.isEmpty(data)){
//                if(!VIN.equals(data)){
//                    VIN = data;
//                    SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).putString("VIN", VIN);
//                }
//            }
//        }
//        //-----------设置VIN---------



        if (isBind){
            has_binding.setVisibility(View.VISIBLE);
            no_binding.setVisibility(View.GONE);

            String vehicleJsonStr = SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).getString("vehicleJson", "");
            if(vehicleJsonStr.equals("")){
                token = SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).getString("token", "");
                if(token.equals("")){
                    toLoginAndRequestAgain();
                }else{
                    if( VINNotEmpty()==true){
                        getVehicle();//用户车辆列表查询
                    }
                }
            }else{
                try {
                    VINNotEmpty();
                    vehicleJson = new JSONObject(vehicleJsonStr);
                    car_num.setText(vehicleJson.getString("PLATE_NO"));
                    engine.setText(vehicleJson.getString("ENGINE_NO"));
                    vin.setText(VIN);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else{
            has_binding.setVisibility(View.GONE);
            no_binding.setVisibility(View.VISIBLE);

            try{
                bitmap = aCache.getAsBitmap("vehicleBitmap");
            }catch (NullPointerException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            if (bitmap != null) {
                app_qr_iv.setImageBitmap(bitmap);
                hasQr = true;
                if( VINNotEmpty()==true){
                    vehiclePull();// 轮询
                }
            }else{
                mTask = new MyTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                mTask.execute("");
            }
        }
    }

    //vin是否为空
    private boolean VINNotEmpty(){
        if(TextUtils.isEmpty(VIN)){
            VIN = getResources().getString(R.string.vin_empty);
            new ToastUtil(VehicleBindingActivity.this,R.string.vin_empty, Toast.LENGTH_SHORT,0).show();
            return false;
        }
        return true;
    }

    /**
     * 用户车辆绑定轮询
     */
    private void vehiclePull(){
        token = SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).getString("token", "");
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        JSONObject privateData = new JSONObject();
        try{
            publicData.put("appKey", Constant.appKey);
            publicData.put("version",Constant.version);
            publicData.put("token",token);
            privateData.put("CAR_ID","");
            requestData.put("public",publicData);
            requestData.put("private",privateData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkGo.<String>post(CommonUtils.getServerName()+Constant.CAR_LIST_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringDialogCallback(VehicleBindingActivity.this,false) {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
                            if(resultCode.equals("0000")){
                                if(response.has("resultList")){
                                    JSONArray resultList = response.getJSONArray("resultList");
                                    if(resultList.length()>0){
                                        for(int i=0;i<resultList.length();i++){
                                            JSONObject dataItem = resultList.getJSONObject(i);
                                            if (dataItem.getString("VIN").equals(VIN)){
                                                hasQr = false;
                                                runPull = false;
                                                has_binding.setVisibility(View.VISIBLE);
                                                no_binding.setVisibility(View.GONE);

                                                isToBind = true;
                                                SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).putString("CAR_ID", dataItem.getString("CAR_ID"));
                                                car_num.setText(dataItem.getString("PLATE_NO"));
                                                engine.setText(dataItem.getString("ENGINE_NO"));
                                                vin.setText(VIN);

                                                vehicleJson = new JSONObject();
                                                vehicleJson.put("PLATE_NO",dataItem.getString("PLATE_NO"));
                                                vehicleJson.put("ENGINE_NO",dataItem.getString("ENGINE_NO"));
                                                SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).putString("vehicleJson", vehicleJson.toString());
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
                if( VINNotEmpty()==true){
                    vehiclePull();// 轮询
                }
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
        VehicleBindingActivity.this.setResult(RESULT_OK, intent);
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
                VehicleBindingActivity.this.setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(0,R.anim.activity_close);
            }
        });
    }

    /**
     * 用户车辆列表查询
     */
    private void getVehicle(){
        token = SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).getString("token", "");
        if(token.equals("")){
            return;
        }
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        JSONObject privateData = new JSONObject();
        try{
            publicData.put("appKey", Constant.appKey);
            publicData.put("version",Constant.version);
            publicData.put("token",token);
            privateData.put("CAR_ID","");
            requestData.put("public",publicData);
            requestData.put("private",privateData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkGo.<String>post(CommonUtils.getServerName()+Constant.CAR_LIST_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringDialogCallback(VehicleBindingActivity.this,true) {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
                            if(resultCode.equals("0000")){
                                if(response.has("resultList")){
                                    JSONArray resultList = response.getJSONArray("resultList");
                                    if(resultList.length()>0){
                                        for(int i=0;i<resultList.length();i++){
                                            JSONObject dataItem = resultList.getJSONObject(i);
                                            if (dataItem.getString("VIN").equals(VIN)){
                                                isToBind = true;
                                                SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).putString("CAR_ID", dataItem.getString("CAR_ID"));
                                                car_num.setText(dataItem.getString("PLATE_NO"));
                                                engine.setText(dataItem.getString("ENGINE_NO"));
                                                vin.setText(VIN);

                                                vehicleJson = new JSONObject();
                                                vehicleJson.put("PLATE_NO",dataItem.getString("PLATE_NO"));
                                                vehicleJson.put("ENGINE_NO",dataItem.getString("ENGINE_NO"));
                                                SharedPreferencesHelper.getInstance(VehicleBindingActivity.this).putString("vehicleJson", vehicleJson.toString());
                                                break;
                                            }
                                        }
                                    }else{//没绑定
                                        new ToastUtil(VehicleBindingActivity.this,R.string.failed_to_obtain_vehicle_information, Toast.LENGTH_SHORT,0).show();
                                    }
                                }else{//没绑定
                                    new ToastUtil(VehicleBindingActivity.this,R.string.failed_to_obtain_vehicle_information, Toast.LENGTH_SHORT,0).show();
                                }
                            }else if(resultCode.equals("E001")){//"token错误"


                                //先注释，以后再打开
//                                XGPushManager.unregisterPush(VehicleBindingActivity.this);//注册信鸽推送,解绑推送账号



                                toLoginAndRequestAgain();
                            }else{
                                new ToastUtil(VehicleBindingActivity.this,R.string.failed_to_obtain_vehicle_information, Toast.LENGTH_SHORT,0).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        try {
                            if (!Kits.NetWork.isNetworkAvailable(VehicleBindingActivity.this)) {
                                new ToastUtil(VehicleBindingActivity.this,R.string.network_not_connected_check_the_network, Toast.LENGTH_SHORT,0).show();
                            } else{
                                new ToastUtil(VehicleBindingActivity.this,R.string.network_failure, Toast.LENGTH_SHORT,0).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    //重新登录，登录成功后重新请求
    private void toLoginAndRequestAgain(){
        new ToastUtil(VehicleBindingActivity.this,R.string.login_has_expired_please_login_again, Toast.LENGTH_SHORT,1).show();
        try{
            if (CallbackAIDLService.getInstance() != null&&CallbackAIDLService.getInstance().mRemoteService != null){
                CallbackAIDLService.getInstance().mRemoteService.toLogin();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dialogShow(){
        loadingDialog = new LoadingDialog.Builder(VehicleBindingActivity.this).setShowMessage(false).setCancelable(true).setCancelOutside(false).create();
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
                urlData.put("type", "car");
                bitmap = QRCodeUtil.CreateTwoDCode(urlData.toString());
                aCache.put("vehicleBitmap",bitmap);
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
                if( VINNotEmpty()==true){
                    vehiclePull();// 轮询
                }
            }
            try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected void onCancelled() {//onCancelled方法用于在取消执行中的任务时更改UI

        }
    }
}
