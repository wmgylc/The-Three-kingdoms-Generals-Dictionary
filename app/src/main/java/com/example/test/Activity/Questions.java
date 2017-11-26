package com.example.test.Activity;

/**
 * Created by user on 2017/11/26.
 */

public class Questions {
    int true_ans,choose_ans,imageres;
    String imagepath,
            name[];

    String getImagepath(){return imagepath;}
    String getName(int i){return  name[i];}
    Questions(){
        imagepath = null;
        imageres = 0;
        name = new String[]{"","",""};
    }

    public int getImageres() {
        return imageres;
    }

    public void setName(String[] name) {
        this.name = name;
    }
    public int getTrue_ans(){return true_ans;}
    public int getChoose_ans(){return choose_ans;}
    public void setTrue_ans(int ans){
        this.true_ans=ans;
    }
    public void  setChoose_ans(int ans){
        this.choose_ans=ans;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public void setName(String name,int i) {
        this.name[i] = name;
    }

    public void setImageres(int imageres) {
        this.imageres = imageres;
    }
}
