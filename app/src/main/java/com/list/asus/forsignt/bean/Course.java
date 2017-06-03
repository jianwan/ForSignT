package com.list.asus.forsignt.bean;

import cn.bmob.v3.BmobObject;



//课程表
public class Course extends BmobObject {

    private String courseId;
    private String courseName;


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
