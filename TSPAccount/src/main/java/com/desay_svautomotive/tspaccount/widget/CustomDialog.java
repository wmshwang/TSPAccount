package com.desay_svautomotive.tspaccount.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.desay_svautomotive.tspaccount.R;

/**
 * @author 王漫生
 * @date 2018-3-16
 * @project：个人中心
 */

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    private static int mTheme = R.style.CustomDialog; // 自定义Dialog对应的Style

    private Button mYesButton; // 确定按钮

    private Button mNoButton; // 取消按钮

    private TextView mContent; // 内容区域

    private OnCustomDialogListener mListener; // 自定义接口OnCustomDialogListener：确定按钮和取消按钮的监听器

    public CustomDialog(Context context) { // 构造方法
        this(context, mTheme);
    }

    public CustomDialog(Context context, int theme) { // 必须实现的构造方法
        super(context, theme);
    }

    public void setCustomOnClickListener(OnCustomDialogListener listener) { // 为确定按钮和取消按钮设置监听器
        mListener = listener;
    }

    private static final int MESSAGE_SET_CONTENT = 0x10; //用来执行初始化操作的消息标识

    private static final int MESSAGE_SET_YES_BTN_TEXT = 0x11;

    private static final int MESSAGE_SET_NO_BTN_TEXT = 0x12;

    private static final int MESSAGE_SET_YES_BTN_TEXT_COLOR = 0x13;

    private static final int MESSAGE_SET_NO_BTN_TEXT_COLOR = 0x14;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() { // 统一用Handler来执行我们的初始化操作：例如设置Dialog内容，设置取消/确定按钮的文字和颜色
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_SET_CONTENT: // 设置内容区域内容
                    mContent.setText((String) msg.obj);
                    break;

                case MESSAGE_SET_YES_BTN_TEXT: // 设置确定按钮的文字
                    mYesButton.setText((String) msg.obj);
                    break;

                case MESSAGE_SET_NO_BTN_TEXT: // 设置取消按钮的文字
                    mNoButton.setText((String) msg.obj);
                    break;

                case MESSAGE_SET_YES_BTN_TEXT_COLOR: // 设置确定按钮的文字颜色
                    mYesButton.setTextColor(msg.arg1);
                    break;

                case MESSAGE_SET_NO_BTN_TEXT_COLOR: // 设置取消按钮的文字颜色
                    mNoButton.setTextColor(msg.arg1);
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog); // 引用以上定义的布局文件

        mYesButton = (Button) findViewById(R.id.ok); // 控件初始化以及
        mYesButton.setOnClickListener(this);

        mNoButton = (Button) findViewById(R.id.cancel);
        mNoButton.setOnClickListener(this);

        mContent = (TextView) findViewById(R.id.tv_content1);
    }

    public interface OnCustomDialogListener { // 自定义接口，确定/取消按钮的点击事件
        void setYesOnClick(); // 确定按钮点击

        void setNoOnClick(); // 取消按钮点击
    }

    @Override
    public void onClick(View v) { // 实现OnClickListener接口
        switch (v.getId()) {
            case R.id.ok:
                mListener.setYesOnClick();
                break;

            case R.id.cancel:
                mListener.setNoOnClick();
                break;
        }
    }

    public void setContent(String content) {
        android.os.Message msg = mHandler.obtainMessage();
        msg.what = MESSAGE_SET_CONTENT;
        msg.obj = content;
        mHandler.sendMessage(msg);
    }


    public void setYesBtnText(String yesText) {
        android.os.Message msg = mHandler.obtainMessage();
        msg.what = MESSAGE_SET_YES_BTN_TEXT;
        msg.obj = yesText;
        mHandler.sendMessage(msg);
    }

    public void setYesBtnTextColor(int colorId) {
        android.os.Message msg = mHandler.obtainMessage();
        msg.what = MESSAGE_SET_YES_BTN_TEXT_COLOR;
        msg.arg1 = colorId;
        mHandler.sendMessage(msg);
    }

    public void setNoBtnText(String noText) {
        android.os.Message msg = mHandler.obtainMessage();
        msg.what = MESSAGE_SET_NO_BTN_TEXT;
        msg.obj = noText;
        mHandler.sendMessage(msg);
    }

    public void setNoBtnTextColor(int colorId) {
        android.os.Message msg = mHandler.obtainMessage();
        msg.what = MESSAGE_SET_NO_BTN_TEXT_COLOR;
        msg.arg1 = colorId;
        mHandler.sendMessage(msg);
    }

}