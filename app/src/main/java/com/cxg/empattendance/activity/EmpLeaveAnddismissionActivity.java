package com.cxg.empattendance.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cxg.empattendance.R;
import com.cxg.empattendance.pojo.EmpInfo;
import com.cxg.empattendance.query.DataProviderFactory;
import com.cxg.empattendance.utils.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description: 请假和离职页面
* @author xg.chen
* @create 2018/9/7
*/

public class EmpLeaveAnddismissionActivity extends AppCompatActivity {

    private TextView empName,userId,empCode;
    private EditText leaveStartDate,leaveDateEnd,note,empId,leave;
    private Button signInOk,scanCode,returnHome;
    private String userIdBind;
    private Dialog overdialog;
    private Dialog waitingDialog;
    private DatePicker leaveStart;
    private DatePicker leaveEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_leave_dismission);
        initView();
        initData();
        userId.setText(userIdBind);
        leave.setText("请假");
    }

    /**
    * @description: 初始化界面
    * @author xg.chen
    * @create 2018/9/7
    */
    private void initView() {
        userId = (TextView) findViewById(R.id.userId);
        empName = (TextView) findViewById(R.id.empName);
        empCode = (TextView) findViewById(R.id.empCode);
        note = (EditText) findViewById(R.id.note);
        //员工编号，这里需要调用手机摄像头扫描一维码获取员工编号
        empId = (EditText) findViewById(R.id.empId);
        empId.setOnEditorActionListener(EnterListenter);
        scanCode = (Button) findViewById(R.id.scanCode);
        scanCode.setOnClickListener(BtnClicked);
        leave = (EditText) findViewById(R.id.leave);
        leave.setOnClickListener(BtnClicked);
        leaveStartDate = (EditText) findViewById(R.id.leaveStartDate);
        leaveStartDate.setOnClickListener(BtnClicked);
        leaveDateEnd = (EditText) findViewById(R.id.leaveDateEnd);
        leaveDateEnd.setOnClickListener(BtnClicked);
        signInOk = (Button) findViewById(R.id.signInOk);
        signInOk.setOnClickListener(BtnClicked);
        returnHome = (Button) findViewById(R.id.returnHome);
        returnHome.setOnClickListener(mGoBack);
    }

    /**
    * @description: 返回按钮事件监听
    * @author xg.chen
    * @create 2018/9/8
    */
    public View.OnClickListener mGoBack = new View.OnClickListener() {
        public void onClick(View v) {
            finish();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };

    /**
    * @description: 初始化数据
    * @author xg.chen
    * @create 2018/9/7
    */
    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            userIdBind = bundle.getString("userId");
            //员工编号
            if (bundle.getString("empId") != null) {
                empId.setText(bundle.getString("empId"));
                if (!"".equals(bundle.getString("empId"))) {
                    // 根据员工的Id获取员工姓名
                    new getEmpNameById().execute(empId.getText().toString().trim());
                }
            }
        }

        //时间选择控件
        selectDatePicker();

    }

    /**
     * description: 选择时间控件
     * author: xg.chen
     * date: 2017/6/26 11:15
     * version: 1.0
     */
    private void selectDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        leaveStartDate.setText(now);
        leaveDateEnd.setText(now);

        leaveStart = new DatePicker(this, new DatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                leaveStartDate.setText(time);
            }
        }, "2010-01-01 00:00", "2099-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        leaveStart.showSpecificTime(true); // 不显示时和分false
        leaveStart.setIsLoop(true); // 不允许循环滚动*/

        leaveEnd = new DatePicker(this, new DatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                leaveDateEnd.setText(time);
            }
        }, "2010-01-01 00:00", "2099-01-01 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        leaveEnd.showSpecificTime(true); // 显示时和分true
        leaveEnd.setIsLoop(true); // 允许循环滚动
    }

    /**
     * @description: 员工编号不为空的时候时候处理数据
     * @author xg.chen
     * @create 2018/8/28
     */
    private TextView.OnEditorActionListener EnterListenter = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.empId:
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || actionId == EditorInfo.IME_ACTION_NEXT
                            || actionId == EditorInfo.IME_ACTION_GO
                            || actionId == EditorInfo.IME_ACTION_SEARCH
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                            && KeyEvent.ACTION_DOWN == event.getAction())) {
                        if (!"".equals(empId.getText().toString().trim())) {
                            // 正则判断下是否输入值为数字
                            Pattern p2 = Pattern.compile("\\d");
                            String userId1 = userId.getText().toString().trim();
                            Matcher matcher = p2.matcher(userId1);
                            if (matcher.matches()) {
                                Toast.makeText(getApplicationContext(), "ZJJ:请输入准确的班组长工号!", Toast.LENGTH_SHORT).show();
                            }
                            new getEmpNameById().execute(empId.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "ZJJ:扫描入工号!", Toast.LENGTH_SHORT).show();
                        }
                    }
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * @description: 根据员工的Id获取员工姓名
     * @author xg.chen
     * @create 2018/8/27
     */
    private class getEmpNameById  extends AsyncTask<String, Integer, EmpInfo> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingDialog();
        }

        @Override
        protected EmpInfo doInBackground(String... params) {
            String empId = params[0];
            return DataProviderFactory.getProvider().getEmpNameById(empId);
        }

        @Override
        protected void onPostExecute(EmpInfo result) {
            dismissWaitingDialog();
            if (result != null) {
                empCode.setText(result.getEmpCode());
                empName.setText(result.getEmpName());
            } else {
                empCode.setText("");
                empName.setText("");
                Toast.makeText(getApplicationContext(), "连接超时(Emp is null)!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @description: 输入框监听事件
     * @author xg.chen
     * @create 2018/8/24
     */
    private View.OnClickListener BtnClicked = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.leaveStartDate:
                    // 日期格式为yyyy-MM-dd
                    leaveStart.show(leaveStartDate.getText().toString());
                    break;
                case R.id.leaveDateEnd:
                    leaveEnd.show(leaveDateEnd.getText().toString());
                    break;
                case R.id.leave:
                    overdialog = null;
                    View overdiaView_pro = View.inflate(EmpLeaveAnddismissionActivity.this,
                            R.layout.dialog_search_msg, null);
                    overdialog = new Dialog(EmpLeaveAnddismissionActivity.this,
                            R.style.dialog_xw);
                    ListView professionList = (ListView) overdiaView_pro
                            .findViewById(R.id.werksList);
                    TextView tv_titlePro = (TextView) overdiaView_pro
                            .findViewById(R.id.Title);
                    tv_titlePro.setText("请选择:");
                    List<String> listpro = new ArrayList<>();
                    listpro.add("请假");
                    listpro.add("离职");
                    SettingAdapterPro settingAdapterPro = new SettingAdapterPro(
                            getApplicationContext(), listpro);
                    professionList.setAdapter(settingAdapterPro);
                    overdialog.setContentView(overdiaView_pro);
                    overdialog.setCanceledOnTouchOutside(true);
                    Button overcancelPro = (Button) overdiaView_pro
                            .findViewById(R.id.dialog_cancel_btn);
                    overcancelPro.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            overdialog.cancel();
                        }
                    });
                    overdialog.show();
                    break;
                case R.id.scanCode :
                    //扫描员工的一维码
                    Intent intent=new Intent(EmpLeaveAnddismissionActivity.this,CommonScan4LeaveActivity.class);
                    Bundle useInfoBundle = new Bundle();
                    useInfoBundle.putString("userId",userId.getText().toString().trim());
                    // 扫描条形码界面
                    useInfoBundle.putString("code","ScanMode");
                    useInfoBundle.putString("mode",String.valueOf(0X100));
                    intent.putExtras(useInfoBundle);
                    startActivity(intent);
                    break;
                case R.id.signInOk :
                    if ("".equals(empName.getText().toString().trim())
                            || "".equals(leave.getText().toString().trim())
                            || "".equals(empId.getText().toString().trim())
                            || "".equals(note.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "签到信息不完整，不能签到！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        EmpInfo empInfo = new EmpInfo();
                        empInfo.setUserId(userId.getText().toString().trim());
                        empInfo.setEmpId(empId.getText().toString().trim());
                        empInfo.setEmpName(empName.getText().toString().trim());
                        empInfo.setLeave(leave.getText().toString().trim());
                        empInfo.setNote(note.getText().toString().trim());
                        if ("请假".equals(leave.getText().toString().trim())) {
                            //时间判断 开始时间大于结束结束时间
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String stastDate = leaveStartDate.getText().toString().trim();
                            String endDate = leaveDateEnd.getText().toString().trim();
                            Date begin;
                            Date end;
                            try {
                                begin = sdf.parse(stastDate);
                                end = sdf.parse(endDate);
                                if(begin.getTime() >= end.getTime()) {
                                    Toast.makeText(getApplicationContext(), "请假时间有误!", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    empInfo.setLeaveStartDate(leaveStartDate.getText().toString().trim());
                                    empInfo.setLeaveDateEnd(leaveDateEnd.getText().toString().trim());
                                    new signInForEmpLeaveByUserId().execute(empInfo);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            empInfo.setLeaveStartDate(leaveStartDate.getText().toString().trim());
                            new signInForEmpDismissionByUserId().execute(empInfo);
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
    * @description: 请假
    * @author xg.chen
    * @create 2018/9/8
    */
    private class signInForEmpLeaveByUserId extends AsyncTask<EmpInfo, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingDialog();
        }

        @Override
        protected String doInBackground(EmpInfo... params) {
            EmpInfo empInfo = params[0];
            return DataProviderFactory.getProvider().signInForEmpLeaveByUserId(empInfo);
        }

        @Override
        protected void onPostExecute(String result) {
            dismissWaitingDialog();
            if (result != null) {
                if (result.equals("SUCCESS")) {
                    empId.setText("");
                    empCode.setText("");
                    empName.setText("");
                    Toast.makeText(getApplicationContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "确认失败!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "连接超时,确认失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
    * @description: 离职
    * @author xg.chen
    * @create 2018/9/8
    */
    private class signInForEmpDismissionByUserId extends AsyncTask<EmpInfo, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingDialog();
        }

        @Override
        protected String doInBackground(EmpInfo... params) {
            EmpInfo empInfo = params[0];
            return DataProviderFactory.getProvider().signInForEmpDismissionByUserId(empInfo);
        }

        @Override
        protected void onPostExecute(String result) {
            dismissWaitingDialog();
            if (result != null) {
                if (result.equals("SUCCESS")) {
                    empId.setText("");
                    empCode.setText("");
                    empName.setText("");
                    Toast.makeText(getApplicationContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "确认失败!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "连接超时,确认失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @description: 配置器
     * @author xg.chen
     * @create 2018/8/24
     */
    protected class ViewHodlerPro {
        TextView stringList = null;
    }

    protected void resetViewHolder(ViewHodlerPro pViewHolder) {
        pViewHolder.stringList.setText(null);
    }

    public class SettingAdapterPro extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public SettingAdapterPro(Context context, List<String> data) {
            this.data = data;
            this.layoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodlerPro hodler;
            if (convertView == null) {
                // 获取组件布局
                hodler = new ViewHodlerPro();
                convertView = layoutInflater.inflate(
                        R.layout.dialog_search_list_child, null);
                hodler.stringList = (TextView) convertView
                        .findViewById(R.id.werksName);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodlerPro) convertView.getTag();
                resetViewHolder(hodler);
            }

            hodler.stringList.setText(data.get(position));
            // 绑定数据、以及事件触发
            final int n = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    leave.setText(data.get(n));
                    overdialog.cancel();
                }
            });
            return convertView;
        }
    }

    /**
     * description: 加载图片开始
     * author: xg.chen
     * date: 2017/6/26 11:56
     * version: 1.0
     */
    private void showWaitingDialog() {
        if (waitingDialog == null) {

            waitingDialog = new Dialog(this, R.style.TransparentDialog);
            waitingDialog.setContentView(R.layout.login_waiting_dialog);
            DialogInterface.OnShowListener showListener = new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ImageView img = (ImageView) waitingDialog.findViewById(R.id.loading);
                    ((AnimationDrawable) img.getDrawable()).start();
                }
            };
            DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // updateButtonLook(false);
                }
            };
            waitingDialog.setOnShowListener(showListener);
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.setOnCancelListener(cancelListener);
            waitingDialog.show();
        }
    }

    /**
     * description: 加载结束
     * author: xg.chen
     * date: 2017/6/26 11:56
     * version: 1.0
     */
    private void dismissWaitingDialog() {
        if (waitingDialog != null) {
            ImageView img = (ImageView) waitingDialog.findViewById(R.id.loading);
            ((AnimationDrawable) img.getDrawable()).stop();

            waitingDialog.dismiss();
            waitingDialog = null;
        }
    }

}
