package com.cxg.empattendance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cxg.empattendance.R;
import com.cxg.empattendance.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
* @description: 考勤首页
* @author xg.chen
* @create 2018/8/27
*/

public class MainActivity extends Activity {

    private TextView userId,userName,workshop;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        userId = (EditText) findViewById(R.id.userId);
        userName = (TextView) findViewById(R.id.userName);
        workshop = (TextView) findViewById(R.id.workshop);
    }

    /**
     * 初始化绑定数据
     */
    public void initData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            userId.setText(bundle.getString("userId"));
            userName.setText(bundle.getString("userName"));
            workshop.setText(bundle.getString("workshop"));
        }

    }

    /**
    * @description: 按钮监听事件，这里我使用Butterknife，不喜欢的也可以直接写监听
    * @author xg.chen
    * @create 2018/8/29
    */
    @OnClick({R.id.sigIn,R.id.leave_dimission})
    public void clickListener(View view){
        Intent intent;
        switch (view.getId()){
            case  R.id.sigIn: //签到
                intent=new Intent(this,EmpAttendanceActivity.class);
                Bundle useInfoBundle = new Bundle();
                useInfoBundle.putString("userId",userId.getText().toString().trim());
                useInfoBundle.putString("userName",userName.getText().toString().trim());
                useInfoBundle.putString("workshop",workshop.getText().toString().trim());
                intent.putExtras(useInfoBundle);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case  R.id.leave_dimission: //请假/离职
                intent=new Intent(this,CommonScanActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            default:
                break;
        }
    }
}
