package com.cxg.empattendance.query;

import android.content.Context;

/**
* @description: data provide factory
* @author xg.chen
* @create 2018/8/23
*/

public class DataProviderFactory {

    public static Context ctx;
    public static Context getContext() {
        return ctx;
    }
    public static void setContext(Context ctx) {
        DataProviderFactory.ctx = ctx;
    }
    public static IDataProvider getProvider() {
        return WebService.getInstance();
    }

    public static IDataProvider getProvider(Context ctx) {
        DataProviderFactory.ctx = ctx;
        return getProvider();
    }

}
