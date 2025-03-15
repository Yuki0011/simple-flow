package com.sentury.approvalflow.biz.api.impl;

import com.sentury.approvalflow.common.dto.LoginUrlDto;
import com.sentury.approvalflow.common.dto.third.*;
import com.sentury.approvalflow.common.utils.JsonUtil;
import com.sentury.approvalflow.biz.api.ApiStrategy;
import com.sentury.approvalflow.biz.utils.CoreHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NetApi implements ApiStrategy, InitializingBean {


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
        return null;
    }

    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> loadRoleIdListByUserId(String userId) {
        return null;
    }

    /**
     * 获取所有的角色
     *
     * @return
     */
    @Override
    public List<RoleDto> loadAllRole() {
        String post = CoreHttpUtil.get( "/test/net/loadUserIdListByRoleIdList");
        return JsonUtil.parseArray(post,RoleDto.class);
    }


    /**
     * 获取所有的部门
     *
     * @return
     */
    @Override
    public List<DeptDto> loadAllDept(String parentDeptId) {
        String post = CoreHttpUtil.get( "/test/net/loadAllDept?parentDeptId="+(parentDeptId==null?"":parentDeptId));
        return JsonUtil.parseArray(post,DeptDto.class);
    }

    /**
     * 根据部门获取部门下的用户集合
     *
     * @param deptId 部门id
     * @return
     */
    @Override
    public List<UserDto> loadUserByDept(String deptId) {
        String post = CoreHttpUtil.get( "/test/net/loadUserByDept?deptId="+deptId);
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
        String post = CoreHttpUtil.get( "/test/net/getUser?userId="+userId);
        return JsonUtil.parseObject(post,UserDto.class);
    }



    /**
     * 批量获取部门集合
     *
     * @param deptIdList 部门id集合
     * @return
     */
    @Override
    public List<DeptDto> getDeptList(List<String> deptIdList) {
        return null;
    }

    @Override
    public List<UserDto> searchUser(String name) {
        String post = CoreHttpUtil.get( "/test/net/searchUser?name="+name);
        return JsonUtil.parseArray(post,UserDto.class);
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
        return null;
    }

    /**
     * 获取登录参数
     *
     * @return
     */
    @Override
    public Object getLoginParam(Map<String, Object> paramMap) {
        return null;
    }

    /**
     * 发送消息
     *
     * @param messageDto
     */
    @Override
    public void sendMsg(MessageDto messageDto) {

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
        String post = CoreHttpUtil.get( "/test/net/getUserIdByToken?token="+token);
        return post;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet("net");
    }
}
