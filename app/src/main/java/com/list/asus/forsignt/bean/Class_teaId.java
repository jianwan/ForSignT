package com.list.asus.forsignt.bean;

import cn.bmob.v3.BmobObject;


public class Class_teaId extends BmobObject {

    private String teaId;
    private String teachingClass;


    public String getTeachingClass() {
        return teachingClass;
    }

    public void setTeachingClass(String teachingClass) {
        this.teachingClass = teachingClass;
    }

    public String getTeaId() {
        return teaId;
    }

    public void setTeaId(String teaId) {
        this.teaId = teaId;
    }


}
