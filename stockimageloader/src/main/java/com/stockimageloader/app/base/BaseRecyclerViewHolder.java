package com.stockimageloader.app.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 * Created by Yang on 2015/8/21 021.
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    protected View itemView;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }
}
