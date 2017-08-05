package com.stockimageloader.app.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by Yang on 2015/8/21 021.
 */
public abstract class BaseRecyclerAdapter<VH extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {
    private final String TAG = BaseRecyclerAdapter.class.getSimpleName();

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
   // 在onBindViewHolder中调用
    protected <VH extends BaseRecyclerViewHolder> void bindOnItemClickListener(VH holder, final int position) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onItemClickListener.OnItemClick(position, v);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    return onItemClickListener.OnItemLongClick(position, v);
                }
            });
        }
    }

}
