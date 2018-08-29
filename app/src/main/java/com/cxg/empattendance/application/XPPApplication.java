package com.cxg.empattendance.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cxg.empattendance.R;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * application static final
 * Created by Administrator on 2017/5/3.
 */

public class XPPApplication extends Application {
    public static final int SUCCESS = 1;
    public static final int FAIL = 2;
    /**
     * 返回或者退出时的页面交互动作
     * */
    public static void  exit(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
    /**
     * @Description:send change broad
     * @author:xg.chen
     * @date:2017年6月5日 下午3:55:08
     * @param context
     * @param service
     * @param map
     * @version:1.0
     */
    public static void sendChangeBroad(Context context, String service,
                                       Map<String, String> map) {
        Intent i = new Intent(service);
        if (map != null) {
            for (Map.Entry entry : map.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                i.putExtra(key, value);
            }
        }
        context.sendBroadcast(i);
    }

    /**
     * @Description:open key board
     * @author:xg.chen
     * @date:2017年6月5日 下午3:56:19
     * @param et
     * @param activity
     * @version:1.0
     */
    public static void openKeyboard(final View et, Activity activity) {
        final Activity activity1 = activity;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity1
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 300);
    }

    /**
     * @Description:close key board
     * @author:xg.chen
     * @date:2017年6月5日 下午3:56:48
     * @param et
     * @param activity
     * @version:1.0
     */
    public static void closeKeyboard(final View et, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }
}
