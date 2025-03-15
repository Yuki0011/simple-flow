package com.sentury.approvalflow.common.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class DateUtil {
    /**
     * 日期显示
     * @param date
     * @return
     */
    public static String dateShow(Date date) {
        if (date == null) {
            return "";
        }
        Date currentDate = new Date();
        if (cn.hutool.core.date.DateUtil.isSameDay(date, currentDate)) {
            return cn.hutool.core.date.DateUtil.format(date, "HH:mm");
        }

        if (cn.hutool.core.date.DateUtil.year(date) == cn.hutool.core.date.DateUtil.year(currentDate)) {
            return cn.hutool.core.date.DateUtil.format(date, "MM-dd HH:mm");
        }
        if(cn.hutool.core.date.DateUtil.offsetMonth(date,6).isAfterOrEquals(currentDate)){
            //半年内
            return cn.hutool.core.date.DateUtil.format(date, "MM-dd HH:mm");
        }
        return cn.hutool.core.date.DateUtil.format(date, "yyyy-MM-dd HH:mm");

    }


    public static boolean isDate(String s){
        if(StrUtil.isBlank(s)){
            return false;
        }

        return ReUtil.isMatch("^\\d{4}-\\d{2}-\\d{2}$",s);
    }
    public static boolean isDateTime(String s){
        if(StrUtil.isBlank(s)){
            return false;
        }

        return ReUtil.isMatch("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}(:\\d{2})$",s);
    }
    public static boolean isTime(String s){
        if(StrUtil.isBlank(s)){
            return false;
        }

        return ReUtil.isMatch("^\\d{2}:\\d{2}:\\d{2}$",s);
    }

}
