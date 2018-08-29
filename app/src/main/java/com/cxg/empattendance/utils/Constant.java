package com.cxg.empattendance.utils;

/**
 * Created by 刘红亮 on 2015/9/24 14:08.
 */
public interface Constant {
    /**
     * 二维码请求的type
     */
    String REQUEST_SCAN_TYPE="type";
    /**
     * 普通类型，扫完即关闭
     */
    int REQUEST_SCAN_TYPE_COMMON=0;
    /**
     * 服务商登记类型，扫描
     */
    int REQUEST_SCAN_TYPE_REGIST=1;


    /**
     * 扫描类型
     * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
     * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
     * 二维码：REQUEST_SCAN_MODE_QRCODE_MODE
     *
     */
    String REQUEST_SCAN_MODE="ScanMode";
    /**
     * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
     */
    int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;
    /**
     * 二维码：REQUEST_SCAN_MODE_ALL_MODE
     */
    int REQUEST_SCAN_MODE_QRCODE_MODE = 0X200;
    /**
     * 条形码或者二维码：REQUEST_SCAN_MODE_ALL_MODE
     */
    int REQUEST_SCAN_MODE_ALL_MODE = 0X300;

}
