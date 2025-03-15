package com.sentury.approvalflow.biz.api.impl;

import com.sentury.approvalflow.common.constants.StatusEnum;
import com.sentury.approvalflow.common.dto.LoginUrlDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.*;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.sentury.approvalflow.biz.api.ApiStrategy;
import com.sentury.approvalflow.domain.entity.*;
import com.sentury.approvalflow.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LocalApi implements ApiStrategy, InitializingBean {

    @Resource
    private ICombinationGroupService combinationGroupService;

    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private IDeptLeaderService deptLeaderService;

    @Resource
    private IRoleService roleService;
    @Resource
    private IDeptService deptService;
    @Resource
    private IUserService userService;
    @Resource
    private IDeptUserService deptUserService;

    @Value("${login.captcha}")
    private Boolean captcha;

    /**
     * 账号密码登录
     *
     * @param password 密码
     * @param account  账号
     * @return 用户id
     */
    @Override
    public String loginWeb(String password, String account) {
        String pwd = SecureUtil.md5(password);

        User u = userService.lambdaQuery()
                .eq(User::getPhone, account)
                .eq(User::getPassword, pwd)
                .eq(User::getStatus, StatusEnum.ENABLE.getValue())

                .one();
        if (u != null) {
            return String.valueOf(u.getId());
        }

        return null;
    }

    /**
     * 查询用户列表
     *
     * @param userQueryDto
     * @return
     */
    @Override
    public PageResultDto<UserDto> queryUserList(UserQueryDto userQueryDto) {
        return userService.queryLocalList(userQueryDto);
    }

    /**
     * 根据部门id集合和角色id集合查询人员id集合
     *
     * @param userQueryDto 查询参数
     * @return
     */
    @Override
    public List<String> queryUserIdListByRoleIdListAndDeptIdList(UserQueryDto userQueryDto) {
        R2<List<String>> listR = combinationGroupService.queryUserListByDeptIdListAndRoleIdList(userQueryDto.getDeptIdList(),
                userQueryDto.getRoleIdList());
        return listR.getData();
    }

    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> loadRoleIdListByUserId(String userId) {
        List<UserRole> userRoleList = userRoleService.lambdaQuery().eq(UserRole::getUserId, userId).list();
        Set<String> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return CollUtil.newArrayList(roleIdSet);
    }

    /**
     * 获取所有的角色
     *
     * @return
     */
    @Override
    public List<RoleDto> loadAllRole() {
        List<Role> roleList = roleService.lambdaQuery().list();


        List<RoleDto> roleDtoList=new ArrayList<>();
        for (Role role : roleList) {
            RoleDto roleDto = RoleDto.builder().id(role.getKey())
                    .name(role.getName())
                    .status(role.getStatus())
                    .build();
            roleDtoList.add(roleDto);

        }

        return roleDtoList;
    }


    /**
     * 获取所有的部门
     *
     * @return
     */
    @Override
    public List<DeptDto> loadAllDept(String parentDeptId) {
        List<Dept> deptList = deptService.lambdaQuery()
                .eq(parentDeptId != null, Dept::getParentId, parentDeptId)
                .list();
        List<DeptDto> deptDtoList = new ArrayList<>();
        for (Dept dept : deptList) {
            DeptDto deptDto = BeanUtil.copyProperties(dept, DeptDto.class);
            deptDto.setLeaderUserIdList(deptLeaderService.queryLeaderIdList(String.valueOf(dept.getId())));
            deptDtoList.add(deptDto);
        }

        return deptDtoList;
    }

    /**
     * 根据部门获取部门下的用户集合
     *
     * @param deptId 部门id
     * @return
     */
    @Override
    public List<UserDto> loadUserByDept(String deptId) {

        MPJLambdaWrapper<User> lambdaQueryWrapper = new MPJLambdaWrapper<User>()
                .selectAll(User.class)

                .leftJoin(DeptUser.class, DeptUser::getUserId, User::getId)

                .eq(DeptUser::getDeptId,deptId);
        List<User> userList = userService.selectJoinList(User.class, lambdaQueryWrapper);



        return BeanUtil.copyToList(userList, UserDto.class);
    }



    /**
     * 根据用户id获取用户
     *
     * @param userId
     * @return
     */
    @Override
    public UserDto getUser(String userId) {
        User user = userService.getById(userId);
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        userDto.setDeptIdList(deptUserService.queryDeptIdList(userId));
        return userDto;
    }



    /**
     * 批量获取部门集合
     *
     * @param deptIdList 部门id集合
     * @return
     */
    @Override
    public List<DeptDto> getDeptList(List<String> deptIdList) {


        List<Dept> deptList = deptService.lambdaQuery().in(Dept::getId,deptIdList).list();

        List<DeptDto> deptDtoList = BeanUtil.copyToList(deptList, DeptDto.class);

        List<DeptLeader> deptLeaderList = deptLeaderService.lambdaQuery().in(DeptLeader::getDeptId, deptIdList).list();
        for (DeptDto deptDto : deptDtoList) {
            List<String> userIdList = deptLeaderList.stream().filter(w -> StrUtil.equals(w.getDeptId(), deptDto.getId()))
                    .map(DeptLeader::getUserId).collect(Collectors.toList());
            deptDto.setLeaderUserIdList(userIdList);
        }


        return deptDtoList;
    }

    @Override
    public List<UserDto> searchUser(String name) {
        List<User> userList = userService.lambdaQuery().and(k ->
                k.like(User::getPinyin, name)
                        .or(w -> w.like(User::getPy, name))
                        .or(w -> w.like(User::getName, name))
        ).list();

        return BeanUtil.copyToList(userList, UserDto.class);
    }

    /**
     * 根据token获取用户id
     * 处理登录接口请求的
     *
     * @param token
     * @return
     */
    @Override
    public String getUserIdByToken(String token) {
        Object loginIdByToken = StpUtil.getLoginIdByToken(token);
        return loginIdByToken == null ? null : loginIdByToken.toString();
    }



    /**
     * 重新拉取数据
     */
    @Override
    public void loadRemoteData() {

    }

    /**
     * 获取登录地址
     *
     * @return
     */
    @Override
    public LoginUrlDto getLoginUrl() {
        return LoginUrlDto.builder().innerUrl(true).url("/aplogin").captcha(captcha).build();
    }

    /**
     * 获取登录参数
     *
     * @return
     */
    @Override
    public Object getLoginParam(Map<String, Object> paramMap) {
        return new HashMap<>();
    }

    /**
     * 发送消息
     *
     * @param messageDto
     */
    @Override
    public void sendMsg(MessageDto messageDto) {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet("local");
    }
}
