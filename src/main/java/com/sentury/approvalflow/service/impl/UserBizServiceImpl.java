package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.PageResultDto;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.dto.third.UserQueryDto;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.service.IMenuService;
import com.sentury.approvalflow.service.IRoleService;
import com.sentury.approvalflow.service.IUserBizService;
import com.sentury.approvalflow.domain.vo.UserListQueryVO;
import com.sentury.approvalflow.domain.vo.third.UserDtoExtension;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-05
 */
@Service
public class UserBizServiceImpl  implements IUserBizService {

    @Lazy
    @Resource
    private IRoleService roleService;

    @Resource
    private IMenuService menuService;


    @Value("${role.allPermission}")
    private Boolean allPermission;







    /**
     * 获取当前用户详细信息
     *
     * @return
     */
    @Override
    public R2 getCurrentUserDetail() {
        String userId = LoginHelper.getUserId()+"";

        UserDto user = ApiStrategyFactory.getStrategy().getUser(userId);


        UserDtoExtension userDtoExtension = BeanUtil.copyProperties(user, UserDtoExtension.class);

        Set<String> roleKeySet = null;
        if (allPermission) {
            roleKeySet = roleService.list().stream().map(w -> w.getKey()).collect(Collectors.toSet());
        } else {
            roleKeySet = roleService.queryRoleKeyByUserId(userId).getData();
        }


        userDtoExtension.setRoles(roleKeySet);
        if (CollUtil.isNotEmpty(roleKeySet)) {
            userDtoExtension.setPerms(menuService.listRolePerms(roleKeySet).getData());

        } else {
            userDtoExtension.setPerms(new HashSet<>());
        }


        return R2.success(userDtoExtension);
    }


    /**
     * 用户管理 查询用户列表
     *
     * @param userListQueryVO
     * @return
     */
    @Override
    public R2 queryList(UserListQueryVO userListQueryVO) {

        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setDeptId(userListQueryVO.getDeptId());
        userQueryDto.setKeywords(userListQueryVO.getKeywords());
        userQueryDto.setStatus(userListQueryVO.getStatus());
        userQueryDto.setName(userListQueryVO.getName());
        userQueryDto.setDeptIdList(userListQueryVO.getDeptIdList());
        userQueryDto.setPageNum(userListQueryVO.getPageNum());
        userQueryDto.setPageSize(userListQueryVO.getPageSize());

        PageResultDto<UserDto> resultDto = ApiStrategyFactory.getStrategy().queryUserList(userQueryDto);
        return R2.success(resultDto);

    }

}
