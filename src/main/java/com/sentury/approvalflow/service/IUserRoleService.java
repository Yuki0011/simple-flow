package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.UserRole;

import java.util.List;

/**
 * <p>
 * 用户-角色 服务类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-08
 */
public interface IUserRoleService extends IService<UserRole> {
    /**
     * 根据用户id获取角色key
     *
     * @param userId
     * @return
     */
    R2<List<UserRole>> queryListByUserId(String userId);

}
