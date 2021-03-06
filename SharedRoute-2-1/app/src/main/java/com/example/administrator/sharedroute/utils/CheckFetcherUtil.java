package com.example.administrator.sharedroute.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Elrond Wang on 2017/12/9.
 * test the Fetcher who must be appointed by us
 */


public class CheckFetcherUtil {
    private static String[] allowID = {"10000", "10001", "10002", "10003", "10004", "10005", "10006", "10007", "10008", "10009", "10010", "10011", "10012", "10013", "10014", "10015", "10016", "10017", "10018", "10019", "10020"};
    private Context mContext;
    public CheckFetcherUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static boolean isTheIDValid(String e) {
        for (String b : allowID)
            if (b.equals(e)) return true;
        return false;
    }

    public boolean isTheFetcherIlligal(){
        SharedPreferences sp = mContext.getSharedPreferences("now_account", Context.MODE_PRIVATE);
        String curID = sp.getString("now_stu_num",null);
        for (String e: allowID)if (e.equals(curID)) return true;
        return false;
    }
}
