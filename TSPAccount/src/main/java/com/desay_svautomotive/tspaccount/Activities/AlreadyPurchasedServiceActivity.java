package com.desay_svautomotive.tspaccount.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.desay_svautomotive.tspaccount.EngineeringBasic.base.XActivity;
import com.desay_svautomotive.tspaccount.EngineeringBasic.cache.SharedPreferencesHelper;
import com.desay_svautomotive.tspaccount.EngineeringBasic.kit.Kits;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tspaccount.Utils.Constant;
import com.desay_svautomotive.tspaccount.Utils.ToastUtil;
import com.desay_svautomotive.tspaccount.adapter.HaveBoughtAdapter;
import com.desay_svautomotive.tspaccount.bean.PurchasedServiceBean;
import com.desay_svautomotive.tspaccount.netconnect.CommonUtils;
import com.desay_svautomotive.tspaccount.netconnect.StringDialogCallback;
import com.desay_svautomotive.tspaccount.receiver.CallbackAIDLService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * @author 王漫生
 * @date 2018-4-2
 * @project：个人中心
 */
public class AlreadyPurchasedServiceActivity extends XActivity {
    @BindView(R.id.ll_left)
    LinearLayout ll_left;//返回按钮
    @BindView(R.id.tv_title)
    TextView tv_title;//标题
    @BindView(R.id.list)
    RecyclerView list ;
    @BindView(R.id.flow_management)
    TextView flow_management;//流量管理
    @BindView(R.id.a_key_to_rescue)
    TextView a_key_to_rescue;//一键救援

    private HaveBoughtAdapter myAdapter ;//
    private List<PurchasedServiceBean> newsList = new ArrayList<PurchasedServiceBean>();
    private List<PurchasedServiceBean> newsTempList = new ArrayList<PurchasedServiceBean>();
    private String[] sort = {"pc05","pc06","pc07","pc08"};
    private String token;
    private boolean isRescue = false;

