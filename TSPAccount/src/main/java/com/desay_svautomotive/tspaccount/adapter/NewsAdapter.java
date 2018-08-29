package com.desay_svautomotive.tspaccount.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desay_svautomotive.tspaccount.EngineeringBasic.base.SimpleRecAdapter;
import com.desay_svautomotive.tspaccount.EngineeringBasic.kit.KnifeKit;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tspaccount.bean.XGNotification;

import butterknife.BindView;

/**
 * @author 王漫生
 * @date 2018-3-28
 * @project：个人中心
 */
public class NewsAdapter extends SimpleRecAdapter<XGNotification,NewsAdapter.ViewHolder> {
    private int selectInt = 0;

    public NewsAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.news_item;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        XGNotification xgNotification = data.get(position);

        if(xgNotification.getType()==0){
            holder.icon_hide.setVisibility(View.VISIBLE);
            holder.icon_open.setVisibility(View.GONE);
        }else if(xgNotification.getType()==1){
            holder.icon_hide.setVisibility(View.GONE);
            holder.icon_open.setVisibility(View.VISIBLE);
        }
        holder.title.setText(xgNotification.getTitle());
        if(position == selectInt){
            holder.item.setSelected(true);//
            holder.title.setTextColor(context.getResources().getColor(R.color.txt_color));
        }else{
            holder.item.setSelected(false);
            holder.title.setTextColor(context.getResources().getColor(R.color.text_White));
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItemClick() != null) {
                    getItemClick().onItemClick(position);
                }
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item)
        LinearLayout item;
        @BindView(R.id.icon_hide)
        ImageView icon_hide;
        @BindView(R.id.icon_open)
        ImageView icon_open;
        @BindView(R.id.title)
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }

    public void clickItem(int position){
        selectInt = position;
    }

}
