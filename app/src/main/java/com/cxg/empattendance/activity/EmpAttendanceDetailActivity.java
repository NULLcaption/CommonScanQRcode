package com.cxg.empattendance.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cxg.empattendance.R;
import com.cxg.empattendance.adapter.EmpAttendanceDetailAdapter;
import com.cxg.empattendance.pojo.EmpInfo;
import com.cxg.empattendance.query.DataProviderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description: 考勤签到明细列表
* @author xg.chen
* @create 2018/8/24
*/

public class EmpAttendanceDetailActivity extends AppCompatActivity {

    private EmpAttendanceDetailAdapter empAttendanceDetailAdapter;
    private ListView empListView;
    private List<EmpInfo> empInfoList;
    private String userId;
    private Dialog overdialog;
    private Dialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_attendance_detail);
        empListView = (ListView) findViewById(R.id.empAttendanceDetail);//详单列表
        initData();
    }

    public void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            //根据班组长获取考勤详情列表
            try {
                new getEmpAttendanceDetailByUserId().execute(userId);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * description:获取上一个Activity的list绑定值
     * author: xg.chen
     * date: 2017/8/3 13:21
     * version: 1.0
     */
    public static List<String> extractMessageByRegular(String paramString) {
        ArrayList localArrayList = new ArrayList();
        Matcher localMatcher = Pattern.compile("(\\[[^\\]]*\\])").matcher(paramString);
        while (localMatcher.find()) {
            localArrayList.add(localMatcher.group().substring(1, -1 + localMatcher.group().length()));
        }
        return localArrayList;
    }


    /**
    * @description: 根据班组长获取考勤详情列表
    * @author xg.chen
    * @create 2018/8/24
    */
    private class getEmpAttendanceDetailByUserId extends AsyncTask<String, Integer, List<EmpInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingDialog();
        }
        @Override
        protected List<EmpInfo> doInBackground(String... params) {
            String userId = params[0];
            return DataProviderFactory.getProvider().getEmpAttendanceDetailByUserId(userId);
        }

        @Override
        protected void onPostExecute(List<EmpInfo> result) {
            dismissWaitingDialog();
            if (result != null) {
                empInfoList = result;
                gotoAdpter(empInfoList);
            } else {
                Toast.makeText(getApplicationContext(), "连接超时...退出稍后重试...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 考勤列表
     * @param empInfoList1
     */
    private void gotoAdpter(List<EmpInfo> empInfoList1) {
        if (empInfoList1==null) {
            empInfoList1 = new ArrayList<>();
        } else {
            empAttendanceDetailAdapter = new EmpAttendanceDetailAdapter(empInfoList1, this);
            empListView.setAdapter(empAttendanceDetailAdapter);
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
