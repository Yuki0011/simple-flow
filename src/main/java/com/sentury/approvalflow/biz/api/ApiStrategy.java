package com.sentury.approvalflow.biz.api;

import com.sentury.approvalflow.common.dto.LoginUrlDto;
import com.sentury.approvalflow.common.dto.third.*;

import java.util.List;
import java.util.Map;

/**
 * API接口
 * 用来扩展数据：对接已存在的组织架构、角色等数据
 */
public interface ApiStrategy {

    /**
     * 策略注册方法
     *
     * @param key
     */
    default void afterPropertiesSet(String key) {
        ApiStrategyFactory.register(key, this);
    }

    /**
     * 账号密码登录
     * @param password 密码
     * @param account 账号
     * @return 用户id
     */
    String loginWeb(String password,String account);

    /**
     * 根据部门id集合和角色id集合查询人员id集合
     * @param userQueryDto 查询参数
     * @return
     */
    List<String> queryUserIdListByRoleIdListAndDeptIdList(UserQueryDto userQueryDto);
    /**
     * 查询用户列表
     * @param userQueryDto
     * @return
     */
    PageResultDto<UserDto> queryUserList(UserQueryDto userQueryDto);


    /**
     * 根据用户id查询角色id集合
     *
     * @param userId
     * @return
     */
    List<String> loadRoleIdListByUserId(String userId);


    /**
     * 获取所有的角色
     *
     * @return
     */
    List<RoleDto> loadAllRole();



    /**
     * 根据父级id获取所有的部门
     * 若父级id为空 则获取所有的部门
     *
     * @param parentDeptId 父级id
     * @return
     */
    List<DeptDto> loadAllDept(String parentDeptId);

    /**
     * 根据部门获取部门下的用户集合
     *
     * @param deptId 部门id
     * @return
     */
    List<UserDto> loadUserByDept(String deptId);

    /**
     * 根据用户id获取用户
     *
     * @param userId
     * @return
     */
    UserDto getUser(String userId);

    /**
     * 批量获取部门集合
     * @param deptIdList 部门id集合
     * @return
     */
    List<DeptDto> getDeptList(List<String> deptIdList);



    /**
     * 根据名字搜索用户
     *
     * @param name
     * @return
     */
    List<UserDto> searchUser(String name);

    /**
     * 根据token获取用户id
     * 处理登录接口请求的
     *
     * @param token
     * @return
     */
    String getUserIdByToken(String token);







    /**
     * 重新拉取数据
     * 现在用来钉钉同步数据使用 其他业务视情况决定是否启用
     */
    void loadRemoteData();

    /**
     * 获取登录地址
     *
     * @return
     */
    LoginUrlDto getLoginUrl();

    /**
     * 获取登录参数
     * 钉钉扫码登录使用
     * 其他业务视情况决定是否启用
     *
     * @return
     */
    Object getLoginParam(Map<String, Object> paramMap);

    /**
     * 发送消息
     * 业务行对接消息
     *
     * @param messageDto
     */
    void sendMsg(MessageDto messageDto);
}
