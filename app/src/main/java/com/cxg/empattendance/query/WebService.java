package com.cxg.empattendance.query;

import android.app.Activity;
import android.provider.Settings;

import com.cxg.empattendance.application.XPPApplication;
import com.cxg.empattendance.pojo.EmpInfo;
import com.cxg.empattendance.pojo.EmpTest;
import com.cxg.empattendance.pojo.Staff;
import com.cxg.empattendance.pojo.UserInfo;
import com.cxg.empattendance.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @description: web service 接口
* @author xg.chen
* @create 2018/8/23
*/

public class WebService implements IDataProvider{

    private static WebService instance;

    private static final int TIME_OUT = 30000;

    private static final String TAG = "WebService";

    //接口请求地址
    private static String OPENAPIURL = "http://139.219.197.114:8881";
//    private static String OPENAPIURL = "http://10.3.9.141:8081";
    //传入班长的EXP登录名进行查询
    private static String GETUSER = "/attendAction/findMonitor";
    //查询员工信息
    private static String GETEMP = "/attendAction/findStaffList";
    //记录考勤信息
    private static String RECODE_AT = "/attendAction/recodeAttend";
    //获取考勤信息
    private static String RECODE_AT_LIST = "/attendAction/findAttendList";
    //请假
    private static String RECODE_LE = "/attendAction/leaveAttend";
    //离职
    private static String RECODE_QU = "/attendAction/quitAttend";

    private WebService() {
        super();
    }

    public static IDataProvider getInstance() {
        if (instance == null) instance = new WebService();
        return instance;
    }

    @Override
    public void startDateUpdateTasks(Activity activity) {

    }

    @Override
    public UserInfo getUserInfo4Workshop(String string) {
        UserInfo userInfo;
        try {
            //根据传入的Id获取班组长的姓名和车间
            String result = HttpUtils.sendGet(OPENAPIURL+GETUSER,"code="+string);
            Gson gson = new Gson();
            Staff staff = gson.fromJson(result, Staff.class);
            if (staff != null) {
                userInfo = new UserInfo();
                userInfo.setUserId(staff.getStaffCode());
                userInfo.setUserName(staff.getStaffName());
                userInfo.setWorkshop(staff.getWorkshop());
                return userInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer loginUser(String param) {
        try {
            //根据传入的Id获取班组长的姓名和车间
            String result = HttpUtils.sendGet(OPENAPIURL+GETUSER,"code="+param);
            if (!"".equals(result)) {
                return XPPApplication.SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return XPPApplication.FAIL;
    }

    @Override
    public List<EmpInfo> getEmpAttendanceDetailByUserId(String userId) {
        List<EmpInfo> empInfoList = new ArrayList<>();
        try {
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_AT_LIST,"createUser="+userId);
            Gson gson = new Gson();
            List<Staff> staffList = gson.fromJson(result, new TypeToken<List<Staff>>(){}.getType());
            for (Staff staff : staffList) {
                EmpInfo empInfo = new EmpInfo();
                if(staff.getStaffName() == null) {
                    empInfo.setEmpName("MASTER");
                } else {
                    empInfo.setEmpName(staff.getStaffName());
                }
                empInfo.setZlinecode(staff.getWorkshop());
                empInfo.setProfession(staff.getPost());
                empInfo.setDayTime(staff.getWorkDate());

                empInfoList.add(empInfo);
            }

            return empInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empInfoList;
    }

    @Override
    public EmpInfo getEmpNameById(String empId) {
        EmpInfo empInfo = new EmpInfo();
        try {
            String result = HttpUtils.sendGet(OPENAPIURL+GETEMP,"staffId="+empId);
            Gson gson = new Gson();
            List<Staff> staffList = gson.fromJson(result, new TypeToken<List<Staff>>(){}.getType());
            if (staffList.size() != 0) {
                empInfo.setEmpCode(staffList.get(0).getStaffCode());
                empInfo.setEmpName(staffList.get(0).getStaffName());
            }
            return empInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String signInForEmpByUserId(EmpInfo empInfo) {
        try{
            String createUser = empInfo.getUserId();
            String staffId = empInfo.getEmpId();
            String workshop = empInfo.getWorkshop();
            String post = empInfo.getProfession();
            String arts = empInfo.getTechnology();
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_AT,
                    "createUser="+createUser+"&staffId="+staffId+"&workshop="+workshop+"&post="+post+"&arts="+arts);
            if (result != null) {
                return "SUCCESS";
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    public String signInForEmpLeaveByUserId(EmpInfo empInfo) {
        try {
            String staffId = empInfo.getEmpId();
            String createUser = empInfo.getUserId();
            String startDate = empInfo.getLeaveStartDate()+":00";
            String endDate = empInfo.getLeaveDateEnd()+":00";
            String remark = empInfo.getNote();
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_LE,
                    "staffId="+staffId+"&createUser="+createUser
                            +"&startDate="+ URLEncoder.encode(startDate)
                            +"&endDate="+URLEncoder.encode(endDate)
                            +"&remark="+URLEncoder.encode(remark));
            if (result != null) {
                return "SUCCESS";
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    @Override
    public String signInForEmpDismissionByUserId(EmpInfo empInfo) {
        try {
            String staffId = empInfo.getEmpId();
            String createUser = empInfo.getUserId();
            String startDate = empInfo.getLeaveStartDate()+":00";
            String remark = empInfo.getNote();
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_QU,
                    "staffId="+staffId+"&createUser="+createUser
                            +"&startDate="+URLEncoder.encode(startDate)
                            +"&remark="+URLEncoder.encode(remark));
            if (result != null) {
                return "SUCCESS";
            } else {
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
}
