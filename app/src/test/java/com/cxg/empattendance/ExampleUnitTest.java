package com.cxg.empattendance;

import com.cxg.empattendance.pojo.EmpInfo;
import com.cxg.empattendance.pojo.EmpTest;
import com.cxg.empattendance.pojo.Staff;
import com.cxg.empattendance.pojo.UserInfo;
import com.cxg.empattendance.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
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

    @Test
    public void initTest005() {
        try {
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_QU,
                    "createUser=1128055&remark="+URLEncoder.encode("离职")+"&staffId=0000000041&startDate="+URLEncoder.encode("2018-09-09 05:00:00")+"");
            Gson gson = new Gson();
            Staff staff = gson.fromJson(result, Staff.class);
            System.out.println(staff);
        } catch (Exception e) {

        }
    }

    @Test
    public void initTest004() {
        try {
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_LE,
                    "createUser=1128055&remark="+URLEncoder.encode("签到")+"&staffId=0000000041&startDate="+URLEncoder.encode("2018-09-09 05:00:00")+"&endDate="+URLEncoder.encode("2018-09-09 05:00:00")+"");
            Gson gson = new Gson();
            Staff staff = gson.fromJson(result, Staff.class);
            System.out.println(staff);
        } catch (Exception e) {

        }
    }

    @Test
    public void initTest03() {
        try {
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_AT_LIST,"createUser=a0004");
            Gson gson = new Gson();
            List<Staff> staffList = gson.fromJson(result, new TypeToken<List<Staff>>(){}.getType());

            System.out.println(staffList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initTest02() {
        try {
            String result = HttpUtils.sendGet(OPENAPIURL+RECODE_AT,"createUser=b.peng&staffId=0000000013&workshop=test&post=test&arts=test");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initTest01() {
        String result = null;
        try {
            Map<String,String> map = new HashMap<>();
            map.put("staffId","0000000013");
            result = HttpUtils.sendGet(OPENAPIURL+GETEMP,"staffId=0000000041");
            Gson gson = new Gson();
            List<Staff> staffList = gson.fromJson(result, new TypeToken<List<Staff>>(){}.getType());
            for (Staff staff: staffList) {
                System.out.println(staff.getStaffName());
            }
            System.out.println(staffList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    @Test
    public void initTest() {
        String result = null;
        try {
            result = HttpUtils.sendGet(OPENAPIURL+GETUSER,"code=1128055");
            Gson gson = new Gson();
            Staff staff = gson.fromJson(result, Staff.class);
            System.out.println(staff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    //@Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    //@Test
    public void initDate() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now =null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse("07:00");
            endTime = df.parse("19:00");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String flag = belongCalendar(now, beginTime, endTime);
        System.out.println(flag);
    }

    public static String belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return "白班";
        } else {
            return "夜班";
        }
    }
}