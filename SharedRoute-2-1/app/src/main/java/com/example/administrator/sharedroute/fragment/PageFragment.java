package com.example.administrator.sharedroute.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.SearchNeedsRcViewAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.listener.RecyclerItemClickListener;
import com.example.administrator.sharedroute.utils.Utils;
import com.example.administrator.sharedroute.widget.SelfDialog;

import java.util.ArrayList;

import static com.example.administrator.sharedroute.activity.SearchNeedsActivity.goodsCount;
import static com.example.administrator.sharedroute.activity.SearchNeedsActivity.mfab;

public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private boolean IS_LOADED = false;
    private static int mSerial=0;
    private int mTabPos=0;
    private boolean isFirst = true;
    Vibrator vibrator;

    private CoordinatorLayout mShoppingCartRly;
    private SearchNeedsRcViewAdapter adapter;
    // 贝塞尔曲线中间过程点坐标
    private float[] mCurrentPosition = new float[2];
    // 路径测量
    private PathMeasure mPathMeasure;
    // 购物车商品数目
    private ArrayList<listItem> myDataset;

    private FloatingActionButton pos_mfab;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Log.e("tag", "IS_LOADED="+IS_LOADED);
            if(!IS_LOADED){
                IS_LOADED = true;
                //这里执行加载数据的操作
                Log.e("tag", "我是page"+(mTabPos+1));
            }
            return;
        };
    };

    public PageFragment(int serial){
        mSerial = serial;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("tag","onCreate()方法执行");

    }

    public void sendMessage(){
        Message message = handler.obtainMessage();
        message.sendToTarget();
    }

    public void setTabPos(int mTabPos) {
        this.mTabPos = mTabPos;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("tag","onCreateView()方法执行");
        View view = inflater.inflate(R.layout.campus1_search_needs, container, false);

        RecyclerView mrc = (RecyclerView) view.findViewById(R.id.searchNeeds_recycler_view);
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
        mrc.setHasFixedSize(true);
//        // use a linear layout manager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mrc.setLayoutManager(llm);
        vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);

//        mrc.addOnItemTouchListener(new RecyclerItemClickListener(mrc,
//                new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
////                        final SelfDialog selfDialog;
////                        selfDialog = new SelfDialog(getContext());
////                        selfDialog.setTitle("任务详情");
////                        selfDialog.setTaskNameInfo(myDataset.get(position).getExpressType());
////                        selfDialog.setPriceInfoStr(myDataset.get(position).getPrice());
////                        selfDialog.setPublisherNameInfoStr("Null");
////                        selfDialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
////                            @Override
////                            public void onYesClick() {
////                                selfDialog.dismiss();
////                            }
////                        });
////                        selfDialog.show();
//                    }
//                    @Override
//                    public void onItemLongClick(View view, int position) {
////                        System.out.println("onItemLongClick " + position);
//                    }
//                }));

//
//        // specify an adapter (see also next example)

        refreshCart();
        init_data();
        // 是否显示购物车商品数目
        mShoppingCartRly = (CoordinatorLayout) view.findViewById(R.id.searchNeeds_center_relView);
        pos_mfab = (FloatingActionButton) view.findViewById(R.id.fake_fab);

        Log.d("Fragment 1", "onCreateView");
        //End pos

//        mfab = (FloatingActionButton) view.findViewById(R.id.fab);
//        isShowCartGoodsCount();
        // 添加数据源
        adapter = new SearchNeedsRcViewAdapter(myDataset);
        adapter.setCallBackListener(new SearchNeedsRcViewAdapter.CallBackListener() {
            @Override
            public void callBackImg(ImageView goodsImg) {
                // 添加商品到购物车
                addGoodsToCart(goodsImg);
            }
        });
        mrc.setAdapter(adapter);

        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos==mSerial) {
            isFirst = false;
            sendMessage();
        }
//        adapter.setOnItemClickListener(new SearchNeedsRcViewAdapter.OnItemClickListener(){
//            @Override
//            public void onItemClick(View view , int position){
//                Toast.makeText(getContext(),myDataset.get(position).getExpressType(), Toast.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }

    private void addGoodsToCart(ImageView goodsImg) {
        // 创造出执行动画的主题goodsImg（这个图片就是执行动画的图片,从开始位置出发,经过一个抛物线（贝塞尔曲线）,移动到购物车里）
        final ImageView goods = new ImageView(getContext());
        goods.setImageDrawable(goodsImg.getDrawable());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        mShoppingCartRly.addView(goods, params);

        // 得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLocation = new int[2];
        mShoppingCartRly.getLocationInWindow(parentLocation);

        // 得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        goodsImg.getLocationInWindow(startLoc);

        // 得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        pos_mfab.getLocationInWindow(endLoc);

        // 开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        float startX = startLoc[0] - parentLocation[0] + goodsImg.getWidth() / 2;
        float startY = startLoc[1] - parentLocation[1] + goodsImg.getHeight() / 2;

        // 商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLocation[0] + pos_mfab.getWidth() / 5;
        float toY = endLoc[1] - parentLocation[1];

        // 开始绘制贝塞尔曲线
        Path path = new Path();
        // 移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        // 使用二阶贝塞尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        // mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        // 属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(400);

        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                // mCurrentPosition此时就是中间距离点的坐标值
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });

        // 开始执行动画
        valueAnimator.start();

        // 动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 购物车商品数量加1
                goodsCount ++;
                refreshCart();
//                 把执行动画的商品图片从父布局中移除
                mShoppingCartRly.removeView(goods);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("tag","onDestroyView()方法执行");
    }

    public void refreshCart()
    {
        switch (goodsCount){
            case 1:
                mfab.setImageResource(R.mipmap.ch1);
                break;
            case 2:
                mfab.setImageResource(R.mipmap.ch2);
                break;
            case 3:
                mfab.setImageResource(R.mipmap.ch3);
                break;
            case 4:
                mfab.setImageResource(R.mipmap.ch4);
                break;
            case 5:
                mfab.setImageResource(R.mipmap.ch5);
                break;
            default:
        }
    }



    protected void init_data()
    {
        myDataset = new ArrayList<>();
        listItem item1 = new listItem("书籍","小件","今天 12：30","一区 顺风速运","今天 12：30","一区 正心楼 524",2.0,false);
        listItem item2 = new listItem("书籍","小件","今天 12：30","一区 顺风速运","今天 12：30","一区 正心楼 524",2.0,false);
        listItem item3 = new listItem("设备","小件","今天 18：30","一区 韵达快递","今天 12：30","一区 2公寓 5024",8.0,false);
        listItem item4 = new listItem("设备","小件","今天 18：30","一区 韵达快递","今天 12：30","一区 2公寓 5024",8.0,false);
        listItem item5 = new listItem("食物","小件","今天 15：30","一区 中通快递","今天 12：30","一区 18公寓 9001",5.0,false);
        myDataset.add(item1);
        myDataset.add(item2);
        myDataset.add(item1);
        myDataset.add(item3);
        myDataset.add(item4);
        myDataset.add(item5);
    }
}