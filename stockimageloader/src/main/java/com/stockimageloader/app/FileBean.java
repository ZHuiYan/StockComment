package com.stockimageloader.app;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Yang on 2015/8/25 025.
 */
public class FileBean implements Parcelable{

    public enum FileType {
        ROOT, PARENT, DIR, FILE
    }

    private int id;
    private String dir;     //文件夹目录
    private String path;    //全路径
    private String size;    //文件大小
    private FileType type;  //文件类型
    private String time;    //时间
    private String firstPath;//第一个文件的路径
    private String name;     //文件名称
    private String thumbPath; //缩略图路径
    private int count;       //文件数量

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        if (dir.contains("/")) {
            int lastIndexOf = this.dir.lastIndexOf("/");
            this.name = this.dir.substring(lastIndexOf + 1);
        } else {
            this.name = dir;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstPath() {
        return firstPath;
    }

    public void setFirstPath(String firstPath) {
        this.firstPath = firstPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

//    方便数组转换成list，用对象toString方法
    @Override
    public String toString() {
        return name;
    }

    public String toStrings() {
        return "FileBean{" +
                "id=" + id +
                ", dir='" + dir + '\'' +
                ", path='" + path + '\'' +
                ", size='" + size + '\'' +
                ", type=" + type +
                ", time='" + time + '\'' +
                ", firstPath='" + firstPath + '\'' +
                ", name='" + name + '\'' +
                ", thumbPath='" + thumbPath + '\'' +
                ", count=" + count +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.dir);
        dest.writeString(this.path);
        dest.writeString(this.size);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.time);
        dest.writeString(this.firstPath);
        dest.writeString(this.name);
        dest.writeString(this.thumbPath);
        dest.writeInt(this.count);
    }

    public FileBean() {
    }

    protected FileBean(Parcel in) {
        this.id = in.readInt();
        this.dir = in.readString();
        this.path = in.readString();
        this.size = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : FileType.values()[tmpType];
        this.time = in.readString();
        this.firstPath = in.readString();
        this.name = in.readString();
        this.thumbPath = in.readString();
        this.count = in.readInt();
    }

    public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
        public FileBean createFromParcel(Parcel source) {
            return new FileBean(source);
        }

        public FileBean[] newArray(int size) {
            return new FileBean[size];
        }
    };
}
