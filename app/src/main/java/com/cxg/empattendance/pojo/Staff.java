package com.cxg.empattendance.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
* @description: 通过接口返员工实体类
* @author xg.chen
* @create 2018/8/28
*/
@DatabaseTable(tableName = "Staff")
public class Staff implements Serializable {
    @DatabaseField
    private String seq;
    @DatabaseField
    private String delFlag;
    @DatabaseField
    private String version;
    @DatabaseField
    private String staffName;
    @DatabaseField
    private String staffId;
    @DatabaseField
    private String staffCode;
    @DatabaseField
    private String workshop;
    @DatabaseField
    private String post;
    @DatabaseField
    private String arts;
    @DatabaseField
    private String joinOrganize;
    @DatabaseField
    private String workDate;

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getArts() {
        return arts;
    }

    public void setArts(String arts) {
        this.arts = arts;
    }

    public String getJoinOrganize() {
        return joinOrganize;
    }

    public void setJoinOrganize(String joinOrganize) {
        this.joinOrganize = joinOrganize;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "seq='" + seq + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", version='" + version + '\'' +
                ", staffName='" + staffName + '\'' +
                ", staffId='" + staffId + '\'' +
                ", staffCode='" + staffCode + '\'' +
                ", workshop='" + workshop + '\'' +
                ", post='" + post + '\'' +
                ", arts='" + arts + '\'' +
                ", joinOrganize='" + joinOrganize + '\'' +
                '}';
    }
}
