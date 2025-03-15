package com.sentury.approvalflow.common.utils;

import cn.hutool.core.util.StrUtil;

/**
 * @author zhj
 * @version 1.0
 * @description: TODO
 * @date 2024/3/11 22:30
 */
public class PlatformUtil {
    /**
     * 判断是否在企微里
     * @param userAgent
     * @return
     */
    public static boolean isWxWork(String userAgent){
        return StrUtil.containsIgnoreCase(userAgent,"wxwork");
    }

    /**
     * 判断是否在钉钉里
     * @param userAgent
     * @return
     */
    public static boolean isDingTalk(String userAgent){
        return StrUtil.containsIgnoreCase(userAgent,"DingTalk");
    }

    /**
     * 判断是否在飞书里
     * @param userAgent
     * @return
     */
    public static boolean isFeiShu(String userAgent){
        return StrUtil.containsIgnoreCase(userAgent,"Lark");
    }
}
