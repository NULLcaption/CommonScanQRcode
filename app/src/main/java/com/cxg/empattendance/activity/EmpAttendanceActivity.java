package com.cxg.empattendance.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import com.cxg.empattendance.pojo.UserInfo;
import com.cxg.empattendance.query.DataProviderFactory;
import com.cxg.empattendance.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description: 员工考勤系统首页
* @author xg.chen
* @create 2018/8/23
*/
public class EmpAttendanceActivity extends AppCompatActivity {

    private TextView workshop,dayTime,empName,userId,empCode;
    private EditText Zlinecode,Zbc,profession,technology,empId;
    private Button pinterButton,signIn,leave_dimission;
    private Dialog overdialog;
    private Dialog waitingDialog;
    //工种配置
    private List<String> listpro;
    //工艺配置
    private List<String> listtec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_attendance);
        initView();
        initData();
    }

    /**
    * @description: 视图
    * @author xg.chen
    * @create 2018/8/24
    */
    public void initView() {
        userId = (TextView) findViewById(R.id.userId);
        workshop = (TextView) findViewById(R.id.workshop);
        dayTime = (TextView) findViewById(R.id.dayTime);
        empName = (TextView) findViewById(R.id.empName);
        empCode = (TextView) findViewById(R.id.empCode);
        //选择框选择
        Zlinecode = (EditText) findViewById(R.id.Zlinecode);
        Zlinecode.setOnClickListener(BtnClicked);
        Zbc = (EditText) findViewById(R.id.Zbc);
        Zbc.setInputType(InputType.TYPE_NULL);
        profession = (EditText) findViewById(R.id.profession);
        profession.setOnClickListener(BtnClicked);
        technology = (EditText) findViewById(R.id.technology);
        technology.setOnClickListener(BtnClicked);
        //员工编号，这里需要调用手机摄像头扫描一维码获取员工编号
        empId = (EditText) findViewById(R.id.empId);
        empId.setOnClickListener(BtnClicked);
        empId.setOnEditorActionListener(EnterListenter);
        empId.setInputType(InputType.TYPE_NULL);
        //详情预览
        pinterButton = (Button)findViewById(R.id.printer);
        pinterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinterButton.setFocusable(true);
                pinterButton.requestFocusFromTouch();
                getEmpAttendanceDetail();
            }
        });
        //签到按钮
        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(BtnClicked);
        //请假&离职
        leave_dimission = (Button) findViewById(R.id.leave_dimission);
        leave_dimission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinterButton.setFocusable(true);
                pinterButton.requestFocusFromTouch();
                gotoLeaveAnddismission();
            }
        });
    }

    /**
    * @description: 请假&离职页面
    * @author xg.chen
    * @create 2018/9/7
    */
    public void gotoLeaveAnddismission() {
        Intent intent = new Intent(EmpAttendanceActivity.this, EmpLeaveAnddismissionActivity.class);
        Bundle useInfoBundle = new Bundle();
        useInfoBundle.putString("userId",userId.getText().toString());
        intent.putExtras(useInfoBundle);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
    * @description: 获取考勤明细
    * @author xg.chen
    * @create 2018/8/24
    */
    public void getEmpAttendanceDetail() {
        Intent intent = new Intent(EmpAttendanceActivity.this, EmpAttendanceDetailActivity.class);
        Bundle useInfoBundle = new Bundle();
        useInfoBundle.putString("userId",userId.getText().toString());
        intent.putExtras(useInfoBundle);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
    * @description: 数据
    * @author xg.chen
    * @create 2018/8/24
    */
    public void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            userId.setText(bundle.getString("userId"));
            workshop.setText(bundle.getString("workshop"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formatStr =formatter.format(new Date());
            dayTime.setText(formatStr);
            //班别
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
            Date now =null;
            Date beginTime = null;
            Date endTime = null;
            //7:00到19:00为白班，其余时间为夜班时间
            try {
                now = df.parse(df.format(new Date()));
                beginTime = df.parse("07:00");
                endTime = df.parse("19:00");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String flag = belongCalendar(now, beginTime, endTime);
            Zbc.setText(flag);

            //员工编号
            if (bundle.getString("empId") != null) {
                empId.setText(bundle.getString("empId"));
                if (!"".equals(bundle.getString("empId"))) {
                    Zlinecode.setText(bundle.getString("Zlinecode"));
                    profession.setText(bundle.getString("profession"));
                    technology.setText(bundle.getString("technology"));
                    // 根据员工的Id获取员工姓名
                    new getEmpNameById().execute(empId.getText().toString().trim());
                }

            }
        }
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
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                            && KeyEvent.ACTION_DOWN == event.getAction())) {
                        if (!"".equals(empId.getText().toString().trim())) {
                            // 正则判断下是否输入值为数字
//                            Pattern p2 = Pattern.compile("\\d");
//                            String userId1 = userId.getText().toString().trim();
//                            Matcher matcher = p2.matcher(userId1);
//                            if (matcher.matches()) {
//                                Toast.makeText(getApplicationContext(), "ZJJ:请输入准确的班组长工号!", Toast.LENGTH_SHORT).show();
//                            }
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
                case R.id.Zlinecode:
                    overdialog = null;
                    View overdiaView = View.inflate(EmpAttendanceActivity.this,
                            R.layout.dialog_search_msg, null);
                    overdialog = new Dialog(EmpAttendanceActivity.this,
                            R.style.dialog_xw);
                    ListView ZlinecodeList = (ListView) overdiaView
                            .findViewById(R.id.werksList);
                    TextView tv_title1 = (TextView) overdiaView
                            .findViewById(R.id.Title);
                    tv_title1.setText("请选择线别:");
                    List<String> list1 = new ArrayList<>();
                    for (int i = 0; i <= 20; i++) {
                        if (i <= 9) {
                            list1.add("0" + i);
                        } else {
                            list1.add("" + i);
                        }
                    }
                    SettingAdapter1 settingAdapter_Zlinecode = new SettingAdapter1(
                            getApplicationContext(), list1);
                    ZlinecodeList.setAdapter(settingAdapter_Zlinecode);
                    overdialog.setContentView(overdiaView);
                    overdialog.setCanceledOnTouchOutside(true);
                    Button overcancel1 = (Button) overdiaView
                            .findViewById(R.id.dialog_cancel_btn);
                    overcancel1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            overdialog.cancel();
                        }
                    });
                    overdialog.show();
                    break;
                case R.id.profession:
                    overdialog = null;
                    View overdiaView_pro = View.inflate(EmpAttendanceActivity.this,
                            R.layout.dialog_search_msg, null);
                    overdialog = new Dialog(EmpAttendanceActivity.this,
                            R.style.dialog_xw);
                    ListView professionList = (ListView) overdiaView_pro
                            .findViewById(R.id.werksList);
                    TextView tv_titlePro = (TextView) overdiaView_pro
                            .findViewById(R.id.Title);
                    tv_titlePro.setText("请选择工种:");
                    listpro = new ArrayList<>();
                    listpro.add("搬运工");
                    listpro.add("打包工");
                    listpro.add("组装工");
                    listpro.add("分拣工");
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
                case R.id.technology:
                    overdialog = null;
                    View overdiaView_tec = View.inflate(EmpAttendanceActivity.this,
                            R.layout.dialog_search_msg, null);
                    overdialog = new Dialog(EmpAttendanceActivity.this,
                            R.style.dialog_xw);
                    ListView technologyList = (ListView) overdiaView_tec
                            .findViewById(R.id.werksList);
                    TextView tv_titleTec = (TextView) overdiaView_tec
                            .findViewById(R.id.Title);
                    tv_titleTec.setText("请选择工艺:");
                    listtec = new ArrayList<>();
                    listtec.add("搬运");
                    listtec.add("打包");
                    listtec.add("组装");
                    listtec.add("分拣");
                    SettingAdapterTec settingAdapterTec = new SettingAdapterTec(
                            getApplicationContext(), listtec);
                    technologyList.setAdapter(settingAdapterTec);
                    overdialog.setContentView(overdiaView_tec);
                    overdialog.setCanceledOnTouchOutside(true);
                    Button overcancelTec = (Button) overdiaView_tec
                            .findViewById(R.id.dialog_cancel_btn);
                    overcancelTec.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            overdialog.cancel();
                        }
                    });
                    overdialog.show();
                    break;
                case R.id.empId :
                    //扫描员工的一维码
                    Intent intent=new Intent(EmpAttendanceActivity.this,CommonScanActivity.class);
                    Bundle useInfoBundle = new Bundle();
                    useInfoBundle.putString("userId",userId.getText().toString().trim());
                    useInfoBundle.putString("workshop",workshop.getText().toString().trim());
                    if (!"".equals(Zlinecode.getText().toString().trim())) {
                        useInfoBundle.putString("Zlinecode",Zlinecode.getText().toString().trim());
                    } else {
                        Toast.makeText(getApplicationContext(), "请选择线别", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    useInfoBundle.putString("Zbc",Zbc.getText().toString().trim());
                    //工种和工艺会在返回的empInfo中
                    useInfoBundle.putString("profession",profession.getText().toString().trim());
                    useInfoBundle.putString("technology",technology.getText().toString().trim());
                    // 扫描条形码界面
                    useInfoBundle.putString("code","ScanMode");
                    useInfoBundle.putString("mode",String.valueOf(0X100));
                    intent.putExtras(useInfoBundle);
                    startActivity(intent);
                    break;
                case R.id.signIn :
                    if ("".equals(empName.getText().toString().trim())
                            || "".equals(Zlinecode.getText().toString().trim())
                            || "".equals(profession.getText().toString().trim())
                            || "".equals(technology.getText().toString().trim())) {
                        Toast.makeText(getApplicationContext(), "签到信息不完整，不能签到！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        EmpInfo empInfo = new EmpInfo();
                        empInfo.setUserId(userId.getText().toString().trim());
                        empInfo.setWorkshop(workshop.getText().toString().trim());
                        empInfo.setZlinecode(Zlinecode.getText().toString().trim());
                        empInfo.setZbc(Zbc.getText().toString().trim());
                        empInfo.setProfession(profession.getText().toString().trim());
                        empInfo.setTechnology(technology.getText().toString().trim());
                        empInfo.setEmpId(empId.getText().toString().trim());
                        empInfo.setEmpName(empName.getText().toString().trim());
                        empInfo.setDayTime(dayTime.getText().toString().trim());
                        new signInForEmpByUserId().execute(empInfo);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
    * @description: 签到
    * @author xg.chen
    * @create 2018/8/27
    */
    private class signInForEmpByUserId extends AsyncTask<EmpInfo, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingDialog();
        }

        @Override
        protected String doInBackground(EmpInfo... params) {
            EmpInfo empInfo = params[0];
            return DataProviderFactory.getProvider().signInForEmpByUserId(empInfo);
        }

        @Override
        protected void onPostExecute(String result) {
            dismissWaitingDialog();
            if (result != null) {
                if (result.equals("SUCCESS")) {
                    empId.setText("");
                    empName.setText("");
                    Toast.makeText(getApplicationContext(), "签到成功!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "签到失败!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "连接超时,签到失败!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
    * @description: 判断时间是否在时间段内
    * @author xg.chen
    * @create 2018/8/27
    */
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

    /**
    * @description: 工艺适配器
    * @author xg.chen
    * @create 2018/8/24
    */
    protected class ViewHodlerTec {
        TextView stringList = null;
    }

    protected void resetViewHolder(ViewHodlerTec pViewHolder) {
        pViewHolder.stringList.setText(null);
    }

    public class SettingAdapterTec extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public SettingAdapterTec(Context context, List<String> data) {
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
            ViewHodlerTec hodler;
            if (convertView == null) {
                // 获取组件布局
                hodler = new ViewHodlerTec();
                convertView = layoutInflater.inflate(
                        R.layout.dialog_search_list_child, null);
                hodler.stringList = (TextView) convertView
                        .findViewById(R.id.werksName);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodlerTec) convertView.getTag();
                resetViewHolder(hodler);
            }

            hodler.stringList.setText(data.get(position));
            // 绑定数据、以及事件触发
            final int n = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    technology.setText(data.get(n));
                    overdialog.cancel();
                }
            });
            return convertView;
        }
    }

    /**
    * @description: 工种是配置器
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
                    profession.setText(data.get(n));
                    overdialog.cancel();
                }
            });
            return convertView;
        }
    }

    /**
     * description: 线别适配器
     * author: xg.chen
     * date: 2017/6/26 9:00
     * version: 1.0
     */
    protected class ViewHodler1 {
        TextView stringList = null;
    }

    protected void resetViewHolder(ViewHodler1 pViewHolder) {
        pViewHolder.stringList.setText(null);
    }

    public class SettingAdapter1 extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public SettingAdapter1(Context context, List<String> data) {
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
            ViewHodler1 hodler;
            if (convertView == null) {
                // 获取组件布局
                hodler = new ViewHodler1();
                convertView = layoutInflater.inflate(
                        R.layout.dialog_search_list_child, null);
                hodler.stringList = (TextView) convertView
                        .findViewById(R.id.werksName);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler1) convertView.getTag();
                resetViewHolder(hodler);
            }

            hodler.stringList.setText(data.get(position));
            // 绑定数据、以及事件触发
            final int n = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    // channelId = data.get(n);
                    Zlinecode.setText(data.get(n));
                    overdialog.cancel();
                }
            });
            return convertView;
        }
    }

    /**
     * description: 班别适配器
     * author: xg.chen
     * date: 2017/6/26 9:00
     * version: 1.0
     */
    protected class ViewHodlerZbc {
        TextView stringList = null;
    }

    protected void resetViewHolder(ViewHodlerZbc pViewHolder) {
        pViewHolder.stringList.setText(null);
    }

    public class SettingAdapterZbc extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public SettingAdapterZbc(Context context, List<String> data) {
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
            ViewHodlerZbc hodler;
            if (convertView == null) {
                // 获取组件布局
                hodler = new ViewHodlerZbc();
                convertView = layoutInflater.inflate(
                        R.layout.dialog_search_list_child, null);
                hodler.stringList = (TextView) convertView
                        .findViewById(R.id.werksName);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodlerZbc) convertView.getTag();
                resetViewHolder(hodler);
            }

            hodler.stringList.setText(data.get(position));
            // 绑定数据、以及事件触发
            final int n = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Zbc.setText(data.get(n));
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
