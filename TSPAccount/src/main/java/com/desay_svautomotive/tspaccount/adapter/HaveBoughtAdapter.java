package com.desay_svautomotive.tspaccount.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.desay_svautomotive.tspaccount.EngineeringBasic.base.SimpleRecAdapter;
import com.desay_svautomotive.tspaccount.EngineeringBasic.kit.KnifeKit;
import com.desay_svautomotive.tspaccount.R;
import com.desay_svautomotive.tspaccount.bean.PurchasedServiceBean;

import butterknife.BindView;

/**
 * @author 王漫生
 * @date 2018-3-31
 * @project：个人中心
 */

public class HaveBoughtAdapter extends SimpleRecAdapter<PurchasedServiceBean,HaveBoughtAdapter.ViewHolder> {

    public HaveBoughtAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder newViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public int getLayoutId() {
        return R.layout.music_item_layout;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PurchasedServiceBean purchasedServiceBean = data.get(position);

        if(purchasedServiceBean.getORDER_STATUS().equals("0")){
            holder.state.setText(R.string.non_purchased);
        }else if(purchasedServiceBean.getORDER_STATUS().equals("1")){//购买
            holder.state.setText(R.string.purchased);
        }
        if(position==0){
            holder.name.setText(R.string.basic_package);
            Glide.with(context).load("").error(R.mipmap.base_p).into(holder.picture);//头像获取及显示
        }else if(position==1){
            holder.name.setText(R.string.music_package);
            Glide.with(context).load("").error(R.mipmap.music_p).into(holder.picture);//头像获取及显示
        }else if(position==2){
            holder.name.setText(R.string.video_package);
            Glide.with(context).load("").error(R.mipmap.video_p).into(holder.picture);//头像获取及显示
        }else if(position==3){
            holder.name.setText(R.string.the_rescue_package);
            Glide.with(context).load("").error(R.mipmap.save_p).into(holder.picture);//头像获取及显示
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
        @BindView(R.id.picture)
        ImageView picture;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.state)
        TextView state;

        public ViewHolder(View itemView) {
            super(itemView);
            KnifeKit.bind(this, itemView);
        }
    }
}