    @Override
    public void initData(Bundle savedInstanceState) {
        initAlreadyData();
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_already_purchased_service;
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
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
            isRescue = false;
            getProduct();//产品介绍
        }
    };

    //临时数据
    private void initTempData(){
        for (int i = 0;i<4;i++){
            PurchasedServiceBean purchasedServiceBean = new PurchasedServiceBean();
            purchasedServiceBean.setORDER_STATUS("0");
            if(i==0){
                purchasedServiceBean.setCATEGORY("pc05");
            }else if(i==1){
                purchasedServiceBean.setCATEGORY("pc06");
            }else if(i==2){
                purchasedServiceBean.setCATEGORY("pc07");
            }else if(i==3){
                purchasedServiceBean.setCATEGORY("pc08");
            }
            newsTempList.add(purchasedServiceBean);
        }
    }

    //初始化数据
    private void initAlreadyData(){
        tv_title.setText(getResources().getString(R.string.already_purchased_service));
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        list.setLayoutManager(linearLayoutManager);

        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();// 获取当前系统语言
        if(language.equals("en")){
            flow_management.setText("\"flow management\"");
            a_key_to_rescue.setText("\"a key to rescue\"");
        }else{
            flow_management.setText("\"流量管理\"");
            a_key_to_rescue.setText("\"一键救援\"");
        }

        initTempData();    //临时数据

        token = SharedPreferencesHelper.getInstance(AlreadyPurchasedServiceActivity.this).getString("token", "");
        if(TextUtils.isEmpty(token)){
            toLoginAndRequestAgain();
        }else{
            isRescue = false;
            getProduct();//产品介绍
        }

        setBroadcast();//设置广播
    }

    /**
     * 监听Back键按下事件:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,R.anim.activity_close);
    }

    //事件处理
    private void initEvent(){
        //跳转到下载界面下载手机APP
        ll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.activity_close);
            }
        });
    }

    //设置数据
    private void setData(){
        if(newsList==null||newsList.size()==0){
            newsList.addAll(newsTempList);
        }else{
            for(int i = 0;i<newsList.size();i++){
                if(newsList.get(i).getCATEGORY().equals("pc05")||newsList.get(i).getCATEGORY().equals("pc06")
                        ||newsList.get(i).getCATEGORY().equals("pc07")||newsList.get(i).getCATEGORY().equals("pc08")){
                }else{
                    newsList.remove(i);
                }
            }
            if(newsList==null||newsList.size()==0){
                newsList.addAll(newsTempList);
            }else{
                for(int i = 0;i<newsList.size();i++){
                    for(int j = 0;j<newsTempList.size();j++){
                        if(newsList.get(i).getCATEGORY().equals(newsTempList.get(j).getCATEGORY())){
                            newsTempList.get(j).setPRODUCT_ID(newsList.get(i).getPRODUCT_ID());
                            newsTempList.get(j).setPRODUCT_NAME(newsList.get(i).getPRODUCT_NAME());
                            newsTempList.get(j).setPRODUCT_DESC(newsList.get(i).getPRODUCT_DESC());
                            newsTempList.get(j).setPRODUCT_PIC(newsList.get(i).getPRODUCT_PIC());
                            newsTempList.get(j).setCATEGORY(newsList.get(i).getCATEGORY());
                            newsTempList.get(j).setPUTAWAY_TIME(newsList.get(i).getPUTAWAY_TIME());
                            newsTempList.get(j).setPULLOFF_TIME(newsList.get(i).getPULLOFF_TIME());
                            newsTempList.get(j).setORDER_STATUS(newsList.get(i).getORDER_STATUS());
                        }
                    }
                }
                newsList.clear();
                for(int k = 0;k<sort.length;k++){
                    for(int p = 0;p<newsTempList.size();p++){
                        if(sort[k].equals(newsTempList.get(p).getCATEGORY())){
                            newsList.add(newsTempList.get(p));
                        }
                    }
                }

                if(isRescue == true){
                    newsList.get(newsList.size()-1).setORDER_STATUS("1");
                }else{
                    newsList.get(newsList.size()-1).setORDER_STATUS("0");
                }
            }
        }


        myAdapter = new HaveBoughtAdapter( AlreadyPurchasedServiceActivity.this) ;
        list.setAdapter( myAdapter );
        myAdapter.setData(newsList);
    }

    /**
     * 产品介绍
     */
    private void getProduct(){
        token = SharedPreferencesHelper.getInstance(AlreadyPurchasedServiceActivity.this).getString("token", "");
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
            requestData.put("public",publicData);
            requestData.put("private",privateData);
        }catch (JSONException e){
            e.printStackTrace();
        }
        OkGo.<String>post(CommonUtils.getServerName()+Constant.PRODUCT_SERVER)
                .upJson(requestData.toString())
                .tag(this)
                .execute(new StringDialogCallback(AlreadyPurchasedServiceActivity.this,true) {
                    @Override
                    public void onSuccess(Response<String> getResponse) {
                        try {
                            String showResponse = getResponse.body();
                            JSONObject response = new JSONObject(showResponse);
                            String resultCode = response.getString("resultCode");
                            if(resultCode.equals("0000")){
                                JSONArray resultList = response.getJSONArray("resultList");
                                newsList.clear();
                                for(int i=0;i<resultList.length();i++){
                                    JSONObject dataItem = resultList.getJSONObject(i);
                                    PurchasedServiceBean purchasedServiceBean = new PurchasedServiceBean();
                                    purchasedServiceBean.setPRODUCT_ID(dataItem.getString("PRODUCT_ID"));
                                    purchasedServiceBean.setPRODUCT_NAME(dataItem.getString("PRODUCT_NAME"));
                                    purchasedServiceBean.setPRODUCT_DESC(dataItem.getString("PRODUCT_DESC"));
                                    purchasedServiceBean.setPRODUCT_PIC(dataItem.getString("PRODUCT_PIC"));
                                    purchasedServiceBean.setCATEGORY(dataItem.getString("CATEGORY"));
                                    purchasedServiceBean.setPUTAWAY_TIME(dataItem.getString("PUTAWAY_TIME"));
                                    purchasedServiceBean.setPULLOFF_TIME(dataItem.getString("PULLOFF_TIME"));
                                    purchasedServiceBean.setORDER_STATUS(dataItem.getString("ORDER_STATUS"));
                                    newsList.add(purchasedServiceBean);
                                }


                                for(int i=0;i<resultList.length();i++){
                                    JSONObject dataItem = resultList.getJSONObject(i);
                                    if(dataItem.getString("CATEGORY").equals("pc03")){//救援
                                        if(dataItem.getString("ORDER_STATUS").equals("1")){//1：存在订购关系
                                            isRescue = true;
                                            break;
                                        }
                                    }
                                }
                                setData();//设置数据
                            }else if(resultCode.equals("E001")){//"token错误"


                                //先注释，以后再打开
//                                XGPushManager.unregisterPush(AlreadyPurchasedServiceActivity.this);//注册信鸽推送,解绑推送账号



                                toLoginAndRequestAgain();
                            }else{
                                new ToastUtil(AlreadyPurchasedServiceActivity.this,R.string.failed_to_get_product_information, Toast.LENGTH_SHORT,0).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        try {
                            if (!Kits.NetWork.isNetworkAvailable(AlreadyPurchasedServiceActivity.this)) {
                                new ToastUtil(AlreadyPurchasedServiceActivity.this,R.string.network_not_connected_check_the_network, Toast.LENGTH_SHORT,0).show();
                            } else{
                                new ToastUtil(AlreadyPurchasedServiceActivity.this,R.string.network_failure, Toast.LENGTH_SHORT,0).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    //重新登录，登录成功后重新请求
    private void toLoginAndRequestAgain(){
        new ToastUtil(AlreadyPurchasedServiceActivity.this,R.string.login_has_expired_please_login_again, Toast.LENGTH_SHORT,0).show();
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

}
