package com.cxg.empattendance.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
* @description: 考勤人员实体类
* @author xg.chen
* @create 2018/8/24
*/
@DatabaseTable(tableName = "EmpInfo")
public class EmpInfo implements Serializable {

    @DatabaseField
    private String empId;
    @DatabaseField
    private String empName;
    @DatabaseField
    private String empCode;
    @DatabaseField
    private String Zlinecode;
    @DatabaseField
    private String Zbc;
    @DatabaseField
    private String profession;
    @DatabaseField
    private String technology;
    @DatabaseField
    private String dayTime;
    @DatabaseField
    private String userId;
    @DatabaseField
    private String workshop;
    @DatabaseField
    private String leave;
    @DatabaseField
    private String note;
    @DatabaseField
    private String leaveStartDate;
    @DatabaseField
    private String leaveDateEnd;

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLeaveStartDate() {
        return leaveStartDate;
    }

    public void setLeaveStartDate(String leaveStartDate) {
        this.leaveStartDate = leaveStartDate;
    }

    public String getLeaveDateEnd() {
        return leaveDateEnd;
    }

    public void setLeaveDateEnd(String leaveDateEnd) {
        this.leaveDateEnd = leaveDateEnd;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getZlinecode() {
        return Zlinecode;
    }

    public void setZlinecode(String zlinecode) {
        Zlinecode = zlinecode;
    }

    public String getZbc() {
        return Zbc;
    }

    public void setZbc(String zbc) {
        Zbc = zbc;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    @Override
    public String toString() {
        return "EmpInfo{" +
                "empId='" + empId + '\'' +
                ", empName='" + empName + '\'' +
                ", Zlinecode='" + Zlinecode + '\'' +
                ", Zbc='" + Zbc + '\'' +
                ", profession='" + profession + '\'' +
                ", technology='" + technology + '\'' +
                ", dayTime='" + dayTime + '\'' +
                ", userId='" + userId + '\'' +
                ", workshop='" + workshop + '\'' +
                '}';
    }
}
