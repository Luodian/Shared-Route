package com.example.administrator.sharedroute.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import com.example.administrator.sharedroute.R;
import com.example.administrator.sharedroute.entity.listItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jne
 * Date: 2015/1/6.
 */
public class OrderDao {
    private static final String TAG = "OrdersDao";

    // 列定义
    private final String[] ORDER_COLUMNS = new String[] {"type","describe","publishTime","getTime","getLocation","sendTime","sendLocation","pickupCode","price","PorA"};
    private Context context;
    private OrderDBHelper ordersDBHelper;

    public OrderDao(Context context) {
        this.context = context;
        ordersDBHelper = new OrderDBHelper(context);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select count(Id) from Orders
                cursor = db.query(OrderDBHelper.TABLE_item, new String[]{"COUNT(taskId)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    public int count(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select count(Id) from Orders
            cursor = db.query(OrderDBHelper.TABLE_item, new String[]{"COUNT(taskId)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return count;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return 0;
    }

    /**
     * 初始化数据
     */
    public void initTable(){
        SQLiteDatabase db = null;

        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " ( type, describe, publishTime,getTime,getLocation,sendTime,sendLocation,pickupCode,price,PorA) values ( '食物','大件', '10月21日21时42分', '10月21日21时42分','顺丰快递','10月21日21时42分','正心楼424','111111',5.2,0)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " ( type, describe, publishTime,getTime,getLocation,sendTime,sendLocation,pickupCode,price,PorA) values ( '食物','大件', '10月21日21时42分', '10月21日21时42分','顺丰快递','10月21日21时42分','正心楼424','111112',5.2,0)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " ( type, describe, publishTime,getTime,getLocation,sendTime,sendLocation,pickupCode,price,PorA) values ( '食物','大件', '10月21日21时42分', '10月21日21时42分','顺丰快递','10月21日21时42分','正心楼424','111113',5.2,0)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " ( type, describe, publishTime,getTime,getLocation,sendTime,sendLocation,pickupCode,price,PorA) values ( '食物','大件', '10月21日21时42分', '10月21日21时42分','顺丰快递','10月21日21时42分','正心楼424','111114',5.2,0)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " ( type, describe, publishTime,getTime,getLocation,sendTime,sendLocation,pickupCode,price,PorA) values ( '食物','大件', '10月21日21时42分', '10月21日21时42分','顺丰快递','10月21日21时42分','正心楼424','111115',5.2,0)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " ( type, describe, publishTime,getTime,getLocation,sendTime,sendLocation,pickupCode,price,PorA) values ( '食物','大件', '10月21日21时42分', '10月21日21时42分','顺丰快递','10月21日21时42分','正心楼424','111116',5.2,1)");

            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")){
                //Toast.makeText(context, R.string.strUnableSql, Toast.LENGTH_SHORT).show();
            }else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")){
                db = ordersDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
                //Toast.makeText(context, R.string.strSuccessSql, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //Toast.makeText(context, R.string.strErrorSql, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 查询数据库中所有数据
     */
    public List<listItem> getAllDate(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(OrderDBHelper.TABLE_item, ORDER_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<listItem> orderList = new ArrayList<listItem>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseOrder(cursor));
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 新增一条数据
     */
    public boolean insertData(listItem listItem){
        SQLiteDatabase db = null;

        try {
            db = ordersDBHelper.getWritableDatabase();
            //db.execSQL("insert into " + OrderDBHelper.TABLE_item + " (taskId, type, describe, publishTime,getTime,getLocation,sendTime,sendLocation,pickupCode,price,PorA) values (1, '食物','大件', '10月21日21时42分', '10月21日21时42分','顺丰快递','10月21日21时42分','正心楼424','111111',5.2,0)");
            db.beginTransaction();
            // insert into Orders(Id, CustomName, OrderPrice, Country) values (7, "Jne", 700, "China");
            ContentValues contentValues = new ContentValues();
            contentValues.put("type",listItem.getExpressType());
            contentValues.put("describe",listItem.getExpressSize());
            contentValues.put("publishTime",listItem.getPublishTime());
            contentValues.put("getTime",listItem.getInTimeStamp());
            contentValues.put("getLocation",listItem.getInLocation());
            contentValues.put("sendTime",listItem.getOutTimeStamp());
            contentValues.put("sendLocation",listItem.getOutLocation());
            contentValues.put("pickupCode",listItem.getPickupCode());
            contentValues.put("price",listItem.getPrice());
            contentValues.put("PorA",listItem.getPorA());
            db.insertOrThrow(OrderDBHelper.TABLE_item, null, contentValues);
            db.setTransactionSuccessful();
            return true;
        }catch (SQLiteConstraintException e){
            //Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }catch (Exception e){
            Log.e(TAG, "", e);
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 删除一条数据
     */
    public boolean deleteOrder(listItem listItem) {
        SQLiteDatabase db = null;

        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            // delete from Orders where Id = 7
            db.delete(OrderDBHelper.TABLE_item, "pickupCode = ?", new String[]{String.valueOf(listItem.getPickupCode())});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 修改一条数据  此处将Id为6的数据的OrderPrice修改了800
     */
    public boolean updateOrder(listItem listItem,String pickupCode){
        SQLiteDatabase db = null;
        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            // update Orders set OrderPrice = 800 where Id = 6
            ContentValues contentValues = new ContentValues();
            contentValues.put("type",listItem.getExpressType());
            contentValues.put("describe",listItem.getExpressSize());
            contentValues.put("publishTime",listItem.getPublishTime());
            contentValues.put("getTime",listItem.getInTimeStamp());
            contentValues.put("getLocation",listItem.getInLocation());
            contentValues.put("sendTime",listItem.getOutTimeStamp());
            contentValues.put("sendLocation",listItem.getOutLocation());
            contentValues.put("pickupCode",listItem.getPickupCode());
            contentValues.put("price",listItem.getPrice());
            contentValues.put("PorA",listItem.getPorA());
            db.update(OrderDBHelper.TABLE_item,
                   contentValues,
                    "pickupCode = ?",
                    new String[]{String.valueOf(pickupCode)});
            db.setTransactionSuccessful();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

        return false;
    }

    /**
     * 数据查询  此处将接受的订单的信息提取出来
     */
    public List<listItem> getAcceptOrder(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();

            // select * from Orders where CustomName = 'Bor'
            cursor = db.query(OrderDBHelper.TABLE_item,
                    ORDER_COLUMNS,
                    "PorA = ?",
                    new String[] {"0"},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<listItem> orderList = new ArrayList<listItem>(cursor.getCount());
                while (cursor.moveToNext()) {
                    listItem order = parseOrder(cursor);
                    orderList.add(order);
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }
    /**
     * 数据查询  此处将发布的订单的信息提取出来
     */

    public List<listItem> getPublishOrder(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();

            // select * from Orders where CustomName = 'Bor'
            cursor = db.query(OrderDBHelper.TABLE_item,
                    ORDER_COLUMNS,
                    "PorA = ?",
                    new String[] {"1"},
                    null, null, null);

            if (cursor.getCount() > 0) {
                List<listItem> orderList = new ArrayList<listItem>(cursor.getCount());
                while (cursor.moveToNext()) {
                    listItem order = parseOrder(cursor);
                    orderList.add(order);
                }
                return orderList;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }
    /**
     * 统计查询  此处查询Country为China的用户总数,***待修改***
     */
    public int getChinaCount(){
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select count(Id) from Orders where Country = 'China'
            cursor = db.query(OrderDBHelper.TABLE_item,
                    new String[]{"COUNT(Id)"},
                    "Country = ?",
                    new String[] {"China"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return count;
    }

    /**
     * 比较查询  此处查询单笔数据中OrderPrice最高的
     */
    public listItem getMaxOrderPrice(){
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select Id, CustomName, Max(OrderPrice) as OrderPrice, Country from Orders
            cursor = db.query(OrderDBHelper.TABLE_item, new String[]{"type","describe","publishTime","getTime","getLocation","sendTime","sendLocation","pickupCode","Max(price) as price","PorA"}, null, null, null, null, null);

            if (cursor.getCount() > 0){
                if (cursor.moveToFirst()) {
                    return parseOrder(cursor);
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    /**
     * 将查找到的数据转换成listItem类
     */
    private listItem parseOrder(Cursor cursor){
        listItem order = new listItem();
        order.setExpressType(cursor.getString(cursor.getColumnIndex("type")));
        order.setExpressSize(cursor.getString(cursor.getColumnIndex("describe")));
        order.setPublishTime(cursor.getString(cursor.getColumnIndex("publishTime")));
        order.setInTimeStamp(cursor.getString(cursor.getColumnIndex("getTime")));
        order.setInLocation(cursor.getString(cursor.getColumnIndex("getLocation")));
        order.setOutTimeStamp(cursor.getString(cursor.getColumnIndex("sendTime")));
        order.setOutLocation(cursor.getString(cursor.getColumnIndex("sendLocation")));
        order.setPickupCode(cursor.getString(cursor.getColumnIndex("pickupCode")));
        order.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
        order.setPorA(cursor.getInt(cursor.getColumnIndex("PorA")));
        return order;
    }
}
