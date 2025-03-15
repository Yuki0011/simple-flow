package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.domain.vo.AutoLoginVO;
import com.sentury.approvalflow.domain.vo.UserBizVO;

import java.util.Map;

public interface ILoginService {

    /**
     * 登录
     *
     * @param userBizVO
     * @return
     */
    R2 login(UserBizVO userBizVO);

    /**
     * h5登录
     * @param userBizVO
     * @return
     */
    R2 loginAtH5(UserBizVO userBizVO);

    /**
     * token登录
     *
     * @param token
     * @return
     */
    R2 loginByToken(String token);

    /**
     * 退出登录
     * @return
     */
    R2 logout();

    /**
     * 钉钉登录
     * @param authCode
     * @return
     */
    R2 loginAtDingTalk(String authCode);

    /**
     * 获取登录地址
     * @return
     */
    R2 getLoginUrl();

    /**
     * 获取登录参数
     * @return
     */
    R2 getLoginParam(Map<String,Object> paramMap);


    /**
     * 自动登录
     * @param autoLoginVO
     * @return
     */
    R2 loginAuto(AutoLoginVO autoLoginVO);



}
