package com.stockimageloader.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * Created by Wode9 on 2016/9/22.
 */
public class ImageTools {
    /**
     * 加载本地图片
     *
     * @param context
     * @param filePath
     * @param view
     * @param def
     */
    public static void loadViewLocal(final Context context, String filePath, final ImageView view, final int def) {

        File file = new File(filePath);
        if (file.exists()) {
            RequestManager glideRequest = Glide.with(context);
            DrawableTypeRequest drawableTypeRequest = glideRequest.load(file);
            drawableTypeRequest.listener(new RequestListener() {
                @Override
                public boolean onException(Exception e, Object o, Target target, boolean b) {
                    Glide.with(context).load(def).into(view);
                    return true;
                }

                @Override
                public boolean onResourceReady(Object o, Object o2, Target target, boolean b, boolean b1) {
                    return false;
                }
            });
            drawableTypeRequest.into(view);
        } else {
            Glide.with(context).load(def).into(view);
        }
    }
    public static void loadViewLocal(Context context, String filePath, ImageView view) {
        Glide.with(context).load(new File(filePath)).into(view);
    }
    /**
     * 启动相机拍照
     *   String photoPath = Utils.getRootFileFolder() + "/images/" + System.currentTimeMillis() + ".jpg";
     * @param activity
     * @param requestCode
     */
    public static String takePic(Activity activity, int requestCode) {
        if (!Utils.isSDExist(activity)) {
            return null;
        }
        String photoPath = Utils.getRootFileFolder(activity) + "/images/" + System.currentTimeMillis() + ".jpg";
        try {
            File photoFile = Utils.openOrCreateFile(photoPath);
            if (photoFile == null || !photoFile.exists())
                return null;
            // 将File对象转换为Uri并启动照相程序
            Uri imageUri = Uri.fromFile(photoFile);
            // "android.media.action.IMAGE_CAPTURE"
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 照相
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 指定图片输出地址
            activity.startActivityForResult(intent, requestCode); // 启动照相
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoPath;
    }
}
