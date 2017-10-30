package com.example.administrator.sharedroute.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.sharedroute.R;
import com.unstoppable.submitbuttonview.SubmitButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.angmarch.views.NiceSpinner;
import org.zackratos.ultimatebar.UltimateBar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.example.administrator.sharedroute.activity.MainActivity.select;


public class PublishNeedsActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    boolean left = true;        //标志选择的是左边的时间还是右边的时间
    int leftStatus = 0;       //标志左边选择的
    String leftDate;
    String leftTime;
    String rightDate;
    String rightTime;
    private int requestCode;
    private BottomNavigationView navigation;
    private DrawerLayout mDrawerLayout;
    private Button pickupLocationButton;
    private TextView textViewName;
    private TextView textViewPhoneNumber;
    private Button sendLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_needs);

        UltimateBar ultimateBar = new UltimateBar(this);
        ultimateBar.setImmersionBar(false);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_publishneeds);
        setSupportActionBar(toolbar);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        /*
         *显示返回的收件地点信息
         */
        pickupLocationButton = (Button)findViewById(R.id.pickupplace);
        textViewName = (TextView)findViewById(R.id.nametext) ;
        textViewPhoneNumber = (TextView)findViewById(R.id.phonetext) ;
        sendLocation = (Button)findViewById(R.id.delieverplace);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_user);
        }

        navigation = (BottomNavigationView) findViewById(R.id.publish_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_shop:
                        select = "releaseOrder";
                        Intent intent1 = new Intent(PublishNeedsActivity.this,TaskViewActivity.class);
                        intent1.putExtra("select_order",select);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_release:
                        select = "releaseOrder";
                        Intent intent2 = new Intent(PublishNeedsActivity.this,MyOrder.class);
                        intent2.putExtra("select_order",select);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_receive:
                        select = "receiveOrder";
                        Intent intent3 = new Intent(PublishNeedsActivity.this,MyOrder.class);
                        intent3.putExtra("select_order",select);
                        startActivity(intent3);
                        return true;
                    case R.id.release_rank:
                        select = "releaseRank";
                        Intent intent4 = new Intent(PublishNeedsActivity.this,MyRank.class);
                        intent4.putExtra("select_order",select);
                        startActivity(intent4);
                        return true;
                    case R.id.receive_rank:
                        select = "receiveRank";
                        Intent intent5 = new Intent(PublishNeedsActivity.this,MyRank.class);
                        intent5.putExtra("select_order",select);
                        startActivity(intent5);
                        return true;
                    case R.id.nav_wallet:
                        Intent intent6 = new Intent(PublishNeedsActivity.this,BandCard.class);
                        startActivity(intent6);
                        return true;
                    case R.id.nav_setting:
                        return true;
                    default:
                }
                return true;
            }
        });

        final TextView nameText = (TextView) findViewById(R.id.nametext);
        final TextView phoneText = (TextView) findViewById(R.id.phonetext);
        final Button pickupPlace = (Button) findViewById(R.id.pickupplace);

        pickupPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishNeedsActivity.this, sendLocationActivity.class);
                requestCode=1;
                startActivityForResult(intent,requestCode);
            }
        });

        final Button delieverPlace = (Button) findViewById(R.id.delieverplace);

        final TextView qujiantext = (TextView) findViewById(R.id.qujiantext);
//        LinearLayout qujianshijan = (LinearLayout) findViewById(R.id.qujianshijian);

        final TextView songjianText = (TextView) findViewById(R.id.songjiantext);
