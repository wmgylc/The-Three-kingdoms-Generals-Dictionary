package com.example.test.DataBase;

import android.graphics.Bitmap;
import android.net.Uri;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by admin on 2017/10/19.
 */

// !!! litepal无法存储uri等类型

public class General extends DataSupport {

    //信息：头像 名字 性别 生卒年 势力 （籍贯）

    private long addTime;

    private int image;

    private String imagePath;

    private String name;

    private int sex;

    private String age;

    private String country;

    //更多信息
    private String info;

    //数据库自动生成的自增数
    private int id;

    //0代表不关注，0以上代表关注
    private int isConcerned;


    public General() {
        //不设带参构造函数，仅初始化数据
        // TODO: 2017/11/5 转化成具体时间
        addTime = System.currentTimeMillis();
        image = 0;
        imagePath = null;
        name = "暂无姓名";
        sex = 0;
        age = "暂无生卒年";
        country = "暂无所属势力";
        info = "暂无更多信息";
        isConcerned = 0;
    }

    public long getAddTime() {
        return addTime;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    //id是自增的，不需要set函数
    public int getid() {
        return id;
    }

    public int getConcerned() {
        return isConcerned;
    }

    public void setConcerned(int isConcerned) {
        this.isConcerned = isConcerned;
    }
}
