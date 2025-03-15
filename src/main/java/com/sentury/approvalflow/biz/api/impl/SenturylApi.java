package com.sentury.approvalflow.biz.api.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sentury.approvalflow.biz.api.ApiStrategy;
import com.sentury.approvalflow.common.constants.StatusEnum;
import com.sentury.approvalflow.common.dto.LoginUrlDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.*;
import com.sentury.approvalflow.domain.entity.Role;
import com.sentury.approvalflow.domain.entity.User;
import com.sentury.approvalflow.domain.entity.UserRole;
import com.sentury.approvalflow.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.system.api.RemoteDeptService;
import org.dromara.system.api.RemoteRoleService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteSysDeptVo;
import org.dromara.system.api.domain.vo.RemoteSysRoleVo;
import org.dromara.system.api.domain.vo.RemoteUserVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SenturylApi implements ApiStrategy, InitializingBean {

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

    @DubboReference
    private final RemoteDeptService remoteDeptService;
    @DubboReference
    private final RemoteUserService remoteUserService;
    @DubboReference
    private final RemoteRoleService remoteRoleService;


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

        User u = userService.lambdaQuery().eq(User::getPhone, account).eq(User::getPassword, pwd).eq(User::getStatus, StatusEnum.ENABLE.getValue())

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
          // R2<List<String>> listR = combinationGroupService.queryUserListByDeptIdListAndRoleIdList(userQueryDto.getDeptIdList(),
          //      userQueryDto.getRoleIdList());
       // List<Long> deptIds = userQueryDto.getDeptIdList().stream().map(e->Long.valueOf(e)).toList();

        List<Long> userIds = remoteRoleService.listUserIdsByRoleKey(null, userQueryDto.getRoleIdList());
        return userIds.stream().map(userId -> String.valueOf(userId)).toList();
    }

    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> loadRoleIdListByUserId(String userId) {
        //List<UserRole> userRoleList = userRoleService.lambdaQuery().eq(UserRole::getUserId, userId).list();
        return remoteRoleService.listRoleKeysByUserId(Long.valueOf(userId));
    }

    /**
     * 获取所有的角色
     *
     * @return
     */
    @Override
    public List<RoleDto> loadAllRole() {
        //List<Role> roleList = roleService.lambdaQuery().list();
        List<RemoteSysRoleVo> remoteSysRoleVos = remoteRoleService.listRoles();
        List<Role> roleList = new ArrayList<>();
        for (RemoteSysRoleVo remoteSysRoleVo : remoteSysRoleVos) {
            Role role = new Role();
            role.setName(remoteSysRoleVo.getRoleName());
            role.setKey(remoteSysRoleVo.getRoleKey());
            role.setStatus(1);
            role.setDelFlag(false);
            roleList.add(role);
        }

        List<RoleDto> roleDtoList = new ArrayList<>();
        for (Role role : roleList) {
            RoleDto roleDto = RoleDto.builder().id(role.getKey()).name(role.getName()).status(role.getStatus()).build();
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
        List<RemoteSysDeptVo> remoteSysDeptVos = remoteDeptService.listDownDeptId(parentDeptId == null ? null : Long.valueOf(parentDeptId));
        List<DeptDto> deptDtoList = new ArrayList<>();
        for (RemoteSysDeptVo remoteSysDeptVo : remoteSysDeptVos) {
            DeptDto  dept = new DeptDto();
            dept.setId(remoteSysDeptVo.getDeptId()+"");
            dept.setName(remoteSysDeptVo.getDeptName());
            dept.setParentId(remoteSysDeptVo.getParentId()+"");
            dept.setStatus(1);
            if(remoteSysDeptVo.getLeaderId()!=null) {
                dept.setLeaderUserIdList(List.of(remoteSysDeptVo.getLeaderId()+""));
            }
            deptDtoList.add(dept);
        }
        //List<DeptDto> deptDtoList = new ArrayList<>();
        //List<RemoteUserVo> remoteUserVos = remoteUserService.selectUsersByDept(deptList.stream().map(Dept::getId).toList());
        //Map<Long, List<Long>> deptUsers = remoteUserVos.stream().collect(Collectors.groupingBy(RemoteUserVo::getDeptId, Collectors.mapping(RemoteUserVo::getUserId, Collectors.toList())));
//        for (Dept dept : deptList) {
//            DeptDto deptDto = BeanUtil.copyProperties(dept, DeptDto.class);
//            //List<Long> ids = deptUsers.get(dept.getId());
//            if (CollectionUtils.isNotEmpty(ids)) {
//              deptDto.setLeaderUserIdList(ids.stream().map(e -> e + "").toList());
//            }
//            deptDtoList.add(deptDto);
//        }
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

//        MPJLambdaWrapper<User> lambdaQueryWrapper = new MPJLambdaWrapper<User>()
//                .selectAll(User.class)
//
//                .leftJoin(DeptUser.class, DeptUser::getUserId, User::getId)
//
//                .eq(DeptUser::getDeptId,deptId);
//        List<User> userList = userService.selectJoinList(User.class, lambdaQueryWrapper);


        List<RemoteUserVo> remoteUserVos = remoteUserService.selectUsersByDept(List.of(Long.valueOf(deptId)));
        List<UserDto> results = new ArrayList<>();
        for (RemoteUserVo remoteUserVo : remoteUserVos) {
            UserDto userDto = new UserDto();
            userDto.setId(remoteUserVo.getUserId() + "");
            userDto.setName(remoteUserVo.getUserName());
            userDto.setStatus(1);
            userDto.setDeptName(remoteUserVo.getDeptName());
            userDto.setPosition(remoteUserVo.getPosition());
            userDto.setSequence(remoteUserVo.getSequence());
            userDto.setPostRank(remoteUserVo.getPostRank());
            userDto.setOnBoardingDate(remoteUserVo.getOnBoardingDate());
            userDto.setTrialDateStart(remoteUserVo.getTrialDateStart());
            userDto.setTrialDateEnd(remoteUserVo.getTrialDateEnd());
            userDto.setOnBoardingDate(remoteUserVo.getOnBoardingDate());
            userDto.setRegularizationDate(remoteUserVo.getRegularizationDate());
            userDto.setFlowNames(remoteUserVo.getFlowNames());
            userDto.setLeaderCode(remoteUserVo.getLeaderCode());
            results.add(userDto);
        }
        return results;
    }


    /**
     * 根据用户id获取用户
     *
     * @param userId
     * @return
     */
    @Override
    public UserDto getUser(String userId) {
        RemoteUserVo user = remoteUserService.selectUserById(Long.valueOf(userId));
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        //userDto.setDeptIdList(deptUserService.queryDeptIdList(userId));
        userDto.setName(user.getUserName());
        userDto.setId(user.getUserId() + "");
        // todo 往下查
        userDto.setDeptIdList(List.of(user.getDeptId() + ""));
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
        //List<Dept> deptList = deptService.lambdaQuery().in(Dept::getId, deptIdList).list();
        List<Long> deptIds = deptIdList.stream().map(Long::valueOf).toList();
        List<RemoteSysDeptVo> remoteSysDeptVos = remoteDeptService.listDownDeptIds(deptIds);
        List<DeptDto> deptDtoList = new ArrayList<>();
        for (RemoteSysDeptVo remoteSysDeptVo : remoteSysDeptVos) {
            DeptDto dept = new DeptDto();
            dept.setId(remoteSysDeptVo.getDeptId() + "");
            dept.setName(remoteSysDeptVo.getDeptName());
            dept.setParentId(remoteSysDeptVo.getParentId() + "");
            dept.setStatus(1);
            deptDtoList.add(dept);
        }
        //    List<DeptLeader> deptLeaderList = deptLeaderService.lambdaQuery().in(DeptLeader::getDeptId, deptIdList).list();
        List<RemoteUserVo> remoteUserVos = remoteUserService.selectLeaderByDept(deptIds);
        for (DeptDto deptDto : deptDtoList) {
            List<String> userIdList = remoteUserVos.stream().filter(w -> StrUtil.equals(w.getDeptId() + "", deptDto.getId())).map(e -> e.getUserId() + "").collect(Collectors.toList());
            deptDto.setLeaderUserIdList(userIdList);
        }
        return deptDtoList;
    }

    @Override
    public List<UserDto> searchUser(String name) {
//        List<User> userList = userService.lambdaQuery().and(k ->
//            k.like(User::getPinyin, name)
//                .or(w -> w.like(User::getPy, name))
//                .or(w -> w.like(User::getName, name))
//        ).list();
        if ("S00001".equals(name)) {
            return Collections.emptyList();
        }
        RemoteUserVo remoteUserVo = remoteUserService.getUserByCode(name);
        if (null == remoteUserVo) {
            return Collections.emptyList();
        }
        UserDto userDto = new UserDto();
        userDto.setId(remoteUserVo.getUserId() + "");
        userDto.setName(remoteUserVo.getUserName());
        userDto.setStatus(1);
        userDto.setDeptName(remoteUserVo.getDeptName());
        userDto.setPosition(remoteUserVo.getPosition());
        userDto.setSequence(remoteUserVo.getSequence());
        userDto.setPostRank(remoteUserVo.getPostRank());
        userDto.setOnBoardingDate(remoteUserVo.getOnBoardingDate());
        userDto.setTrialDateStart(remoteUserVo.getTrialDateStart());
        userDto.setTrialDateEnd(remoteUserVo.getTrialDateEnd());
        userDto.setOnBoardingDate(remoteUserVo.getOnBoardingDate());
        userDto.setRegularizationDate(remoteUserVo.getRegularizationDate());
        userDto.setFlowNames(remoteUserVo.getFlowNames());
        return List.of(userDto);
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
        afterPropertiesSet("sentury");
    }
}
