package com.example.administrator.sharedroute.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.adapter.SearchNeedsRcViewAdapter;
import com.example.administrator.sharedroute.entity.listItem;
import com.example.administrator.sharedroute.localdatabase.OrderDao;
import com.example.administrator.sharedroute.utils.EndLessOnScrollListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.example.administrator.sharedroute.activity.SearchNeedsActivity.goodsCount;
import static com.example.administrator.sharedroute.activity.SearchNeedsActivity.mfab;

public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private boolean IS_LOADED = false;
    private static int mSerial=0;
    private int mTabPos=0;
    private boolean isFirst = true;
    Vibrator vibrator;

    private int totalItemCount;
    private int lastVisibleItem;
    private boolean loading = false;
    private boolean bottom = false;

    private CoordinatorLayout mShoppingCartRly;
    private SearchNeedsRcViewAdapter adapter;
    // 贝塞尔曲线中间过程点坐标
    private float[] mCurrentPosition = new float[2];
    // 路径测量
    private PathMeasure mPathMeasure;
    // 购物车商品数目
    private ArrayList<listItem> TaskListItem = new ArrayList<>();

    private FloatingActionButton pos_mfab;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Log.e("tag", "IS_LOADED="+IS_LOADED);
            if(!IS_LOADED){
                IS_LOADED = true;
                //这里执行加载数据的操作
                Log.e("tag", "我是page"+(mTabPos+1));
            }
        }
    };

    private SwipeRefreshLayout mRefreshLayout;

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

        refreshCart();
//        init_data();

        // 是否显示购物车商品数目
        mShoppingCartRly = (CoordinatorLayout) view.findViewById(R.id.searchNeeds_center_relView);
        pos_mfab = (FloatingActionButton) view.findViewById(R.id.fake_fab);

        Log.d("Fragment 1", "onCreateView");
        //End pos

        // 添加数据源
        adapter = new SearchNeedsRcViewAdapter(TaskListItem);
        adapter.setCallBackListener(new SearchNeedsRcViewAdapter.CallBackListener() {
            @Override
            public void callBackImg(ImageView goodsImg) {
                // 添加商品到购物车
                addGoodsToCart(goodsImg);
            }
        });
        mrc.setAdapter(adapter);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.setColorSchemeColors(Color.RED, Color.CYAN);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //我在List最前面加入一条数据

//                //数据重新加载完成后，提示数据发生改变，并且设置现在不在刷新
//                adapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(true);
                new MoreTask().execute();
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                new InitTask().execute();
            }
        });

