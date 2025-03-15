package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.domain.entity.UserRole;
import com.sentury.approvalflow.mapper.UserRoleMapper;
import com.sentury.approvalflow.service.IUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户-角色 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-08
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    /**
     * 根据用户id获取角色key
     *
     * @param userId
     * @return
     */
    @Override
    public R2<List<UserRole>> queryListByUserId(String userId) {
        List<UserRole> userRoleList = this.lambdaQuery().eq(UserRole::getUserId, userId).list();
        return R2.success(userRoleList);
    }
}
