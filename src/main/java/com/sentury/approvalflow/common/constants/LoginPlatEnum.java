package com.sentury.approvalflow.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录平台
 */
@Getter
@AllArgsConstructor
public enum LoginPlatEnum {

    ADMIN("admin","管理员后台"),
    H5("h5","H5"),
    DING_TALK("dingtalk","钉钉登录"),
    WX_WORK("wxwork","企微"),
    FEI_SHU("feishu","飞书"),
    WX_MIN_APP("wxMiniApp","微信小程序"),
    WX_CP_MIN_APP("wxCpMiniApp","企业微信小程序"),



    ;





    private String type;

    private String name;


}
