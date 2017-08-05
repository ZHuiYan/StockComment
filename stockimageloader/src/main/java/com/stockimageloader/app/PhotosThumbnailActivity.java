package com.stockimageloader.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stockimageloader.app.base.OnItemClickListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



/**
 * 相册缩略图
 * <p/>
 * Created by Yang on 2015/8/25 025.
 */
public class PhotosThumbnailActivity extends Activity {

    private final String TAG = PhotosThumbnailActivity.class.getSimpleName();
    private static final String KEY_MULTI = "multi";
    private static final String KEY_CROP = "crop";

    private boolean isMulti;//是否为多选
    private RecyclerView recyclerView;
    private PhotosThumbAdapter adapter;
    private MenuItem doneMenu;//选中完成菜单

    private TextView tv_thumb_dir;
    private TextView tv_thumb_preview;
    private TextView tv_thumb_pages;

    //所有图片文件夹
    private ArrayList<FileBean> imgFolders = new ArrayList<>();
    //当前预览目录
    private String curDir;
    //当前目录下的所有图片
    private ArrayList<String> photos = new ArrayList<>();
    private ScanHandler handler;
    private ListPopupWindow dirPop;
    private boolean needCrop = false;//是否需要裁剪

    private Context context;
    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, PhotosThumbnailActivity.class);
        activity.startActivity(intent);
    }

    public static void startForResult(Activity activity, int requestCode, boolean isMulti) {
        startForResult(activity, requestCode, isMulti, false);
    }

    public static void startForResult(Activity activity, int requestCode, boolean isMulti, boolean needCrop) {
        Intent intent = new Intent();
        intent.setClass(activity, PhotosThumbnailActivity.class);
        intent.putExtra(KEY_MULTI, isMulti);
        intent.putExtra(KEY_CROP, needCrop);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(Activity activity, int requestCode) {
        startForResult(activity, requestCode, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_thumbnail);
        context = PhotosThumbnailActivity.this;
        isMulti = getIntent().getBooleanExtra(KEY_MULTI, true);
        needCrop = getIntent().getBooleanExtra(KEY_CROP, false);
        findById();
        getImg();
        init();
        setListener();
    }

    private void init() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        adapter = new PhotosThumbAdapter(context, curDir, photos, isMulti);
        recyclerView.setAdapter(adapter);
        if (!isMulti) {
            tv_thumb_preview.setVisibility(View.GONE);
        }
    }

    private void findById() {
        tv_thumb_dir = (TextView) findViewById(R.id.tv_thumb_dir);
        tv_thumb_preview = (TextView) findViewById(R.id.tv_thumb_preview);
        tv_thumb_pages = (TextView) findViewById(R.id.tv_thumb_pages);
        recyclerView = (RecyclerView) findViewById(R.id.rc_thumb);
    }

    private void setListener() {
        tv_thumb_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDirPopWind(view);
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(int position, View view) {
                if (position == 0) {
                    takePhotoPath = ImageTools.takePic(PhotosThumbnailActivity.this, TAKEPHOTO_CODE);
                    Log.e("imgLoader",TAG + "takePhotoPath==" + takePhotoPath);
                } else {
                    if (isMulti) {
                        adapter.updateSelectData(position);
//                        doneMenu.setEnabled(adapter.getSelectList().size() > 0);
                    } else {
                        if(needCrop){
                            Log.e("imgLoader",TAG + "needCrop");
//                            PhotosCropActivity.startForResult((PhotosThumbnailActivity) context, adapter.getItemObject(position), CROP_CODE);
                        }else {
                            ArrayList<String> paths=new ArrayList<>();
                            paths.add(adapter.getItemObject(position));
                            Log.e("imgLoader",TAG + "预览");
//                            PhotosPreviewActivity.startForResult((PhotosThumbnailActivity) context, PREVIEW_CODE, paths);
                        }

                    }
                }
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });
    }

    private void getImg() {
        handler = new ScanHandler();
        new Thread(new ScanRunnable(handler)).start();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);
        doneMenu = menu.findItem(R.id.action_done);
        if (isMulti) {
            doneMenu.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (adapter.getSelectList() == null || adapter.getSelectList().size() <= 0) {
                return false;
            }
            setActivityResult(adapter.getSelectList());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onStop() {
        if (dirPop != null) {
            dirPop.dismiss();
            dirPop = null;
        }
        super.onStop();
    }
    private HashMap<String,ArrayList<String>> dirMap = new HashMap<>();
    private void initDirPopWind(View v) {
        if (dirPop == null) {
            dirPop = new ListPopupWindow(context);
            dirPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            dirPop.setHeight((int)Utils.dpToPx(context, 400));
            dirPop.setAdapter(new DirAdapter(context, imgFolders));

            dirPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dirPop.dismiss();
                    curDir = imgFolders.get(position).getDir();
                    updatePhoto();
                    setSelectView(imgFolders.get(position));
                }
            });
        }
        dirPop.setAnchorView(v);
        dirPop.show();
    }

    private final int TAKEPHOTO_CODE = 1;
    private final int PREVIEW_CODE = 2;
    private final int CROP_CODE = 3;
    private String takePhotoPath = "";

    private void updatePhoto() {
        if (dirMap.containsKey(curDir)){
            photos = dirMap.get(curDir);
            adapter.updatePhoto(curDir, photos);
        }else {
            photos = new ArrayList<>();
            File file = new File(curDir);
            // FIXME xa zxj 2016/1/14 华为荣耀4X ，没有拍照之前，此处会报空指针
            if (file.exists()) {
                List<File> newfiles = SortFiles(file);
                for (File file1 : newfiles) {
                    String filename = file1.getName();
                    if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg")) {
                        photos.add(filename);
                    }
                }
                dirMap.put(curDir,photos);
                adapter.updatePhoto(curDir, photos);
            }
        }
    }

    private void setSelectView(FileBean imgFolder) {
        tv_thumb_dir.setText(imgFolder.getName());
        tv_thumb_pages.setText(getString(R.string.photoPages,imgFolder.getCount()));
    }

    /**
     * 文件排序 需要自己手动过滤
     *
     * @return
     */
    @NonNull
    private List<File> SortFiles(File dir) {
        List<File> newfiles = Arrays.asList(dir.listFiles());
        Collections.sort(newfiles, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.lastModified() > rhs.lastModified()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return newfiles;
    }

    //检索遍历图片列表
    private String[] filterFile(File dir) {
        return dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg");
            }
        });
    }

    //调用setResult,返回之前Activity
    private void setActivityResult(ArrayList<String> paths) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("data", paths);
        setResult(RESULT_OK, intent);
        finish();
    }

    class ScanRunnable implements Runnable {
        private ScanHandler handler;

        public ScanRunnable(ScanHandler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendEmptyMessage(SHOW_PROGRESS);//显示进度条
            try {
                scanPhotos();
            } catch (Exception e) {
                Log.e("comment",e.toString());
                handler.sendEmptyMessage(ERROR);
            }
        }

        //扫描图片
        private void scanPhotos() throws Exception {
            // 临时的辅助哈希表，用于防止同一个文件夹的多次扫描
            HashSet<String> dirHash = new HashSet<>();
            //扫描 缩略图
            ContentResolver cr = context.getContentResolver();

            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            // Add xa zxj 2015/12/23 增加 orderby 按 最后修改时间降序排序
            String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
            Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, orderBy);
            if (cursor == null) {
                //发送扫描失败通知
                handler.sendEmptyMessage(ERROR);
                return;
            }
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File parentFile = new File(imgPath).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();
                // 利用一个HashSet防止多次扫描同一个文件夹
                if (dirHash.contains(dirPath)) {
                    continue;
                }
                dirHash.add(dirPath);
                String[] files = filterFile(parentFile);
                if (files == null || files.length == 0) {
                    continue;
                }
                FileBean imgFile = new FileBean();
                imgFile.setDir(dirPath);//绝对路径
                imgFile.setCount(files.length);
                imgFolders.add(imgFile);//路径和总数加入到了imgFoilder里
            }
            cursor.close();
            handler.sendEmptyMessage(FINISH);
        }
    }

    private final int FINISH = 1;
    private final int ERROR = 2;
    private final int SHOW_PROGRESS = 3;
    private Dialog progressDialog;

    class ScanHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case SHOW_PROGRESS:
                    if (progressDialog == null) {
                        progressDialog = Utils.buildProgressDialog(context);
                    }
                    progressDialog.show();
                    break;
                case FINISH:
                    if (imgFolders != null && imgFolders.size() > 0) {
                        curDir = imgFolders.get(0).getDir();
                    }
                    updatePhoto();//更新图片
                    break;
                case ERROR:

                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKEPHOTO_CODE && resultCode == RESULT_OK) {
            if (isMulti) {
                ArrayList<String> paths = new ArrayList<>();
                paths.add(takePhotoPath);
                setActivityResult(paths);
            } else {
                Log.e("imgLoader",TAG + "裁剪类");
//                PhotosCropActivity.startForResult((PhotosThumbnailActivity) context, takePhotoPath, CROP_CODE);
            }
        } else if (requestCode == PREVIEW_CODE && resultCode == RESULT_OK) {
            ArrayList<String> paths;
            if (null !=data){
                 paths =data.getStringArrayListExtra("data");
            }else {
                paths =new ArrayList<>();
            }
            setActivityResult(paths);
        }
    }

    class DirAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<FileBean> dirs;
        private final String ROOTPATH = Utils.getRootFileFolder(context);

        public DirAdapter(Context context, ArrayList<FileBean> dirs) {
            this.context = context;
            this.dirs = dirs;
        }

        @Override
        public int getCount() {
            return dirs.size();
        }

        @Override
        public Object getItem(int position) {
            return dirs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.view_photo_dir_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FileBean fileBean = dirs.get(position);
            ImageTools.loadViewLocal(context, fileBean.getDir() + File.separator + fileBean.getFirstPath(), viewHolder.img);
            viewHolder.tvCount.setText(fileBean.getCount() + "");
            if (fileBean.getDir().contains(ROOTPATH)) {
                viewHolder.tvName.setText(fileBean.getDir().replace(ROOTPATH, ""));
            } else {
                viewHolder.tvName.setText(fileBean.getDir());
            }
            if (curDir.equals(fileBean.getDir())) {
                viewHolder.imgStatus.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgStatus.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder {
            private ImageView img;
            private TextView tvName;
            private TextView tvCount;
            private ImageView imgStatus;

            public ViewHolder(View itemView) {
                img = (ImageView) itemView.findViewById(R.id.img_item_icon);
                tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
                tvName.setEllipsize(TextUtils.TruncateAt.START);
                tvCount = (TextView) itemView.findViewById(R.id.tv_item_count);
                imgStatus = (ImageView) itemView.findViewById(R.id.img_item_status);
            }
        }
    }
}
