package com.desay_svautomotive.tspaccount.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desay_svautomotive.tspaccount.EngineeringBasic.base.ItemCallback;
import com.desay_svautomotive.tspaccount.EngineeringBasic.base.XActivity;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tspaccount.Utils.StringUtils;
import com.desay_svautomotive.tspaccount.adapter.NewsAdapter;
import com.desay_svautomotive.tspaccount.bean.XGNotification;
import com.desay_svautomotive.tspaccount.common.NotificationService;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

/**
 * @author 王漫生
 * @date 2018-3-29
 * @project：个人中心
 */
public class MyNewsActivity extends XActivity {
    @BindView(R.id.ll_left)
    LinearLayout ll_left;//返回按钮
    @BindView(R.id.hasData)
    LinearLayout hasData;//有数据
    @BindView(R.id.noData)
    TextView noData;//没数据
    @BindView(R.id.tv_title)
    TextView tv_title;//标题
    @BindView(R.id.list)
    RecyclerView list ;
    @BindView(R.id.title)
    TextView title ;
    @BindView(R.id.content)
    TextView content ;
    private NewsAdapter myAdapter ;
    private List<XGNotification> msgList = new ArrayList<XGNotification>();
    private NotificationService notificationService;
    private static final int lineSize = Integer.MAX_VALUE;// 每次显示数

    @Override
    public void initData(Bundle savedInstanceState) {
        toOpen();//从状态栏点击进入查看消息
        initMyNewsData();
        setBroadcast();//设置广播
        initEvent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_news;
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
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    //从状态栏点击进入查看消息
    private void toOpen(){
        // 判断是否从推送通知栏打开的
        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        if (click != null) {
            //从推送通知栏打开-Service打开Activity会重新执行Laucher流程
            //查看是不是全新打开的面板
            if (isTaskRoot()) {
                return;
            }
            //如果有面板存在则关闭当前的面板
            finish();
        }
    }

    //初始化数据
    private void initMyNewsData(){
        tv_title.setText(getResources().getString(R.string.my_Message));
        list.setLayoutManager( new LinearLayoutManager( this ));
        notificationService = NotificationService.getInstance(this);

        msg();//消息
    }

    //设置广播
    private void setBroadcast(){
        IntentFilter filter = new IntentFilter("com.desay_svautomotive.tspaccount.sendMsg");
        registerReceiver(broadcastReceiver, filter);
    }

    //广播
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            msg();    //消息
        }
    };

    //消息
    private void msg(){
        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        msgList.clear();
        msgList = notificationService.getScrollData(1, lineSize, "");
        if(msgList == null||msgList.size()==0){//没有消息
            hasData.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }else{//有消息
            for(int i =0;i<msgList.size();i++){
                long timeBetween = StringUtils.getTimeCom(msgList.get(i).getUpdate_time(),nowDate);
                if(timeBetween>=24*60*60*1000*7){//24*60*60*1000*7
                    notificationService.delete(msgList.get(i).getMsg_id());
                    msgList.remove(i);
                }
            }
            if(msgList.size()>0){
                hasData.setVisibility(View.VISIBLE);
                noData.setVisibility(View.GONE);
                title.setText(msgList.get(0).getTitle());
                content.setText(msgList.get(0).getContent());

                msgList.get(0).setType(1);
                notificationService.update(msgList.get(0));
            }else{
                hasData.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            }
        }
        myAdapter = new NewsAdapter(this) ;
        list.setAdapter( myAdapter );
        myAdapter.setData(msgList);
    }

    /**
     * 监听Back键按下事件:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
        Intent intentMsg = new Intent("com.desay_svautomotive.tspaccount.sendMsg");
        sendBroadcast(intentMsg);
        super.onBackPressed();
        overridePendingTransition(0,R.anim.activity_close);
    }

    //事件处理
    private void initEvent(){
        //跳转到下载界面下载手机APP
        ll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMsg = new Intent("com.desay_svautomotive.tspaccount.sendMsg");
                sendBroadcast(intentMsg);
                finish();
                overridePendingTransition(0,R.anim.activity_close);
            }
        });

        //消息列表点击事件
        myAdapter.setItemClick(new ItemCallback() {
            @Override
            public void onItemClick(int position) {
                title.setText(msgList.get(position).getTitle());
                content.setText(msgList.get(position).getContent());
                msgList.get(position).setType(1);
                myAdapter.clickItem(position);
                myAdapter.setData(msgList);

                notificationService.update(msgList.get(position));
            }
        });

    }
}