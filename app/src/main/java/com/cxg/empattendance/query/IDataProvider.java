package com.cxg.empattendance.query;

import android.app.Activity;

import com.cxg.empattendance.pojo.EmpInfo;
import com.cxg.empattendance.pojo.UserInfo;

import java.util.List;

/**
* @description: service interface
* @author xg.chen
* @create 2018/8/23
*/

public interface IDataProvider {

    /**
     * init data
     * @param activity ui
     */
    void startDateUpdateTasks(Activity activity);

    /**
     * 根据输入的Id获取班组长的信息
     * @param string
     * @return
     */
    UserInfo getUserInfo4Workshop(String string);

    /**
     * 登录验证
     * @param param
     * @return
     */
    Integer loginUser(String param);

    /**
     * 根据班组长获取考勤详情列表
     * @param userId
     * @return
     */
    List<EmpInfo> getEmpAttendanceDetailByUserId(String userId);

    /**
     * 根据empId获取员工的姓名
     * @param empId
     * @return
     */
    EmpInfo getEmpNameById(String empId);

    /**
     * 签到
     * @param empInfo
     * @return
     */
    String signInForEmpByUserId(EmpInfo empInfo);
}