//        LinearLayout songjianshijian = (LinearLayout) findViewById(R.id.songjianshijian);

        if (bundle != null && bundle.containsKey("nameInfo")) {
            nameText.setText(bundle.getString("nameInfo"));
            phoneText.setText(bundle.getString("phoneInfo"));
            delieverPlace.setText(bundle.getString("delieverplaceInfo"));
        }

        CardView cdv1 = (CardView) findViewById(R.id.cdv1);
        cdv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishNeedsActivity.this, HistoryInfoActivity.class);
                requestCode = 0;
                startActivityForResult(intent,requestCode);
            }
        });
        delieverPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishNeedsActivity.this, HistoryInfoActivity.class);
                requestCode = 0;
                startActivityForResult(intent,requestCode);
            }
        });

        if (delieverPlace.getText().toString().equals("")) {
            delieverPlace.setText("送件地点");
        }

        final NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("书籍", "玩具", "化妆品", "电器"));
        niceSpinner.attachDataSource(dataset);

        ((LinearLayout)findViewById(R.id.pick_time_block)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = true;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PublishNeedsActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(false);
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        ((LinearLayout)findViewById(R.id.deliever_time_block)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = false;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PublishNeedsActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(false);
                dpd.vibrate(true);
                dpd.dismissOnPause(true);
                dpd.showYearPickerFirst(true);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        final EditText remarkText = (EditText) findViewById(R.id.remarktext);
        final EditText numText = (EditText) findViewById(R.id.numtext);
        final EditText money = (EditText) findViewById(R.id.money);

        final SubmitButton submitBtn = (SubmitButton) findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //验证是否是合法输入
//                if (nameText.getText().toString().equals("")||phoneText.getText().toString().equals("")||numText.getText().toString().equals("")
//                        ||delieverPlace.getText().toString().equals("送件地点")||pickupPlace.getText().toString().equals("取件地点")||
//                        ((TextView) findViewById(R.id.qujiantext)).getText().toString().equals("选择时间")||
//                        ((TextView) findViewById(R.id.songjiantext)).getText().toString().equals("选择时间")||
//                        money.getText().toString().equals("")){
//                    Toast.makeText(PublishNeedsActivity.this,"请将信息填写完整",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                String str = phoneText.getText().toString();
//                for (int i=0;i<str.length();++i){
//                    if (str.charAt(i)<'0' || str.charAt(i)>'9') {
//                        Toast.makeText(PublishNeedsActivity.this,"请输入正确的手机号码",Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                }
//                str = money.getText().toString();
//                boolean hasDotYet = false;
//                for (int i=0;i<str.length();++i) {
//                    if(str.charAt(i)=='.'&&(!hasDotYet)){
//                        hasDotYet=true;
//                    }else  if ((str.charAt(i)=='.'&&hasDotYet)) {
//                        Toast.makeText(PublishNeedsActivity.this,"请输入正确的金额",Toast.LENGTH_LONG).show();
//                        return;
//                    } else if (str.charAt(i)!='.'&&(str.charAt(i)<'0'||str.charAt(i)>'9')) {
//                        Toast.makeText(PublishNeedsActivity.this,"请输入正确的金额",Toast.LENGTH_LONG).show();
//                        return;
//                        }
//                }
                submitBtn.doResult(true);
                Thread thread = new Thread() {
                 public void run(){
                     try {
                         Thread.sleep(1500);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     Intent intent1 = new Intent(PublishNeedsActivity.this, PayBillActivity.class);
                     Bundle bundle = new Bundle();
                     bundle.putCharSequence("remark",remarkText.getText().toString());
                     bundle.putCharSequence("name", nameText.getText().toString());
                     bundle.putCharSequence("phone", phoneText.getText().toString());
                     bundle.putCharSequence("packsort", niceSpinner.getText().toString());
                     bundle.putCharSequence("remark", remarkText.getText().toString());
                     bundle.putCharSequence("num", numText.getText().toString());
                     bundle.putCharSequence("pickupplace", pickupPlace.getText().toString());
                     bundle.putCharSequence("delieverplace", delieverPlace.getText().toString());
                     bundle.putCharSequence("pickuptime", qujiantext.getText().toString());
                     bundle.putCharSequence("delievertime", songjianText.getText().toString());
                     bundle.putCharSequence("money", money.getText().toString());
                     bundle.putCharSequence("securitymoney",((EditText)findViewById(R.id.securitymoney)).getText().toString());
                     intent1.putExtras(bundle);
                     startActivity(intent1);
                 }
                };
                thread.start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:{
                if (data == null) break;
                String textName = data.getStringExtra("textName");
                String textPhone = data.getStringExtra("textPhone");
                String textDeliverPlace = data.getStringExtra("textDeliverPlace");
                textViewName.setText(textName);
                textViewPhoneNumber.setText(textPhone);
                sendLocation.setText(textDeliverPlace);
                break;
            }
            case 1:{
                String pickupLocation = data.getStringExtra("pickupLocation");
                pickupLocationButton.setText(pickupLocation);
                break;
            }
            default:break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    JumpToActivity(MainActivity.class);
                    finish();
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    JumpToActivity(SearchNeedsActivity.class);
                    finish();
                    return true;
            }
            return false;
        }

    };

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return true;
    }

    @Override
    public void onPause(){
        super.onPause();
        mDrawerLayout.closeDrawers();
    }

    public void JumpToActivity(Class activity){
        startActivity(new Intent(this,activity));
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialog");
        if (tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "" + year + "年" + (++monthOfYear) + "月" + dayOfMonth+"日";

        if (left) {
            leftDate = date;
        } else {
            rightDate = date;
        }

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                PublishNeedsActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.enableSeconds(false);
        tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "" + hourString + " : " + minuteString;
        if (left == true) {
            leftTime = time;
            ((TextView) findViewById(R.id.qujiantext)).setText(leftDate + "\n" + leftTime);
            ((TextView) findViewById(R.id.qujiantext)).setTextSize(8);
        } else {
            rightTime = time;
            ((TextView) findViewById(R.id.songjiantext)).setText(rightDate + "\n" + rightTime);
            ((TextView) findViewById(R.id.songjiantext)).setTextSize(8);
        }
    }
}
