package com.desay_svautomotive.tspaccount.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.desay_svautomotive.tspaccount.Utils.ButtonUtils;
import com.desay_svautomotive.tspaccount.Utils.FileUtil;
import com.desay_svautomotive.tspaccount.netconnect.StringDialogCallback;
import com.desay_svautomotive.tspaccount.receiver.InternalService;
import com.desay_svautomotive.tspaccount.widget.CircleImageView;
import com.desay_svautomotive.tspaccount.widget.CustomDialog;
import com.desay_svautomotive.tspaccount.widget.LoadingDialog;
import com.desay_svautomotive.tspaccount.EngineeringBasic.base.XActivity;
import com.desay_svautomotive.tspaccount.EngineeringBasic.cache.SharedPreferencesHelper;
import com.desay_svautomotive.tspaccount.EngineeringBasic.kit.Kits;
import com.desay_svautomotive.tspaccount.EngineeringBasic.log.XLog;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tspaccount.Utils.ACache;
import com.desay_svautomotive.tspaccount.Utils.Constant;
import com.desay_svautomotive.tspaccount.Utils.QRCodeUtil;
import com.desay_svautomotive.tspaccount.Utils.StringUtils;
import com.desay_svautomotive.tspaccount.Utils.ToastUtil;
import com.desay_svautomotive.tspaccount.bean.XGNotification;
import com.desay_svautomotive.tspaccount.common.NotificationService;
import com.desay_svautomotive.tspaccount.netconnect.CommonUtils;
import com.desay_svautomotive.tspaccount.receiver.CallbackAIDLService;
import com.desaysv.secureapp.Result;
import com.desaysv.vehicle.TspBigDataPointService.ILogStrategyInterface;
import com.desaysv.vehicle.TspBigDataPointService.log.LogDataInterfaceManager;
import com.desaysv.vehicle.TspBigDataPointService.log.LogDataUtil;
import com.desaysv.vehicle.theft.Theft;
import com.desaysv.vehicle.theft.TheftListener;
import com.desaysv.vehicle.theft.TheftService;
import com.google.zxing.WriterException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * @author 王漫生
 * @date 2018-3-29
 * @project：个人中心
 */
public class PersonCenterActivity extends XActivity implements TheftListener {//
    @BindView(R.id.no_net)
    RelativeLayout no_net;//没数据
    @BindView(R.id.no_net_txt)
    TextView no_net_txt;//没数据提示
    @BindView(R.id.exit_btn)
    TextView exit_btn;//退出账号
    @BindView(R.id.head)
    CircleImageView head;//头像
    @BindView(R.id.name)
    TextView name;//姓名
    @BindView(R.id.has_service_tv)
    TextView has_service_tv;//姓名
    @BindView(R.id.music_icon)
    ImageView music_icon;//音乐会员icon
    @BindView(R.id.rescue_icon)
    ImageView rescue_icon;//救援icon
    @BindView(R.id.already_purchased_service)
    RelativeLayout already_purchased_service;//已购服务一栏
    @BindView(R.id.new_new)
    RelativeLayout new_new;//消息一栏
    @BindView(R.id.num_icon)
    TextView num_icon;//消息红点
    @BindView(R.id.num)
    TextView num;//未读消息
    @BindView(R.id.name_right)
    TextView name_right;//右侧姓名
    @BindView(R.id.phone_num)
    TextView phone_num;//电话号码
    @BindView(R.id.vehicle_binding)
    RelativeLayout vehicle_binding;//车辆绑定一栏
    @BindView(R.id.car_info)
    TextView car_info;//车辆是否绑定
    @BindView(R.id.bind_num_icon)
    TextView bind_num_icon;//车辆绑定红点提示
    @BindView(R.id.device_binding)
    RelativeLayout device_binding;//设备绑定一栏
    @BindView(R.id.device_tv)
    TextView device_tv;//设备行题
    @BindView(R.id.device_info)
    TextView device_info;//设备是否绑定
    @BindView(R.id.device_num_icon)
    TextView device_num_icon;//设备绑定红点提示

    private static String TAG = "PersonCenterActivity";
    private JSONObject vehicleJson = new JSONObject();
    private boolean isVehicleBind = false;
    private boolean isDeviceBind = false;
    private List<XGNotification> msgList = new ArrayList<XGNotification>();
    private String language,IMEI = "",ICCID = "",lastIMEI = "",lastICCID = "";
    private String device_id = "",token ="",VIN = "",OLD_VIN = "";
    private NotificationService notificationService;
    private static final int lineSize = Integer.MAX_VALUE;// 每次显示数
    private ChangeReceiver changeReceiver;
    private boolean hasData = true,isFirst = true,isMusic = false,isRescue = false;
    private MyTask mTask;
    private ACache aCache;
    private static LogDataInterfaceManager  in = null;
    private static boolean isAllowUpload = false,isNoCollection = false,isBigdataRegister = false;//
    private LoadingDialog loadingDialog;
    private TheftService theftService;
    private ArrayList<Integer> send_list;

