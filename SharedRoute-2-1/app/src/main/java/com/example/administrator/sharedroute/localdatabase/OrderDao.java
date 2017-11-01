package com.example.administrator.sharedroute.localdatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.administrator.sharedroute.entity.listItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jne
 * Date: 2015/1/6.
 * PorA属性中，0代表在购物车中的信息，1代表发布的信息
 */
public class OrderDao {
    private static final String TAG = "OrdersDao";

    // 列定义
    private final String[] ORDER_COLUMNS = new String[] {"ID","Money","PickID","TaskkindID","PublisherName","PublisherPhone","FetchLocation",
            "SendLocation","FetchTime","SendTime","WhichPay","Remark","PublisherID","FetcherID","Status","PromiseMoney","FetcherName","FetcherPhone"};
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
            cursor = db.query(OrderDBHelper.TABLE_item, new String[]{"COUNT(ID)"}, null, null, null, null, null);

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


    /**
     * 初始化数据
     */
    public void initTable(){
        SQLiteDatabase db = null;

        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " (ID,Money,PickID,TaskkindID,PublisherName,PublisherPhone,FetchLocation," +
                    "SendLocation,FetchTime,SendTime,WhichPay,Remark,PublisherID,FetcherID,Status,PromiseMoney) " +
                    "values ( '1','2.7','111','食物','wyz','1153710308','顺丰快递','正心楼424', '10月21日21时42分', '10月21日21时42分','支付宝','大件','1503',null,1,5.2)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " (ID,Money,PickID,TaskkindID,PublisherName,PublisherPhone,FetchLocation," +
                    "SendLocation,FetchTime,SendTime,WhichPay,Remark,PublisherID,FetcherID,Status,PromiseMoney) " +
                    "values ( '2','2.7','111','食物','wyz','1153710308','顺丰快递','正心楼424', '10月21日21时42分', '10月21日21时42分','支付宝','大件','1503',null,1,5.2)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " (ID,Money,PickID,TaskkindID,PublisherName,PublisherPhone,FetchLocation," +
                    "SendLocation,FetchTime,SendTime,WhichPay,Remark,PublisherID,FetcherID,Status,PromiseMoney) " +
                    "values ( '3','2.7','111','食物','wyz','1153710308','顺丰快递','正心楼424', '10月21日21时42分', '10月21日21时42分','支付宝','大件','1503',null,1,5.2)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " (ID,Money,PickID,TaskkindID,PublisherName,PublisherPhone,FetchLocation," +
                    "SendLocation,FetchTime,SendTime,WhichPay,Remark,PublisherID,FetcherID,Status,PromiseMoney) " +
                    "values ( '4','2.7','111','食物','wyz','1153710308','顺丰快递','正心楼424', '10月21日21时42分', '10月21日21时42分','支付宝','大件','1503',null,1,5.2)");
            db.execSQL("insert into " + OrderDBHelper.TABLE_item + " (ID,Money,PickID,TaskkindID,PublisherName,PublisherPhone,FetchLocation," +
                    "SendLocation,FetchTime,SendTime,WhichPay,Remark,PublisherID,FetcherID,Status,PromiseMoney) " +
                    "values ( '5','2.7','111','食物','wyz','1153710308','顺丰快递','正心楼424', '10月21日21时42分', '10月21日21时42分','支付宝','大件','1503',null,1,5.2)");
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

            }else if (sql.contains("insert") || sql.contains("update") || sql.contains("DELETE")){
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
            //"ID","Money","PickID","TaskkindID","PublisherName","PublisherPhone","FetchLocation",
            //"SendLocation","FetchTime","SendTime","WhichPay","Remark","PublisherID","FetcherID","Status","PromiseMoney"
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID",listItem.ID);
            contentValues.put("Money",listItem.Money);
            contentValues.put("TaskkindID",listItem.TaskKindID);
            contentValues.put("PublisherName",listItem.PublisherName);
            contentValues.put("PublisherPhone",listItem.PublisherPhone);
            contentValues.put("FetcherPhone",listItem.FetcherPhone);
            contentValues.put("FetcherName",listItem.FetcherName);
            contentValues.put("FetchLocation",listItem.FetchLocation);
            contentValues.put("SendLocation",listItem.SendLocation);
            contentValues.put("FetchTime",listItem.FetchTime);
            contentValues.put("SendTime",listItem.SendTime);
            contentValues.put("WhichPay",listItem.WhichPay);
            contentValues.put("Remark",listItem.Remark);
            contentValues.put("PublisherID",listItem.PublisherID);
            contentValues.put("FetcherID",listItem.FetcherID);
            contentValues.put("Status",listItem.status);
            contentValues.put("PromiseMoney",listItem.PromiseMoney);
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
            db.delete(OrderDBHelper.TABLE_item, "ID = ?", new String[]{String.valueOf(listItem.ID)});
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
    public boolean updateOrder(listItem listItem,int id){
        SQLiteDatabase db = null;
        try {
            db = ordersDBHelper.getWritableDatabase();
            db.beginTransaction();

            // update Orders set OrderPrice = 800 where Id = 6
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID",listItem.ID);
            contentValues.put("Money",listItem.Money);
            contentValues.put("TaskkindID",listItem.TaskKindID);
            contentValues.put("PublisherName",listItem.PublisherName);
            contentValues.put("PublisherPhone",listItem.PublisherPhone);
            contentValues.put("FetcherPhone",listItem.FetcherPhone);
            contentValues.put("FetcherName",listItem.FetcherName);
            contentValues.put("FetchLocation",listItem.FetchLocation);
            contentValues.put("SendLocation",listItem.SendLocation);
            contentValues.put("FetchTime",listItem.FetchTime);
            contentValues.put("SendTime",listItem.SendTime);
            contentValues.put("WhichPay",listItem.WhichPay);
            contentValues.put("Remark",listItem.Remark);
            contentValues.put("PublisherID",listItem.PublisherID);
            contentValues.put("FetcherID",listItem.FetcherID);
            contentValues.put("Status",listItem.status);
            contentValues.put("PromiseMoney",listItem.PromiseMoney);
            db.update(OrderDBHelper.TABLE_item,
                    contentValues,
                    "ID = ?",
                    new String[]{String.valueOf(id)});
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
    public int getItemCount(int sql) {//**//
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = ordersDBHelper.getReadableDatabase();
            // select count(Id) from Orders where Country = 'China'
            cursor = db.query(OrderDBHelper.TABLE_item,
                    new String[]{"COUNT(ID)"},
                    "ID = ?",
                    new String[]{String.valueOf(sql)},
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
            cursor = db.query(OrderDBHelper.TABLE_item, new String[]{"id","type","describe","publishTime","getTime","getLocation","sendTime","sendLocation","pickupCode","Max(price) as price","PorA"}, null, null, null, null, null);

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
        order.ID=cursor.getInt(cursor.getColumnIndex("ID"));
        order.Money=cursor.getDouble(cursor.getColumnIndex("Money"));
        order.FetcherID=cursor.getString(cursor.getColumnIndex("FetcherID"));
        order.FetcherName=cursor.getString(cursor.getColumnIndex("FetcherName"));
        order.FetcherPhone=cursor.getString(cursor.getColumnIndex("FetcherPhone"));
        order.PublisherID=cursor.getString(cursor.getColumnIndex("PublisherID"));
        order.PublisherName=cursor.getString(cursor.getColumnIndex("PublisherName"));
        order.PublisherPhone=cursor.getString(cursor.getColumnIndex("PublisherPhone"));
        order.TaskKindID=cursor.getString(cursor.getColumnIndex("TaskKindID"));
        order.FetchLocation= cursor.getString(cursor.getColumnIndex("FetchLocation"));
        order.FetchTime= cursor.getString(cursor.getColumnIndex("FetchTime"));
        order.SendLocation= cursor.getString(cursor.getColumnIndex("SendLocation"));
        order.SendTime=cursor.getString(cursor.getColumnIndex("SendTime"));
        order.status=cursor.getInt(cursor.getColumnIndex("Status"));
        order.PickID=cursor.getString(cursor.getColumnIndex("PickID"));
        order.Remark= cursor.getString(cursor.getColumnIndex("Remark"));
        order.WhichPay=cursor.getInt(cursor.getColumnIndex("WhichPay"));
        order.PromiseMoney=cursor.getDouble(cursor.getColumnIndex("PromiseMoney"));
        return order;
    }
}
