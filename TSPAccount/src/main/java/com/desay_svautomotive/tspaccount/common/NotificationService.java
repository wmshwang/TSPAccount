package com.desay_svautomotive.tspaccount.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.desay_svautomotive.tspaccount.bean.XGNotification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王漫生
 * @date 2018-6-2
 * @project：个人中心
 */
public class NotificationService {
    private DBOpenHelper dbOpenHelper;
    private static NotificationService instance = null;

    public NotificationService(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
    }

    public synchronized static NotificationService getInstance(Context ctx) {
        if (null == instance) {
            instance = new NotificationService(ctx);
        }
        return instance;
    }

    public void save(XGNotification notification) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_id", notification.getMsg_id());
        values.put("title", notification.getTitle());
        values.put("content", notification.getContent());
        values.put("activity", notification.getActivity());
        values.put("notificationActionType", notification.getNotificationActionType());
        values.put("update_time", notification.getUpdate_time());
        values.put("type", notification.getType());
        db.insert("notification", null, values);
    }

    public void delete(long id) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete("notification", "msg_id=?", new String[]{id+""});
    }

    public void deleteAll() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete("notification", "", null);
    }

    public void update(XGNotification notification) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("msg_id", notification.getMsg_id());
        values.put("title", notification.getTitle());
        values.put("content", notification.getContent());
        values.put("activity", notification.getActivity());
        values.put("notificationActionType", notification.getNotificationActionType());
        values.put("update_time", notification.getUpdate_time());
        values.put("type", notification.getType());
        db.update("notification", values, "id=?", new String[]{notification
                .getId().toString()});
    }

    public XGNotification find(Integer id) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db
                .query("notification",
                        new String[]{"id,msg_id,title,content,activity,notificationActionType,update_time,type"},
                        "id=?", new String[]{id.toString()}, null, null,
                        null, "1");
        try {
            if (cursor.moveToFirst()) {
                return new XGNotification(cursor.getInt(cursor
                        .getColumnIndex("id")), cursor.getLong(cursor
                        .getColumnIndex("msg_id")), cursor.getString(cursor
                        .getColumnIndex("title")), cursor.getString(cursor
                        .getColumnIndex("content")), cursor.getString(cursor
                        .getColumnIndex("activity")), cursor.getInt(cursor
                        .getColumnIndex("notificationActionType")), cursor.getString(cursor
                        .getColumnIndex("update_time")), cursor.getInt(cursor
                        .getColumnIndex("type")));
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public List<XGNotification> getScrollData(int currentPage, int lineSize,
                                              String msg_id) {
        String firstResult = String.valueOf((currentPage - 1) * lineSize);
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (msg_id == null || "".equals(msg_id)) {
                cursor = db
                        .query("notification",
                                new String[]{"id,msg_id,title,content,activity,notificationActionType,update_time,type"},
                                null, null, null, null, "update_time DESC",
                                firstResult + "," + lineSize);
            } else {
                cursor = db
                        .query("notification",
                                new String[]{"id,msg_id,title,content,activity,notificationActionType,update_time,type"},
                                "msg_id like ?", new String[]{msg_id + "%"},
                                null, null, "update_time DESC", firstResult
                                        + "," + lineSize);
            }
            List<XGNotification> notifications = new ArrayList<XGNotification>();
            while (cursor.moveToNext()) {
                notifications.add(new XGNotification(cursor.getInt(cursor
                        .getColumnIndex("id")), cursor.getLong(cursor
                        .getColumnIndex("msg_id")), cursor.getString(cursor
                        .getColumnIndex("title")), cursor.getString(cursor
                        .getColumnIndex("content")), cursor.getString(cursor
                        .getColumnIndex("activity")), cursor.getInt(cursor
                        .getColumnIndex("notificationActionType")), cursor.getString(cursor
                        .getColumnIndex("update_time")),cursor.getInt(cursor
                        .getColumnIndex("type"))));
            }
            return notifications;
        } finally {
            cursor.close();
        }
    }

    public int getCount() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from notification", null);
        try {
            cursor.moveToFirst();
            return cursor.getInt(0);
        } finally {
            cursor.close();
        }
    }
}
