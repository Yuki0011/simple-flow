package com.sentury.approvalflow.biz.api.impl;

import com.sentury.approvalflow.common.dto.LoginUrlDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.*;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.biz.api.ApiStrategy;
import com.sentury.approvalflow.biz.utils.WxCpHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业微信对接api
 */
@Service
@Slf4j
public class WxCpApi implements ApiStrategy, InitializingBean {


    /**
     * 账号密码登录
     *
     * @param password 密码
     * @param account  账号
     * @return 用户id
     */
    @Override
    public String loginWeb(String password, String account) {
        return null;
    }

    /**
     * 根据部门id集合和角色id集合查询人员id集合
     *
     * @param userQueryDto 查询参数
     * @return
     */
    @Override
    public List<String> queryUserIdListByRoleIdListAndDeptIdList(UserQueryDto userQueryDto) {
        String post = WxCpHttpUtil.post(userQueryDto, "/user/loadUserIdListByRoleIdListAndDeptIdList");
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<List<String>>>() {
        }).getData();
    }
    /**
     * 查询用户列表
     *
     * @param userQueryDto
     * @return
     */
    @Override
    public PageResultDto<UserDto> queryUserList(UserQueryDto userQueryDto) {
        String post = WxCpHttpUtil.post(userQueryDto, "/user/queryUserList");
        PageResultDto<UserDto> data = JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<PageResultDto<UserDto>>>() {
        }).getData();
        return data;
    }

    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> loadRoleIdListByUserId(String userId) {
        String post = WxCpHttpUtil.get( "/user/loadRoleIdListByUserId?userId="+userId);
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<List<String>>>() {
        }).getData();
    }

    /**
     * 获取所有的角色
     *
     * @return
     */
    @Override
    public List<RoleDto> loadAllRole() {
        String post = WxCpHttpUtil.get("/role/all");
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<List<RoleDto>>>() {
        }).getData();
    }

    /**
     * 获取所有的部门
     *
     * @return
     */
    @Override
    public List<DeptDto> loadAllDept(String parentDeptId) {
        String post = WxCpHttpUtil.get("/dept/all?deptId=" + (parentDeptId == null ? "" : parentDeptId));
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<List<DeptDto>>>() {
        }).getData();
    }

    /**
     * 根据部门获取部门下的用户集合
     *
     * @param deptId 部门id
     * @return
     */
    @Override
    public List<UserDto> loadUserByDept(String deptId) {
        String post = WxCpHttpUtil.get("/user/loadUserByDept?deptId=" + deptId);
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<List<UserDto>>>() {
        }).getData();
    }



    /**
     * 批量获取部门集合
     *
     * @param deptIdList 部门id集合
     * @return
     */
    @Override
    public List<DeptDto> getDeptList(List<String> deptIdList) {
        String post = WxCpHttpUtil.post(deptIdList,"/dept/batchGet" );
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<List<DeptDto>>>() {
        }).getData();
    }

    /**
     * 根据用户id获取用户
     *
     * @param userId
     * @return
     */
    @Override
    public UserDto getUser(String userId) {
        String post = WxCpHttpUtil.get("/user/one?userId=" + userId);
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<UserDto>>() {
        }).getData();
    }



    @Override
    public List<UserDto> searchUser(String name) {
        String post = WxCpHttpUtil.get("/user/searchUser?name=" + name);
        return JsonUtil.parseObject(post, new JsonUtil.TypeReference<R2<List<UserDto>>>() {
        }).getData();
    }


    /**
     * 重新拉取数据
     */
    @Override
    public void loadRemoteData() {
        WxCpHttpUtil.post(new HashMap<>(), "/base/loadRemoteData");
    }

    /**
     * 获取登录地址
     *
     * @return
     */
    @Override
    public LoginUrlDto getLoginUrl() {
        String s = WxCpHttpUtil.get("/base/getLoginUrl");
        R2<LoginUrlDto> r = JsonUtil.parseObject(s, new JsonUtil.TypeReference<R2<LoginUrlDto>>() {
        });
        return r.getData();

    }

    /**
     * 获取登录参数
     *
     * @return
     */
    @Override
    public Object getLoginParam(Map<String, Object> paramMap) {
        String s = WxCpHttpUtil.post(paramMap,"/base/getLoginParam");
        R2 r = JsonUtil.parseObject(s, new JsonUtil.TypeReference<R2>() {
        });
        return r.getData();
    }

    /**
     * 发送消息
     *
     * @param messageDto
     */
    @Override
    public void sendMsg(MessageDto messageDto) {

        WxCpHttpUtil.post(messageDto,"/message/send");
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

        String s = WxCpHttpUtil.get("/user/getUserIdByCode?authCode=" + token);
        R2<String> r = JsonUtil.parseObject(s, new JsonUtil.TypeReference<R2<String>>() {
        });

        return r.getData();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet("wxcp");
    }
}
