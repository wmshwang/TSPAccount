package com.desay_svautomotive.tspaccount.EngineeringBasic.base;

/**
 * @author 王漫生
 * @date 2018-7-10
 * @project：个人中心
 */

public abstract class ItemCallback<T> {

    public void onItemClick(int position, T model, int tag) {
    }

    public void onItemClick(int position, int tag) {
    }

    public void onItemClick(int position, T model) {
    }

    public void onItemClick(int position) {
    }

    public void onItemLongClick(int position, T model, int tag) {
    }

    public void onItemLongClick(int position, int tag) {
    }

    public void onItemLongClick(int position, T model) {
    }

    public void onItemLongClick(int position) {
    }

    public void onItemSelect(int position, T model) {
    }

    public void onItemSelect(int position) {
    }
}
