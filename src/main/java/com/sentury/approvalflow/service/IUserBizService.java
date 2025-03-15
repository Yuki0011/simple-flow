package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.domain.vo.UserListQueryVO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-05
 */
public interface IUserBizService  {



    /**
     * 获取当前用户详细信息
     *
     * @return
     */
    R2 getCurrentUserDetail();




    /**
     * 查询用户列表
     *
     * @param userListQueryVO
     * @return
     */
    R2 queryList(UserListQueryVO userListQueryVO);



}
