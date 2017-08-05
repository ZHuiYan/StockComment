package com.stockimageloader.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Wode9 on 2016/10/15.
 */

public class Utils {
    /**
     * 检查外部存储SD是否存在
     *
     * @return
     */
    public static boolean isSDExist(Context context) {
        String SDState = Environment.getExternalStorageState();
        if (!SDState.equals(Environment.MEDIA_MOUNTED)) {

            return false;
        }
        return true;
    }
    /**
     * 打开或创建文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File openOrCreateFile(String filePath) throws IOException {
        return openOrCreateFile(filePath, false);
    }

    public static File openOrCreateFile(String filePath, boolean isForce) throws IOException {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        String dir = filePath.substring(0, filePath.lastIndexOf(File.separator));
        if (!mkDirs(dir)) {
            Log.e("Utils", "目录不存在，创建失败：" + dir);
            return null;
        }
        if (!createFile(isForce, filePath)) {
            return null;
        }
        return new File(filePath);
    }

    public static boolean mkDirs(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            return dirFile.mkdirs();
        }
        return true;
    }
    public static boolean createFile(boolean isForce, String filePath) {
        File file = new File(filePath);
        try {
            if (file.exists() && isForce) {
                file.delete();
            }
            if (!file.exists()) {
                return file.createNewFile();
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }
    private static final String DIR="StockEmotion";

    //获取根目录
    public static String getRootFileFolder(Context context) {
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            path = context.getCacheDir().getAbsolutePath();
        }
        path = path + "/" + DIR;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }
    public static Dialog buildProgressDialog(Context context){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(null);
        dialog.setMessage(context.getString(R.string.wait));
        return dialog;
    }
}