    @Override
    public void initData(Bundle savedInstanceState) {
        getMcu();//从MCU获取sn和vin
        getIMEIandICCID();//获取设备编号及ICCID
        initPersonData();//初始化数据
        initEvent();//事件处理
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_person_center;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(aidlBroadcastReceiver);
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(changeReceiver);

        if(isBigdataRegister == true){
            in.unregisterCallback(LogDataUtil.FUNCTION_TYPE_BIT.PERSONAL_CENTER);
        }
        in.unBindService(this);
    }

    //系统网络是否已更改
    private void getSystemNetworkChange(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//设置了系统网络

        changeReceiver = new ChangeReceiver();
        registerReceiver(changeReceiver, intentFilter);
    }

    private class ChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
                if(Kits.NetWork.isNetworkAvailable(PersonCenterActivity.this)){//有网
                    try{OkGo.getInstance().cancelTag(this);}catch (Exception e){e.printStackTrace();}
                    no_net.setVisibility(View.GONE);
                    if(hasData ==false){
                        XLog.e(TAG,"    android.net.conn.CONNECTIVITY_CHANGE");
                        getNetUserInfo();//用户基本信息查询
                    }
                }
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initPersonData(){
        notificationService = NotificationService.getInstance(this);
        device_id = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("device_id", "");

        Locale locale = Locale.getDefault();
        language = locale.getLanguage();// 获取当前系统语言

        already_purchased_service.setEnabled(false);
        new_new.setEnabled(false);
        vehicle_binding.setEnabled(false);
        device_binding.setEnabled(false);

        getUserData();
        setBroadcast();//设置广播
        setAidlBroadcast();//设置aidl广播
        bigData();//大数据
        getSystemNetworkChange();//系统网络是否已更改
    }

