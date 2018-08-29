package com.cxg.empattendance.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
* @description: 考勤实体类
* @author xg.chen
* @create 2018/8/23
*/
@DatabaseTable(tableName = "UserInfo")
public class UserInfo implements Serializable {

    @DatabaseField
    private String userId;
    @DatabaseField
    private String userName;
    @DatabaseField
    private String workshop;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }
}
