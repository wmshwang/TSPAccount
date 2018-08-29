package com.desay_svautomotive.tspaccount.bean;

import java.io.Serializable;

/**
 * @author 王漫生
 * @date 2018-5-16
 * @project：个人中心
 */
public class XGNotification implements Serializable {
    private static final long serialVersionUID = 100L;
    private Integer id;
    private long msg_id;//消息id
    private String title;//消息标题
    private String content;//消息内容
    private String activity;
    private int notificationActionType;
    private String update_time;//消息时间
    private int type;//消息是否已读

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMsg_id(long msg_id) {
        this.msg_id = msg_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setNotificationActionType(int notificationActionType) {
        this.notificationActionType = notificationActionType;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setType(int type) {
        this.type = type;
    }


    public Integer getId() {
        return id;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getActivity() {
        return activity;
    }

    public int getNotificationActionType() {
        return notificationActionType;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public int getType() {
        return type;
    }


    public XGNotification() {

    }

    public XGNotification(Integer id, Long msg_id, String title,
                          String content, String activity, int notificationActionType, String update_time, int type) {
        super();
        this.id = id;
        this.msg_id = msg_id;
        this.title = title;
        this.content = content;
        this.activity = activity;
        this.notificationActionType = notificationActionType;
        this.update_time = update_time;
        this.type = type;
    }

    public XGNotification(Long msg_id, String title, String content, String activity, int notificationActionType, String update_time, int type) {
        super();
        this.msg_id = msg_id;
        this.title = title;
        this.content = content;
        this.activity = activity;
        this.notificationActionType = notificationActionType;
        this.update_time = update_time;
        this.type = type;
    }

    @Override
    public String toString() {
        return "XGNotification{" +
                "id=" + id +
                ", msg_id=" + msg_id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", activity='" + activity + '\'' +
                ", notificationActionType=" + notificationActionType +
                ", update_time='" + update_time + '\'' +
                ", type=" + type +
                '}';
    }
}
