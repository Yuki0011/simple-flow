package com.sentury.approvalflow.biz.api.impl;

import com.sentury.approvalflow.common.dto.LoginUrlDto;
import com.sentury.approvalflow.common.dto.third.*;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.lang.Dict;
import com.sentury.approvalflow.biz.api.ApiStrategy;
import com.sentury.approvalflow.biz.utils.ApiHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据配置的地址对接接口实现
 */
@Service
@Slf4j
public class HttpApi implements ApiStrategy, InitializingBean {
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
        String post = ApiHttpUtil.post(Dict.create().set("account", account).set("password", password), "login");
        return post;
    }

    /**
     * 根据部门id集合和角色id集合查询人员id集合
     *
     * @param userQueryDto 查询参数
     * @return
     */
    @Override
    public List<String> queryUserIdListByRoleIdListAndDeptIdList(UserQueryDto userQueryDto) {
        String post = ApiHttpUtil.post(userQueryDto, "userIdListByRoleIdListAndDeptIdList");
        return JsonUtil.parseArray(post, String.class);
    }

    /**
     * 查询用户列表
     *
     * @param userQueryDto
     * @return
     */
    @Override
    public PageResultDto<UserDto> queryUserList(UserQueryDto userQueryDto) {
        String post = ApiHttpUtil.post(userQueryDto, "userList");
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<PageResultDto<UserDto>>() {
        });
    }

    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> loadRoleIdListByUserId(String userId) {
        String post = ApiHttpUtil.post(Dict.create().set("userId", userId), "roleIdListByUserId");
        return JsonUtil.parseArray(post, String.class);
    }

    /**
     * 获取所有的角色
     *
     * @return
     */
    @Override
    public List<RoleDto> loadAllRole() {
        String post = ApiHttpUtil.post(Dict.create(), "roleAll");
        return JsonUtil.parseArray(post, RoleDto.class);
    }


    /**
     * 获取所有的部门
     *
     * @return
     */
    @Override
    public List<DeptDto> loadAllDept(String parentDeptId) {
        String post = ApiHttpUtil.post(Dict.create().set("parentDeptId", parentDeptId), "deptListByParentDeptId");
        return JsonUtil.parseArray(post, DeptDto.class);
    }

    /**
     * 根据部门获取部门下的用户集合
     *
     * @param deptId 部门id
     * @return
     */
    @Override
    public List<UserDto> loadUserByDept(String deptId) {
        String post = ApiHttpUtil.post(Dict.create().set("deptId", deptId), "userListByDeptId");
        return JsonUtil.parseArray(post, UserDto.class);
    }

    /**
     * 根据用户id获取用户
     *
     * @param userId
     * @return
     */
    @Override
    public UserDto getUser(String userId) {
        String post = ApiHttpUtil.post(Dict.create().set("userId", userId), "userById");
        return JsonUtil.parseObject(post, UserDto.class);
    }



    /**
     * 批量获取部门集合
     *
     * @param deptIdList 部门id集合
     * @return
     */
    @Override
    public List<DeptDto> getDeptList(List<String> deptIdList) {
        String post = ApiHttpUtil.post(Dict.create().set("deptIdList", deptIdList), "batchGetDept");
        return JsonUtil.parseArray(post, DeptDto.class);
    }

    @Override
    public List<UserDto> searchUser(String name) {
        String post = ApiHttpUtil.post(Dict.create().set("name", name), "userByName");
        return JsonUtil.parseArray(post, UserDto.class);
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
        LoginUrlDto loginUrlDto = new LoginUrlDto();
        loginUrlDto.setInnerUrl(true);
        loginUrlDto.setUrl("/aplogin");
        loginUrlDto.setCaptcha(captcha);
        return loginUrlDto;
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
         ApiHttpUtil.post(messageDto, "messageNotify");

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
        String post = ApiHttpUtil.post(Dict.create().set("token", token), "userIdByToken");
        return post;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet("http");
    }
}