    //上传数据——数据埋点
    public static void upData(int action_code){
        XLog.e(TAG,"  upData-- "+"TSPAccount PersonCenterActivity ------ big  "+action_code);
        if(isAllowUpload == true&&isNoCollection == false){
            JSONObject allData = new JSONObject();//所有数据都填入该JSon
            try {
                allData.put(LogDataUtil.KEY_FUNCTION_TYPE,LogDataUtil.FUNCTION_TYPE_BIT.PERSONAL_CENTER);//添加应用id号必填
                allData.put(LogDataUtil.KEY_APP_ID,"tspaccount");//添加应用id号必填
                JSONObject functionData = new JSONObject();//必填
                JSONObject functionActionData = new JSONObject();//非必填，暂时目前所有 apk都有
                //动作收集
                functionActionData.put("action_code",action_code);//收集打开动作
                functionData.put("action_data",functionActionData);
                allData.put(LogDataUtil.KEY_FUNCTION_DATA,functionData);
                String timeStap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:mmm").format(new Date());
                allData.put(LogDataUtil.KEY_TIME_STAMP,timeStap);
                in.addOneLogRecord(allData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //大数据
    private void bigData(){
        in = LogDataInterfaceManager.getInstances();
        in.setContext(PersonCenterActivity.this);
        in.bindService(PersonCenterActivity.this,new LogDataInterfaceManager.LogDataServiceConnection(){
            @Override
            public void onServiceConnected() {
//                XLog.e(TAG,"   onServiceConnected-- "+"TSPAccount PersonCenterActivity ------ big");
                isBigdataRegister = true;
                in.registerCallback(LogDataUtil.FUNCTION_TYPE_BIT.PERSONAL_CENTER, new ILogStrategyInterface.Stub() {
                    @Override
                    public void onResponseStrategy(boolean b, long l, boolean b1) throws RemoteException {
                        XLog.e(TAG,"  StrategyImpl-- "+b+", "+b1+"  TSPAccount  PersonCenterActivity");
                        isAllowUpload = b;
                        isNoCollection = b1;
                    }
                });
                in.judgeAllowUpLoad(LogDataUtil.FUNCTION_TYPE_BIT.PERSONAL_CENTER);
            }
            @Override
            public void onServiceDisconnected() {

            }
        });
    }

    //设置aidl广播
    private void setAidlBroadcast(){
        IntentFilter filter = new IntentFilter("com.desay_svautomotive.tspaccount.sendAIDL");
        registerReceiver(aidlBroadcastReceiver, filter);
    }

    //aidl广播
    BroadcastReceiver aidlBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isFirst==true){
                isFirst = false;
            }else{
                vehicle_binding.setEnabled(false);
                device_binding.setEnabled(false);
                getUserData();
            }
        }
    };

    //设置广播
    private void setBroadcast(){
        IntentFilter filter = new IntentFilter("com.desay_svautomotive.tspaccount.sendMsg");
        registerReceiver(broadcastReceiver, filter);
    }

    //广播
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            msgNum();    //未读消息
        }
    };

    //未读消息
    private void msgNum(){
        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        msgList.clear();
        msgList = notificationService.getScrollData(1, lineSize, "");
        if(msgList == null||msgList.size()==0){//没有新消息
            num_icon.setVisibility(View.GONE);
            num.setText("");
        }else{//有新消息
            int numInt = 0;
            for(int i =0;i<msgList.size();i++){
                long timeBetween = StringUtils.getTimeCom(msgList.get(i).getUpdate_time(),nowDate);
                if(timeBetween>=24*60*60*1000*7){//24*60*60*1000*7
                    notificationService.delete(msgList.get(i).getMsg_id());
                    msgList.remove(i);
                }else{
                    if(msgList.get(i).getType()==0){
                        numInt++;
                    }
                }
            }
            if(numInt==0){
                num_icon.setVisibility(View.GONE);
                num.setText("");
            }else if(numInt>0&&numInt<=99){
                if(language.equals("en")){
                    num.setText(numInt+"");
                    num_icon.setVisibility(View.VISIBLE);
                }else{
                    num.setText(getString(R.string.unread)+numInt+getString(R.string.article));
                    num_icon.setVisibility(View.VISIBLE);
                }
            }else{
                if(language.equals("en")){
                    num.setText(numInt+"+");
                    num_icon.setVisibility(View.VISIBLE);
                }else{
                    num.setText(getString(R.string.unread)+numInt+"+");
                    num_icon.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void getUserData(){
        token = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("token", "");
        if(token.equals("")){
            XLog.e(TAG,"  getUserData   token   null");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toLoginAndRequestAgain(R.string.you_did_not_login_please_login);
                }
            }, 1000);//1秒后执行Runnable中的run方法
        }else{
            if (!Kits.NetWork.isNetworkAvailable(PersonCenterActivity.this)) {
                new ToastUtil(PersonCenterActivity.this,R.string.network_not_connected_check_the_network, Toast.LENGTH_SHORT,0).show();
                no_net.setVisibility(View.VISIBLE);
                no_net_txt.setText(getResources().getString(R.string.network_not_connected_please_check_the_network));
                hasData = false;
            } else{
                no_net.setVisibility(View.GONE);
                getUserInfo();//用户基本信息查询
            }
        }
    }

    /**
     * 事件处理
     */
    private void initEvent(){
        //音乐会员icon
        music_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonUtils.isFastDoubleClick(R.id.music_icon)) {
                    if(isMusic==true){
                        new ToastUtil(PersonCenterActivity.this,R.string.purchased_music_members, Toast.LENGTH_SHORT,0).show();
                    }else{
                        new ToastUtil(PersonCenterActivity.this,R.string.no_music_member_purchased, Toast.LENGTH_SHORT,0).show();
                    }
                }
            }
        });

        //救援icon
        rescue_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonUtils.isFastDoubleClick(R.id.rescue_icon)) {
                    if(isRescue==true){
                        new ToastUtil(PersonCenterActivity.this,R.string.rescue_card_has_been_purchased, Toast.LENGTH_SHORT,0).show();
                    }else{
                        new ToastUtil(PersonCenterActivity.this,R.string.no_rescue_card_were_purchased, Toast.LENGTH_SHORT,0).show();
                    }
                }
            }
        });


        //点击退出账号
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog customDialog = new CustomDialog(PersonCenterActivity.this);
                customDialog.setContent(getString(R.string.are_you_sure_to_quit_your_account));
                customDialog.setYesBtnText(PersonCenterActivity.this.getString(R.string.confirm_txt));
                customDialog.setNoBtnText(PersonCenterActivity.this.getString(R.string.cancel_txt));
                customDialog.setCustomOnClickListener(new CustomDialog.OnCustomDialogListener() {
                    @Override
                    public void setYesOnClick() {
                        customDialog.dismiss();
                        toLogout();//退出登录
                    }
                    @Override
                    public void setNoOnClick() {
                        customDialog.dismiss();
                    }
                });
                customDialog.show(); //这一句不可漏
                WindowManager.LayoutParams lp = customDialog.getWindow().getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                customDialog.getWindow().setAttributes(lp);
            }
        });

        //进入已购服务
        already_purchased_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(PersonCenterActivity.this,AlreadyPurchasedServiceActivity.class);
//                startActivity(intent);
            }
        });

        //进入消息中心
        new_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先注释，以后再打开
