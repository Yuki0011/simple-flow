package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.NodeUser;

import java.util.List;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-08
 */
public interface IRoleBizService  {





    /**
     * 查询所有角色
     * @return
     */
    R2 queryAll();



    /**
     * 保存角色用户
     * @param nodeUserDtoList
     * @param id
     * @return
     */
    R2 saveUserList(List<NodeUser> nodeUserDtoList, String id);


}
