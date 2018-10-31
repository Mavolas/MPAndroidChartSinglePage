package com.ziruk.base.zdsviewer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by 宋棋安
 * on 2018/10/29.
 */
public class DateUtil {

    public static String formatDate(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sf2 = new SimpleDateFormat("MM-dd");
        String formatStr = "";
        try {
            formatStr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }
}
