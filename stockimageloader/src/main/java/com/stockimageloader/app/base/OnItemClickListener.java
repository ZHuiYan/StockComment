package com.stockimageloader.app.base;

import android.view.View;

/**
 * RecycelView 选项点击长按监听
 * Created by Yang on 2015/8/19 019.
 */
public interface OnItemClickListener {

    void OnItemClick(int position, View view);

    boolean  OnItemLongClick(int position, View view);
}
