package com.cxg.empattendance.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2018/8/27.
 */
@DatabaseTable(tableName = "EmpTest")
public class EmpTest {
    @DatabaseField
    private String empId;
    @DatabaseField
    private String empName;
    @DatabaseField
    private String orgName;

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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return "EmpTest{" +
                "empId='" + empId + '\'' +
                ", empName='" + empName + '\'' +
                ", orgName='" + orgName + '\'' +
                '}';
    }
}