//                Intent intent =new Intent(PersonCenterActivity.this,MyNewsActivity.class);
//                startActivityForResult(intent,Constant.REQ_CODE_1);
            }
        });

        //进入车辆绑定页面
        vehicle_binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PersonCenterActivity.this,VehicleBindingActivity.class);
                if(isVehicleBind==true){
                    intent.putExtra("bind",true);
                }else{
                    intent.putExtra("bind",false);
                }
                startActivityForResult(intent,Constant.REQ_CODE_2);
            }
        });

        //进入设备绑定页面
        device_binding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PersonCenterActivity.this,DeviceBindingActivity.class);
                if(isDeviceBind==true){
                    intent.putExtra("bind",true);
                }else{
                    intent.putExtra("bind",false);
                }
                startActivityForResult(intent,Constant.REQ_CODE_3);
            }
        });
    }

    //退出账号时处理
    private void logoutEvent(){
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("token", "");
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("expires", "");
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("nowTime", "");
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("phone", "");
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("CAR_ID", "");

        isVehicleBind = false;
        isDeviceBind = false;


        //先注释，以后再打开
//        XGPushManager.unregisterPush(PersonCenterActivity.this);//解绑推送账号



        String mTempMode = CommonUtils.contextSwitch();
        String oldTempMode = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("mTempMode", "");
        if(!oldTempMode.equals(mTempMode)){
            SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("mTempMode", mTempMode);
            notificationService.deleteAll();
        }

        new ToastUtil(PersonCenterActivity.this,R.string.exiting_account, Toast.LENGTH_SHORT,1).show();
        try{
            if (CallbackAIDLService.getInstance() == null){
                XLog.e("     ====logoutEvent===    ","  getInstance   null");
            } else if(CallbackAIDLService.getInstance().mRemoteService == null){
                XLog.e("     ====logoutEvent===    ","  mRemoteService   null");
            }

            if (CallbackAIDLService.getInstance() != null&&CallbackAIDLService.getInstance().mRemoteService != null){
                CallbackAIDLService.getInstance().mRemoteService.toLogin();
                finish();
            }else{
                goToLogin();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            goToLogin();
        }catch (Exception e){
            e.printStackTrace();
            goToLogin();
        }
    }

    private void goToLogin(){
        try {
            Intent intent=new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //参数是包名，类全限定名，注意直接用类名不行
            ComponentName cn = new ComponentName("com.desay_svautomotive.tsplogin", "com.desay_svautomotive.tsplogin.ui.Activities.LoginActivity");
            intent.setComponent(cn);
            startActivity(intent);
            finish();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出登录
     */
    private void toLogout(){
        token = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("token", "");
        if(token.equals("")){
            logoutEvent();    //退出账号时处理
            return;
        }
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        try{
            publicData.put("appKey", Constant.appKey);
            publicData.put("version",Constant.version);
            publicData.put("token",token);
            requestData.put("public",publicData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkGo.<String>post(CommonUtils.getServerName()+Constant.LOGOUT_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringDialogCallback(PersonCenterActivity.this,true) {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");//Z003 退出登录失败   0000 退出成功
                            if(resultCode.equals("0000")||resultCode.equals("Z003")){//"退出登录操作成功"
                                logoutEvent();    //退出账号时处理
                            }else{
                                new ToastUtil(PersonCenterActivity.this,R.string.failed_to_logout, Toast.LENGTH_SHORT,0).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        try {
                            if (!Kits.NetWork.isNetworkAvailable(PersonCenterActivity.this)) {
                                new ToastUtil(PersonCenterActivity.this,R.string.network_not_connected_check_the_network, Toast.LENGTH_SHORT,0).show();
                            } else{
                                new ToastUtil(PersonCenterActivity.this,R.string.logout_failure, Toast.LENGTH_SHORT,0).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void nodata(){
        name.setText("");
        has_service_tv.setText("");
        name_right.setText("");
        phone_num.setText("");
        bind_num_icon.setVisibility(View.GONE);
        car_info.setText("");
        device_num_icon.setVisibility(View.GONE);
        device_info.setText("");
        device_tv.setTextColor(getResources().getColor(R.color.account_info));
        device_binding.setEnabled(false);

        music_icon.setVisibility(View.GONE);
        rescue_icon.setVisibility(View.GONE);
        music_icon.setSelected(false);
        rescue_icon.setSelected(false);
        isMusic = false;
        isRescue = false;
    }

    public void dialogShow(){
        loadingDialog = new LoadingDialog.Builder(PersonCenterActivity.this).setShowMessage(false).setCancelable(true).setCancelOutside(false).create();
        loadingDialog.show();
    }

    public void dialogCancel(){
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 用户基本信息查询
     */
    private void getUserInfo(){
        token = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("token", "");
        if(token.equals("")){
            hasData = false;
            return;
        }
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        try{
            publicData.put("appKey", Constant.appKey);
            publicData.put("version",Constant.version);
            publicData.put("token",token);
            requestData.put("public",publicData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        try{dialogShow();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
        OkGo.<String>post(CommonUtils.getServerName()+Constant.USER_ACCOUNT_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
                        try {
                            hasData = true;
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
                            if(resultCode.equals("0000")){//"操作成功"
                                JSONArray resultList = response.getJSONArray("resultList");
                                JSONObject dataItem = resultList.getJSONObject(0);
                                //头像获取及显示
                                Glide.with(PersonCenterActivity.this).load(dataItem.getString("IMAGE")).error(R.mipmap.user_head).into(head);
                                name.setText(dataItem.getString("NICKNAME"));
                                name_right.setText(dataItem.getString("NICKNAME"));
                                //电话号码显示
                                String phoneStr = dataItem.getString("PHONE");
                                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("phone", phoneStr);
                                if(phoneStr.length()<=7){
                                    phone_num.setText(phoneStr+"****");
                                }else{
                                    phoneStr = phoneStr.substring(0,4)+"****"+phoneStr.substring(phoneStr.length()-3,phoneStr.length());
                                    phone_num.setText(phoneStr);
                                }

//                                msgNum();//显示未读消息
                                has_service_tv.setText("");
                                new_new.setEnabled(true);
                                already_purchased_service.setEnabled(true);

                                getVehicle();//用户车辆列表查询
                                getProduct();//产品介绍
                            }else{
                                nodata();
                                if(resultCode.equals("E001")){//"token错误"


                                    //先注释，以后再打开
//                                    XGPushManager.unregisterPush(PersonCenterActivity.this);//注册信鸽推送,解绑推送账号



                                    toLoginAndRequestAgain(R.string.login_has_expired_please_login_again);
                                }else if(resultCode.equals("Z028")){
                                    //"该用户未绑定cpsp"
                                }else{
                                    new ToastUtil(PersonCenterActivity.this,R.string.failed_to_get_user_information, Toast.LENGTH_SHORT,0).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        try{dialogCancel();}catch (NullPointerException e){e.printStackTrace();}catch (Exception e){e.printStackTrace();}
                        XLog.e(TAG,"  onError-- "+response.code());
                        try {
                            hasData = false;
                            if (!Kits.NetWork.isNetworkAvailable(PersonCenterActivity.this)) {
                                new ToastUtil(PersonCenterActivity.this,R.string.network_not_connected_check_the_network, Toast.LENGTH_SHORT,0).show();
                                no_net_txt.setText(getResources().getString(R.string.network_not_connected_please_check_the_network));
                            } else{
                                new ToastUtil(PersonCenterActivity.this,R.string.network_failure, Toast.LENGTH_SHORT,0).show();
                                no_net_txt.setText(getResources().getString(R.string.network_failure));
                            }
                            no_net.setVisibility(View.VISIBLE);

                            nodata();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    /**
     * 用户基本信息查询_网络监听
     */
    private void getNetUserInfo(){
        token = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("token", "");
        if(token.equals("")){
            XLog.e(TAG,"  getNetUserInfo   token   null");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toLoginAndRequestAgain(R.string.you_did_not_login_please_login);
                }
            }, 1000);//1秒后执行Runnable中的run方法
            return;
        }
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        try{
            publicData.put("appKey", Constant.appKey);
            publicData.put("version",Constant.version);
            publicData.put("token",token);
            requestData.put("public",publicData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkGo.<String>post(CommonUtils.getServerName()+Constant.USER_ACCOUNT_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
                            if(resultCode.equals("0000")){//"操作成功"
                                JSONArray resultList = response.getJSONArray("resultList");
                                JSONObject dataItem = resultList.getJSONObject(0);
                                //头像获取及显示
                                Glide.with(PersonCenterActivity.this).load(dataItem.getString("IMAGE")).error(R.mipmap.user_head).into(head);
                                name.setText(dataItem.getString("NICKNAME"));
                                name_right.setText(dataItem.getString("NICKNAME"));
                                //电话号码显示
                                String phoneStr = dataItem.getString("PHONE");
                                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("phone", phoneStr);
                                if(phoneStr.length()<=7){
                                    phone_num.setText(phoneStr+"****");
                                }else{
                                    phoneStr = phoneStr.substring(0,4)+"****"+phoneStr.substring(phoneStr.length()-3,phoneStr.length());
                                    phone_num.setText(phoneStr);
                                }

//                                msgNum();//显示未读消息
                                has_service_tv.setText("");
                                new_new.setEnabled(true);
                                already_purchased_service.setEnabled(true);

                                getVehicle();//用户车辆列表查询
                                getProduct();//产品介绍
                            }else{
                                nodata();
                                if(resultCode.equals("E001")){//"token错误"



                                    //先注释，以后再打开
//                                    XGPushManager.unregisterPush(PersonCenterActivity.this);//注册信鸽推送,解绑推送账号



                                    toLoginAndRequestAgain(R.string.login_has_expired_please_login_again);
                                }else if(resultCode.equals("Z028")){
                                    //"该用户未绑定cpsp"
                                }else{
                                    new ToastUtil(PersonCenterActivity.this,R.string.failed_to_get_user_information, Toast.LENGTH_SHORT,0).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        try {
                            if (!Kits.NetWork.isNetworkAvailable(PersonCenterActivity.this)) {
                                new ToastUtil(PersonCenterActivity.this,R.string.network_not_connected_check_the_network, Toast.LENGTH_SHORT,0).show();
                                no_net_txt.setText(getResources().getString(R.string.network_not_connected_please_check_the_network));
                            } else{
                                new ToastUtil(PersonCenterActivity.this,R.string.network_failure, Toast.LENGTH_SHORT,0).show();
                                no_net_txt.setText(getResources().getString(R.string.network_failure));
                            }
                            no_net.setVisibility(View.VISIBLE);

                            nodata();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    /**
     * 产品介绍
     */
    private void getProduct(){
        music_icon.setSelected(false);
        rescue_icon.setSelected(false);
        isMusic = false;
        isRescue = false;

        token = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("token", "");
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        JSONObject privateData = new JSONObject();
        try{
            publicData.put("appKey", Constant.appKey);
            publicData.put("version",Constant.version);
            publicData.put("token",token);
            requestData.put("public",publicData);
            requestData.put("private",privateData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkGo.<String>post(CommonUtils.getServerName()+Constant.PRODUCT_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
                            if(resultCode.equals("0000")){
                                JSONArray resultList = response.getJSONArray("resultList");
                                music_icon.setVisibility(View.VISIBLE);
                                rescue_icon.setVisibility(View.VISIBLE);
                                for(int i=0;i<resultList.length();i++){
                                    JSONObject dataItem = resultList.getJSONObject(i);
                                    if(dataItem.getString("CATEGORY").equals("pc02")){//音乐
                                        if(dataItem.getString("ORDER_STATUS").equals("0")){//0：不存在订购关系
                                            music_icon.setSelected(false);
                                            isMusic = false;
                                        }else if(dataItem.getString("ORDER_STATUS").equals("1")){//1：存在订购关系
                                            music_icon.setSelected(true);
                                            isMusic = true;
                                            break;
                                        }
                                    }
                                }

                                for(int i=0;i<resultList.length();i++){
                                    JSONObject dataItem = resultList.getJSONObject(i);
                                    if(dataItem.getString("CATEGORY").equals("pc03")){//救援
                                        if(dataItem.getString("ORDER_STATUS").equals("0")){//0：不存在订购关系
                                            rescue_icon.setSelected(false);
                                            isRescue = false;
                                        }else if(dataItem.getString("ORDER_STATUS").equals("1")){//1：存在订购关系
                                            rescue_icon.setSelected(true);
                                            isRescue = true;
                                            break;
                                        }
                                    }
                                }
                            }else{
                                music_icon.setVisibility(View.VISIBLE);
                                rescue_icon.setVisibility(View.VISIBLE);
                                music_icon.setSelected(false);
                                rescue_icon.setSelected(false);
                                isMusic = false;
                                isRescue = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        music_icon.setVisibility(View.VISIBLE);
                        rescue_icon.setVisibility(View.VISIBLE);
                        music_icon.setSelected(false);
                        rescue_icon.setSelected(false);
                        isMusic = false;
                        isRescue = false;
                    }
                });
    }

    //车辆未绑定
    private void noVehicleBing(){
        bind_num_icon.setVisibility(View.VISIBLE);
        car_info.setText(R.string.no_binding);
        vehicle_binding.setEnabled(true);
        device_num_icon.setVisibility(View.GONE);
        device_info.setText("");
        device_tv.setTextColor(getResources().getColor(R.color.account_info));
        device_binding.setEnabled(false);
    }

    //获取车辆绑定信息失败
    private void noVehicle(){
        bind_num_icon.setVisibility(View.GONE);
        car_info.setText("");
        vehicle_binding.setEnabled(false);
        device_num_icon.setVisibility(View.GONE);
        device_info.setText("");
        device_tv.setTextColor(getResources().getColor(R.color.account_info));
        device_binding.setEnabled(false);
    }

    /**
     * 用户车辆列表查询
     */
    private void getVehicle(){
        if(TextUtils.isEmpty(VIN)){
            bind_num_icon.setVisibility(View.VISIBLE);
            car_info.setText(R.string.vin_no_get);
            vehicle_binding.setEnabled(true);
            device_num_icon.setVisibility(View.GONE);
            device_info.setText("");
            device_tv.setTextColor(getResources().getColor(R.color.account_info));
            device_binding.setEnabled(false);
            return;
        }

        token = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("token", "");
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
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
                            if(resultCode.equals("0000")){
                                if(response.has("resultList")){
                                    JSONArray resultList = response.getJSONArray("resultList");
                                    for(int i=0;i<resultList.length();i++){
                                        JSONObject dataItem = resultList.getJSONObject(i);
                                        XLog.e(TAG,"  VIN---- "+VIN+", "+dataItem.getString("VIN"));
                                        if (dataItem.getString("VIN").equals(VIN)){
                                            SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("CAR_ID", dataItem.getString("CAR_ID"));
                                            isVehicleBind = true;
                                            vehicleJson = new JSONObject();
                                            vehicleJson.put("PLATE_NO",dataItem.getString("PLATE_NO"));
                                            vehicleJson.put("ENGINE_NO",dataItem.getString("ENGINE_NO"));
                                            SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("vehicleJson", vehicleJson.toString());
                                            break;
                                        }
                                    }
                                    if(isVehicleBind==true){
                                        bind_num_icon.setVisibility(View.GONE);
                                        car_info.setText(R.string.has_bind);
                                        vehicle_binding.setEnabled(true);
                                        getDevice();//用户设备信息查询
                                    }else{
                                        noVehicleBing();
                                    }
                                }else{
                                    noVehicleBing();
                                }

                            }else{
                                noVehicle();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        noVehicle();
                    }

                });
    }

    //设备未绑定
    private void noDeviceBing(){
        if(isVehicleBind==true){
            device_num_icon.setVisibility(View.VISIBLE);
            device_info.setText(R.string.no_binding);
            device_tv.setTextColor(getResources().getColor(R.color.text_White));
            device_binding.setEnabled(true);
        }else{
            device_num_icon.setVisibility(View.GONE);
            device_info.setText(R.string.no_binding);
            device_tv.setTextColor(getResources().getColor(R.color.account_info));
            device_binding.setEnabled(false);
        }
    }

    //设备绑定失败
    private void noDevice(){
        device_num_icon.setVisibility(View.GONE);
        device_info.setText("");
        device_tv.setTextColor(getResources().getColor(R.color.account_info));
        device_binding.setEnabled(false);
    }

    /**
     * 用户设备信息查询
     */
    private void getDevice(){
        token = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("token", "");
        JSONObject requestData = new JSONObject();
        JSONObject publicData = new JSONObject();
        JSONObject privateData = new JSONObject();
        String CAR_ID = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("CAR_ID", "");

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
                .execute(new StringCallback() {
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
                                            if (dataItem.getString("DEVICE_ID").equals(device_id)){
                                                isDeviceBind = true;
                                                break;
                                            }
                                        }
                                        if(isDeviceBind==true){
                                            device_info.setText(R.string.has_bind);
                                            device_tv.setTextColor(getResources().getColor(R.color.text_White));
                                            device_num_icon.setVisibility(View.GONE);
                                            device_binding.setEnabled(true);
                                        }else{
                                            noDeviceBing();
                                        }
                                    }else{
                                        noDeviceBing();
                                    }
                                }else{
                                    noDeviceBing();
                                }
                            }else{
                                noDevice();
                            }
                            vehicle_binding.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        noDevice();
                    }

                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQ_CODE_1) {//未读消息更新
//                msgNum();//显示未读消息
            } else if (requestCode == Constant.REQ_CODE_2) {//车辆绑定
                int  type = data.getExtras().getInt("type");
                if(type==1){//已绑定
                    isVehicleBind = true;
                    bind_num_icon.setVisibility(View.GONE);
                    car_info.setText(R.string.has_bind);

                    device_num_icon.setVisibility(View.VISIBLE);
                    device_info.setText(R.string.no_binding);
                    device_tv.setTextColor(getResources().getColor(R.color.text_White));
                    device_binding.setEnabled(true);
                }
            }else if (requestCode == Constant.REQ_CODE_3) {//设备绑定
                int  deviceType = data.getExtras().getInt("type");
                if(deviceType==1){//已绑定
                    isDeviceBind = true;
                    device_info.setText(R.string.has_bind);
                    device_tv.setTextColor(getResources().getColor(R.color.text_White));
                    device_num_icon.setVisibility(View.GONE);
                    device_binding.setEnabled(true);
                }
            }
        }
    }

    //重新登录
    private void toLoginAndRequestAgain(int info){
        new ToastUtil(PersonCenterActivity.this,info, Toast.LENGTH_SHORT,1).show();
        try{
            if (CallbackAIDLService.getInstance() == null){
                XLog.e("     ====toLoginAndRequestAgain===    ","  getInstance   null");
            } else if(CallbackAIDLService.getInstance().mRemoteService == null){
                XLog.e("     ====toLoginAndRequestAgain===    ","  mRemoteService   null");
            }

            if (CallbackAIDLService.getInstance() != null&&CallbackAIDLService.getInstance().mRemoteService != null){
                CallbackAIDLService.getInstance().mRemoteService.toLogin();
                finish();
            }else{
                goToLogin();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            goToLogin();
        }catch (Exception e){
            e.printStackTrace();
            goToLogin();
        }
    }

    //从MCU获取sn和vin
    private void getMcu(){
        theftService = Theft.getTheftService();
        theftService.setOnTheftListener(this);
        sendMsgToMcu(0x72A1);//sn
        sendMsgToMcu(0x7243,16);//vin
    }

    //获取设备编号及ICCID
    @SuppressLint("MissingPermission")
    private void getIMEIandICCID(){
        aCache = ACache.get(PersonCenterActivity.this);

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager.getDeviceId() != null){
            IMEI = telephonyManager.getDeviceId();
        }
        if(telephonyManager.getSimSerialNumber() != null){
            ICCID =telephonyManager.getSimSerialNumber();  //取出ICCID:集成电路卡识别码（固化在手机SIM卡中,就是SIM卡的序列号）
            ICCID = ICCID.toUpperCase();
        }
        lastIMEI = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("IMEI", "");
        lastICCID = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("ICCID", "");
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("IMEI", IMEI);
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("ICCID", ICCID);
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("lastIMEI", lastIMEI);
        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("lastICCID", lastICCID);
        getSecret();//加密数据
    }

    private void getSecret(){
        VIN = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("NEW_VIN", "");
        OLD_VIN = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("OLD_VIN", "");

        //---------------------- 车辆二维码---------------
        Bitmap vehicleBitmap = null;
        try{
            vehicleBitmap = aCache.getAsBitmap("vehicleBitmap");
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (vehicleBitmap == null) {
            mTask = new MyTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
            mTask.execute("car","1");//String type = "0";//"0"设备编码 "1"车辆绑定
        }
        //---------------------- 车辆二维码---------------


        //---------------------- 设备二维码---------------


//        //-----------设置VIN---------
//        if(VIN.equals("")){
//            String data = FileUtil.getFile("save_vin.txt");
//            if(!TextUtils.isEmpty(data)){
//                VIN = data;
//                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("VIN", VIN);
//            }
//        }else{
//            String data = FileUtil.getFile("save_vin.txt");
//            if(!TextUtils.isEmpty(data)){
//                if(!VIN.equals(data)){
//                    VIN = data;
//                    SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("VIN", VIN);
//                }
//            }
//        }
//        //-----------设置VIN---------


        if(TextUtils.isEmpty(VIN)){
            return;
        }else if(!OLD_VIN.equals(VIN)){
            SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("OLD_VIN", VIN);
            signature();//签名
        }else{
            if(TextUtils.isEmpty(ICCID)){
                return;
            }else if(!lastICCID.equals(ICCID)){
                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("lastICCID", ICCID);
                signature();//签名
            }else{
                if(!lastIMEI.equals(IMEI)){
                    SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("lastIMEI", IMEI);
                    signature();//签名
                }else{
                    String mTempMode = CommonUtils.contextSwitch();
                    String oldTempMode = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("mTempMode", "");
                    if(!oldTempMode.equals(mTempMode)){
                        SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("mTempMode", mTempMode);
                    }

                    signature();//签名
                }
            }
        }
        //---------------------- 设备二维码---------------
    }

    //签名
    private void signature(){
        if (InternalService.getInstance() != null&&InternalService.getInstance().iTaseSdkAidlInterface != null) {
            try {
                Result deviceResult = InternalService.getInstance().iTaseSdkAidlInterface.Security_encodeQRcode(ICCID+ "|"+IMEI + "|" + VIN);
                if(deviceResult!=null&&deviceResult.getErrCode()==0&&deviceResult.getReqBuffer()!=null){
                    String  deviceSecret = deviceResult.getReqBuffer();
                    mTask = new MyTask();//注意每次需new一个实例,新建的任务只能执行一次,否则会出现异常
                    mTask.execute(deviceSecret,"0");//String type = "0";//"0"设备编码 "1"车辆绑定
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {//onPreExecute方法用于在执行后台任务前做一些UI操作

        }

        @Override
        protected String doInBackground(String... params) {//doInBackground方法内部执行后台任务,不可在此方法内修改UI
            try {
                if(params[1].equals("0")){//String type = "0";//"0"设备编码 "1"车辆绑定
                    SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("deviceSecret", params[0]);

                    JSONObject urlData = new JSONObject();
                    try{
                        urlData.put("type", "device");
                        urlData.put("device_id",device_id);
                        urlData.put("secret",params[0]);
                    }catch (JSONException e){
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = QRCodeUtil.CreateTwoDCode(urlData.toString());
                    aCache.put("deviceBitmap",bitmap);
                }else if(params[1].equals("1")){//String type = "0";//"0"设备编码 "1"车辆绑定
                    JSONObject urlData = new JSONObject();
                    try{
                        urlData.put("type", "car");
                    }catch (JSONException e){
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = QRCodeUtil.CreateTwoDCode(urlData.toString());
                    aCache.put("vehicleBitmap",bitmap);
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {//onProgressUpdate方法用于更新进度信息
        }

        @Override
        protected void onPostExecute(String result) {//onPostExecute方法用于在执行完后台任务后更新UI,显示结果

        }

        @Override
        protected void onCancelled() {//onCancelled方法用于在取消执行中的任务时更改UI

        }
    }

    @Override
    public void onCmdProc(int i, ArrayList<Integer> arrayList) {
        if (i == 0x72A1) {
            StringBuffer sn = new StringBuffer();
            for (Integer j : arrayList){
                sn.append(StringUtils.convertHexToString(Integer.toHexString(j)));
            }
            if(sn.toString().length()>0){
                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("device_id", sn.toString());
            }
        }else if(i == 0x741E){
            StringBuffer vin = new StringBuffer();
            for (Integer j : arrayList){
                vin.append(StringUtils.convertHexToString(Integer.toHexString(j)));
            }
            if(vin.toString().length()>0){
                String NEW_VIN = SharedPreferencesHelper.getInstance(PersonCenterActivity.this).getString("NEW_VIN", "");
                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("NEW_VIN", vin.toString());
                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("OLD_VIN", NEW_VIN);
            }else{
                SharedPreferencesHelper.getInstance(PersonCenterActivity.this).putString("NEW_VIN", "");
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
}
