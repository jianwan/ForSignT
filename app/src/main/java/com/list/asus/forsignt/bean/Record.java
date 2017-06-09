package com.list.asus.forsignt.bean;

/**
 * Created by wanjian on 2017/6/6.
 */

public class Record {

    private String stuName;
    private String stuId;
    private Boolean clockStatus;
    private String remark;


    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public Boolean getClockStatus() {
        return clockStatus;
    }

    public void setClockStatus(Boolean clockStatus) {
        this.clockStatus = clockStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
