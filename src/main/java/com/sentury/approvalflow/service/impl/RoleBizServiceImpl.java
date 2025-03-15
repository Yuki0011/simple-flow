package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.third.RoleDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.entity.UserRole;
import com.sentury.approvalflow.service.IRoleBizService;
import com.sentury.approvalflow.service.IUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2023-06-08
 */
@Service
public class RoleBizServiceImpl  implements IRoleBizService {
    @Resource
    private IUserRoleService userRoleService;



    /**
     * 查询所有角色
     *
     * @return
     */
    @Override
    public R2 queryAll() {
        List<RoleDto> roleDtoList = ApiStrategyFactory.getStrategy().loadAllRole();
        return R2.success(roleDtoList);
    }



    /**
     * 保存角色用户
     *
     * @param nodeUserDtoList
     * @param id
     * @return
     */
    @Transactional
    @Override
    public R2 saveUserList(List<NodeUser> nodeUserDtoList, String id) {
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getRoleId, id);
        userRoleService.remove(queryWrapper);

        for (NodeUser nodeUserDto : nodeUserDtoList) {
            UserRole userRole = new UserRole();

            userRole.setUserId(nodeUserDto.getId());
            userRole.setRoleId(id);
            userRoleService.save(userRole);

        }

        return R2.success();
    }
}
