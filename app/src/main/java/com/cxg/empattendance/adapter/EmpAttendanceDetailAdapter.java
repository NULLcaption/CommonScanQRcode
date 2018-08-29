package com.cxg.empattendance.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxg.empattendance.R;
import com.cxg.empattendance.pojo.EmpInfo;

import java.util.ArrayList;
import java.util.List;

/**
* @description: 考勤详情适配
* @author xg.chen
* @create 2018/8/24
*/
public class EmpAttendanceDetailAdapter extends BaseAdapter {

    public List<EmpInfo> empInfoList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Activity activity;

    private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    public EmpAttendanceDetailAdapter(List<EmpInfo> parameterList, Activity activity) {
        params.leftMargin = 2;
        this.empInfoList = parameterList;
        this.layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return empInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return empInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*列表字段赋值*/
        ViewHolder hodler;
        if (convertView == null) {
            hodler = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.layout_sysinfo, null);
            hodler.empName = (TextView) convertView.findViewById(R.id.empName);//物料编码
            hodler.Zlinecode = (TextView) convertView.findViewById(R.id.Zlinecode);//物料描述
            hodler.profession = (TextView) convertView.findViewById(R.id.profession);//工厂
            hodler.dayTime = (TextView) convertView.findViewById(R.id.dayTime);//单据号
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHolder) convertView.getTag();
            resetViewHolder(hodler);
        }
        EmpInfo empInfo = empInfoList.get(position);
        hodler.empName.setText(empInfo.getEmpName());
        hodler.Zlinecode.setText(empInfo.getZlinecode());
        hodler.profession.setText(empInfo.getProfession());
        hodler.dayTime.setText(empInfo.getDayTime());

        /*设置列表的点击事件
        * 在这里预留接口
        * 在后期需要添加其他功能可用
        */
//        final int n = position;
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(activity, EmpAttendanceDetailActivity.class);
//                intent.putExtra("zslips",zslipsList.get(n));
//                activity.startActivity(intent);
//                activity.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
//            }
//        });

        return convertView;
    }

    private void resetViewHolder(ViewHolder pViewHodler) {
        pViewHodler.empName.setText(null);
        pViewHodler.Zlinecode.setText(null);
        pViewHodler.profession.setText(null);
        pViewHodler.dayTime.setText(null);
    }

    private static class ViewHolder {
        TextView empName = null;
        TextView Zlinecode = null;
        TextView profession = null;
        TextView dayTime = null;

    }
}