//        mrc.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            /**
//             * Callback method to be invoked when the RecyclerView has been scrolled. This will be
//             * called after the scroll has completed.
//             * <p/>
//             * This callback will also be called if visible item range changes after a layout
//             * calculation. In that case, dx and dy will be 0.
//             *
//             * @param recyclerView The RecyclerView which scrolled.
//             * @param dx           The amount of horizontal scroll.
//             * @param dy           The amount of vertical scroll.
//             */
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                super.onScrolled(recyclerView, dx, dy);
//
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                totalItemCount = layoutManager.getItemCount();
//
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//
//                if (lastVisibleItem != totalItemCount - 1) {
//                    bottom = false;
//                }
//                if (!bottom && !loading && totalItemCount < (lastVisibleItem + 3)) {
//                    new LatestArticleTask().execute();
//                    loading = true;
//                }
//            }
//        });


        //设置页和当前页一致时加载，防止预加载
        if (isFirst && mTabPos==mSerial) {
            isFirst = false;
            sendMessage();
        }
        return view;
    }

    //每次上拉加载的时候，给RecyclerView的后面添加了10条数据数据
    private void loadMoreData() {
        for (int i = 0; i < 10; i++) {
            listItem item1 = new listItem("书籍", "小件", "今天 12：30", "一区 顺风速运", "今天 12：30", "一区 正心楼 524", 2.0, false);
            TaskListItem.add(item1);
            adapter.notifyDataSetChanged();
        }
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

    private class InitTask extends AsyncTask<Void, Void, ArrayList<listItem>> {
        @Override
        protected ArrayList<listItem> doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<listItem> data = new ArrayList<>();
            listItem item1 = new listItem("书籍", "小件", "今天 12：30", "一区 顺风速运", "今天 12：30", "一区 正心楼 524", 2.0, false);
            listItem item2 = new listItem("书籍", "小件", "今天 12：30", "一区 顺风速运", "今天 12：30", "一区 正心楼 524", 2.0, false);
            listItem item3 = new listItem("设备", "小件", "今天 18：30", "一区 韵达快递", "今天 12：30", "一区 2公寓 5024", 8.0, false);
            listItem item4 = new listItem("设备", "小件", "今天 18：30", "一区 韵达快递", "今天 12：30", "一区 2公寓 5024", 8.0, false);
            listItem item5 = new listItem("食物", "小件", "今天 15：30", "一区 中通快递", "今天 12：30", "一区 18公寓 9001", 5.0, false);
            data.add(item1);
            data.add(item2);
            data.add(item1);
            data.add(item3);
            data.add(item4);
            data.add(item5);
            return data;

        }

        @Override
        protected void onPostExecute(ArrayList<listItem> data) {
            super.onPostExecute(data);

            if (mRefreshLayout != null) {
                mRefreshLayout.setRefreshing(false);
            }
            //没有新的数据，提示消息
            if (data == null || data.size() == 0) {
                Toast.makeText(getActivity(), R.string.list_no_data, Toast.LENGTH_SHORT).show();
            } else {
                TaskListItem.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private class MoreTask extends AsyncTask<Void, Void, ArrayList<listItem>> {
        @Override
        protected ArrayList<listItem> doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<listItem> data;
            data = new ArrayList<listItem>();
            listItem item1 = new listItem("上滑", "小件", "今天 12：30", "一区 顺风速运", "今天 12：30", "一区 正心楼 524", 2.0, false);
            data.add(item1);
//            //只有第一次需要加载头部的轮播图片
//            //下拉刷新时候不加轮播图片
//            if (myDataset.size() == 0) {
//                data.addAll(getRotationItem());
//            }

//            data.addAll(getMoreById(mColumn, params[0]));
            return data;

        }

        @Override
        protected void onPostExecute(ArrayList<listItem> data) {
            super.onPostExecute(data);

            if (mRefreshLayout != null) {
                mRefreshLayout.setRefreshing(false);
            }
            //没有新的数据，提示消息
            if (data == null || data.size() == 0) {
                Toast.makeText(getActivity(), R.string.list_no_data, Toast.LENGTH_SHORT).show();
            } else {
                TaskListItem.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }

    }

    //Integer 是输入参数
    private class LatestArticleTask extends AsyncTask<Void, Void, List<listItem>> {

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //增加底部的一个null数据，表示ProgressBar
            if (TaskListItem != null && TaskListItem.size() > 0) {
                TaskListItem.add(null);
                // notifyItemInserted(int position)，这个方法是在第position位置
                // 被插入了一条数据的时候可以使用这个方法刷新，
                // 注意这个方法调用后会有插入的动画，这个动画可以使用默认的，也可以自己定义。
//                Logger.d("增加底部footer 圆形ProgressBar");

                adapter.notifyItemInserted(TaskListItem.size() - 1);
            }
        }

        @Override
        protected List<listItem> doInBackground(Void... params) {
//            Logger.d("in doInBackground");

            ArrayList<listItem> data = new ArrayList<>();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listItem item1 = new listItem("下滑", "小件", "今天 12：30", "一区 顺风速运", "今天 12：30", "一区 正心楼 524", 2.0, false);
            data.add(item1);
            return data;
        }

        @Override
        protected void onPostExecute(final List<listItem> moreArticles) {
            super.onPostExecute(moreArticles);
            if (TaskListItem.size() == 0) {
                TaskListItem.addAll(moreArticles);
                adapter.notifyDataSetChanged();
            } else {
                //删除 footer
                TaskListItem.remove(TaskListItem.size() - 1);

//                Logger.d("下拉增加数据 " + moreArticles);

                //只有到达最底部才加载
                //防止上拉到了倒数两三个也加载
                if (!bottom && lastVisibleItem == totalItemCount - 1 && moreArticles.size() == 0) {
//                    Snackbar.with(getActivity()) // context
//                            .text(getActivity().getResources().getString(R.string.list_no_data)) // text to display
//                            .duration(Snackbar.SnackbarDuration.LENGTH_SHORT) // make it shorter
//                            .show(PageFragment.this); // activity where it is displayed
                    Toast.makeText(getActivity(), R.string.list_no_data, Toast.LENGTH_SHORT).show();
                    bottom = true;
                }

                TaskListItem.addAll(moreArticles);
                adapter.notifyDataSetChanged();
                loading = false;
//            mArticleList.addAll(moreArticles);
            }

        }
    }

    protected void init_data() {
        TaskListItem = new ArrayList<>();
        listItem item1 = new listItem("书籍", "小件", "今天 12：30", "一区 顺风速运", "今天 12：30", "一区 正心楼 524", 2.0, false);
        listItem item2 = new listItem("书籍", "小件", "今天 12：30", "一区 顺风速运", "今天 12：30", "一区 正心楼 524", 2.0, false);
        listItem item3 = new listItem("设备", "小件", "今天 18：30", "一区 韵达快递", "今天 12：30", "一区 2公寓 5024", 8.0, false);
        listItem item4 = new listItem("设备", "小件", "今天 18：30", "一区 韵达快递", "今天 12：30", "一区 2公寓 5024", 8.0, false);
        listItem item5 = new listItem("食物", "小件", "今天 15：30", "一区 中通快递", "今天 12：30", "一区 18公寓 9001", 5.0, false);
        TaskListItem.add(item1);
        TaskListItem.add(item2);
        TaskListItem.add(item1);
        TaskListItem.add(item3);
        TaskListItem.add(item4);
        TaskListItem.add(item5);
    }
}