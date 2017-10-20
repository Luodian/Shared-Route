package com.example.administrator.sharedroute.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;

public class SelfDialog extends Dialog {

    private Button yes;//确定按钮
    private TextView titleTv;//消息标题文本
    private TextView mTaskName;//消息提示文本
    private TextView mTaskNameInfo;
    private TextView mPublisherName;
    private TextView mPublisherNameInfo;
    private TextView mPrice;
    private TextView mPriceInfo;
    private String titleStr;//从外界设置的title文本
    private String TaskNameInfoStr;
    private String PublisherNameInfoStr;
    private String PriceInfoStr;
    //确定文本和取消文本的显示内容
    private String yesStr;

    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
//    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
//        if (str != null) {
//            noStr = str;
//        }
//        this.noOnclickListener = onNoOnclickListener;
//    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null)
        {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public SelfDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (TaskNameInfoStr != null) {
            mTaskNameInfo.setText(TaskNameInfoStr);
        }
        if (PublisherNameInfoStr != null) {
            mPublisherNameInfo.setText(PublisherNameInfoStr);
        }
        if (PriceInfoStr != null) {
            mPriceInfo.setText(PriceInfoStr);
        }
        //如果设置按钮的文字
        if (yesStr != null) {
            yes.setText(yesStr);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (Button) findViewById(R.id.detaildialog_yesbutton);
        titleTv = (TextView) findViewById(R.id.title);
        mPublisherName = (TextView) findViewById((R.id.detaildialog_publisher_name));
        mPublisherNameInfo = (TextView) findViewById(R.id.detaildialog_publisher_name_info);
        mTaskName = (TextView) findViewById((R.id.detaildialog_taskname));
        mTaskNameInfo = (TextView) findViewById((R.id.detaildialog_taskname_info));
        mPrice = (TextView) findViewById((R.id.detaildialog_price));
        mPriceInfo = (TextView) findViewById((R.id.detaildialog_price_info));
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setTaskNameInfo(String message) {
        TaskNameInfoStr = message;
    }

    public void setPublisherNameInfoStr(String message) {
        PublisherNameInfoStr = message;
    }
    public void setPriceInfoStr(String message) {
        PriceInfoStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

}
