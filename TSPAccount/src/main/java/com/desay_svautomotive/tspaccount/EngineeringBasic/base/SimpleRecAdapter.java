package com.desay_svautomotive.tspaccount.EngineeringBasic.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author 王漫生
 * @date 2018-7-10
 * @project：个人中心
 */

public abstract class SimpleRecAdapter<T, V extends RecyclerView.ViewHolder> extends XRecyclerAdapter<T, V> {

    public SimpleRecAdapter(Context context) {
        super(context);
    }

    public SimpleRecAdapter(Context context, ItemCallback<T> callback) {
        super(context, callback);
    }

    public SimpleRecAdapter(Context context, List<T> data) {
        super(context, data);
    }

    public SimpleRecAdapter(Context context, List<T> data, ItemCallback<T> callback) {
        super(context, data, callback);
    }

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
        return newViewHolder(view);
    }

    public abstract V newViewHolder(View itemView);

    public abstract int getLayoutId();

}
