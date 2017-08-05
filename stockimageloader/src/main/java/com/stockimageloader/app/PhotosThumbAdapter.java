package com.stockimageloader.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.stockimageloader.app.base.BaseRecyclerAdapter;
import com.stockimageloader.app.base.BaseRecyclerViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by Yang on 2015/8/25 025.
 */
public class PhotosThumbAdapter extends BaseRecyclerAdapter<PhotosThumbAdapter.PhotosThumbViewHolder> {

    private final String TAG = PhotosThumbAdapter.class.getSimpleName();
    private Context context;
    private boolean isMulti = true;//是否为多选，显示checkbox
    private String curDir;
    private ArrayList<String> photos = new ArrayList<>();
    private ArrayList<String> selectList = new ArrayList<>();
    private HashMap<Integer, Boolean> selectMap = new HashMap<>();

    public PhotosThumbAdapter(Context context, String currentDir, ArrayList<String> photos, boolean isMulti) {
        this.context = context;
        this.isMulti = isMulti;
        updatePhoto(currentDir, photos);
    }

    public void updatePhoto(String curDir, ArrayList<String> photos) {
        initSelectData();
        this.curDir = curDir;
        this.photos.clear();
        this.photos.add(0, "takePhotos");
        this.photos.addAll(photos);
        notifyDataSetChanged();
    }

    //初始化选择数据
    private void initSelectData() {
        selectList.clear();
        selectMap.clear();
    }

    public String getItemObject(int position) {
        if (position == 0) return null;
        return curDir + File.separator + photos.get(position);
    }

    public ArrayList<String> getSelectList() {
        return this.selectList;
    }

    public void updateSelectData(int position) {
        if (position == 0 || selectMap == null || !selectMap.containsKey(position))
            return;
        String path = curDir + File.separator + photos.get(position);
        boolean selected = selectMap.get(position);
        if (!selected) {
            selectList.add(path);
        } else {
            selectList.remove(path);
        }
        selectMap.put(position, !selected);
        notifyItemChanged(position);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotosThumbViewHolder(View.inflate(context, R.layout.photos_grid_item, null));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        PhotosThumbViewHolder viewHolder = (PhotosThumbViewHolder) holder;
        bindOnItemClickListener(viewHolder, position);
        if (position == 0) {
            viewHolder.img.setImageResource(R.mipmap.icon_takephotos);
            viewHolder.checkBox.setVisibility(View.GONE);
        } else {
            ImageTools.loadViewLocal(context, curDir + File.separator + photos.get(position), viewHolder.img,R.mipmap.pictures_default);
            if (isMulti){
                viewHolder.checkBox.setVisibility(View.VISIBLE);
                if (!selectMap.containsKey(position)) {
                    selectMap.put(position, false);
                    viewHolder.checkBox.setChecked(false);
                } else {
                    viewHolder.checkBox.setChecked(selectMap.get(position));
                }
            }else{
                viewHolder.checkBox.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotosThumbViewHolder extends BaseRecyclerViewHolder {
        private ImageView img;
        private CheckBox checkBox;

        public PhotosThumbViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_photos_item);
            checkBox = (CheckBox) itemView.findViewById(R.id.ch_photos_item);
        }
    }
}
