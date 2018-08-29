package com.desay_svautomotive.tspaccount.EngineeringBasic.base;

/**
 * @author 王漫生
 * @date 2018-7-10
 * @project：个人中心
 */

public abstract class SimpleItemCallback<T, H> extends ItemCallback<T> {
    public void onItemClick(int position, T model, int tag, H holder) {
    }

    public void onItemClick(int position, T model, H holder) {
    }

    public void onItemClick(int position, int tag, H holder) {
    }

    public void onItemLongClick(int position, T model, int tag, H holder) {
    }

    public void onItemLongClick(int position, T model, H holder) {
    }

    public void onItemLongClick(int position, int tag, H holder) {
    }
}
