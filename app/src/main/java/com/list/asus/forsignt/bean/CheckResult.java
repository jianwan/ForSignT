package com.list.asus.forsignt.bean;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;


//考勤结果表
public class CheckResult extends BmobObject {

    private String checkID;
    private String stuId;
    private String stuName;
    private Boolean clockStatus;
    private String remark;


    public Boolean getClockStatus() {
        return clockStatus;
    }

    public void setCheckStatus(Boolean checkStatus) {
        this.clockStatus = checkStatus;
    }

    public String getCheckID() {
        return checkID;
    }

    public void setCheckID(String checkID) {
        this.checkID = checkID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }


}
