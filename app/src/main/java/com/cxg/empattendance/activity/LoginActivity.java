package com.cxg.empattendance.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cxg.empattendance.R;
import com.cxg.empattendance.application.XPPApplication;
import com.cxg.empattendance.pojo.UserInfo;
import com.cxg.empattendance.query.DataProviderFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @description: 员工考勤系统登录页面
* @author xg.chen
* @create 2018/8/23
*/

public class LoginActivity extends AppCompatActivity {

    private EditText userId;
    private TextView userName,workshop;
    private Button btn_login;
    private Dialog waitingDialog;
    private LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    /**
    * @description: 初始化页面数据
    * @author xg.chen
    * @create 2018/8/23
    */
    public void init() {
        userId = (EditText) findViewById(R.id.userId);
        userId.setOnFocusChangeListener(ChangeListener);
        userId.setOnEditorActionListener(EnterListenter);
        userName = (TextView) findViewById(R.id.userName);
        workshop = (TextView) findViewById(R.id.workshop);
        findViewById(R.id.btn_login).setOnClickListener(BtnClicked);
    }

    private TextView.OnEditorActionListener EnterListenter = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.userId:
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                            && KeyEvent.ACTION_DOWN == event.getAction())) {
                        if (!"".equals(userId.getText().toString().trim())) {
                            // 正则判断下是否输入值为数字
//                            Pattern p2 = Pattern.compile("\\d");
//                            String userId1 = userId.getText().toString().trim();
//                            Matcher matcher = p2.matcher(userId1);
//                            if (matcher.matches()) {
//                                Toast.makeText(getApplicationContext(), "ZJJ:请输入准确的班组长工号!", Toast.LENGTH_SHORT).show();
//                            }
                            new getUserInfo4Workshop().execute(userId.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "ZJJ:请输入班组长工号!", Toast.LENGTH_SHORT).show();
                        }
                    }
                default:
                    break;
            }
            return false;
        }
    };

    /**
    * @description: 输入编号以后失去焦点后获取班组长的姓名车间
    * @author xg.chen
    * @create 2018/8/23
    */
    private View.OnFocusChangeListener ChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.userId:
                    if (!hasFocus) {
                        if (!"".equals(userId.getText().toString().trim())) {
                            // 正则判断下是否输入值为数字
//                            Pattern p2 = Pattern.compile("\\d");
//                            String userId1 = userId.getText().toString().trim();
//                            Matcher matcher = p2.matcher(userId1);
//                            if (matcher.matches()) {
//                                Toast.makeText(getApplicationContext(), "ZJJ:请输入准确的班组长工号!", Toast.LENGTH_SHORT).show();
//                            }
                            new getUserInfo4Workshop().execute(userId.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "ZJJ:请输入班组长工号!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
    * @description: 输入班组长工号以后获取姓名和车间
    * @author xg.chen
    * @create 2018/8/23
    */
    private class getUserInfo4Workshop extends AsyncTask<String, Integer, UserInfo> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingDialog();
        }

        @Override
        protected UserInfo doInBackground(String... params) {
            String string = params[0];
            return DataProviderFactory.getProvider().getUserInfo4Workshop(string) ;
        }

        @Override
        protected void onPostExecute(UserInfo result) {
            dismissWaitingDialog();
            if (result != null) {
                userName.setText(result.getUserName());
                workshop.setText(result.getWorkshop());
            } else {
                userName.setText("");
                workshop.setText("");
                Toast.makeText(getApplicationContext(), "连接超时(Login user is null)!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
    * @description: 按钮登录
    * @author xg.chen
    * @create 2018/8/23
    */
    private View.OnClickListener BtnClicked = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    tryLogin();
                    break;
            }
        }
    };

    /**
    * @description: 登录只是进入到下一个页面
    * @author xg.chen
    * @create 2018/8/23
    */
    private void tryLogin() {
        if ("".equals(userName.getText().toString())) {
            Toast.makeText(getApplicationContext(), "请按回车或者完成按键获取班组长登录信息!", Toast.LENGTH_SHORT).show();
        } else {
            //登录验证一下
            if (!"".equals(userId.getText().toString())) {
                loginTask = new LoginTask();
                //执行一个异步任务，需要我们在代码中调用此方法，触发异步任务的执行。
                loginTask.execute(userId.getText().toString());
            }
        }
    }

    /**
    * @description: 登录验证
    * @author xg.chen
    * @create 2018/8/24
    */
    private class LoginTask extends AsyncTask<String, Integer, Integer> {

        protected void onPreExecute() {
            showWaitingDialog();
        }

        @Override
        protected Integer doInBackground(String... params) {
            return DataProviderFactory.getProvider().loginUser(params[0]);
        }

        protected void onPostExecute(Integer result) {
            switch (result) {
                case XPPApplication.SUCCESS:
                    Intent intent = new Intent(LoginActivity.this, EmpAttendanceActivity.class);
                    Bundle useInfoBundle = new Bundle();
                    useInfoBundle.putString("userId",userId.getText().toString().trim());
                    useInfoBundle.putString("userName",userName.getText().toString().trim());
                    useInfoBundle.putString("workshop",workshop.getText().toString().trim());
                    intent.putExtras(useInfoBundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    break;
                case XPPApplication.FAIL:
                    userName.setText("");
                    workshop.setText("");
                    Toast.makeText(getApplicationContext(), "用户不存在，请重新输入！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            dismissWaitingDialog();
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
