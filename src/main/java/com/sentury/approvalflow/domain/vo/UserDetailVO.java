package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.third.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户对象
 */

@Getter
@Setter
public class UserDetailVO extends UserDto {

    /**
     * 部门名称
     */

    private String deptName;
    /**
     * 直属主管对象集合
     */

    private List<NodeUser> parentShow;
    /**
     * 部门id集合
     */

    private List<String> deptIdList;
    /**
     * 角色集合
     */

    private Set<String> roles;
    /**
     * 权限集合
     */

    private Set<String> perms;
    /**
     * 扩展字段
     */

    private List<UserFieldDataVo> userFieldDataList;
    /**
     * 参数数据
     */

    private Map<String,Object> fieldData;
    /**
     * 验证码
     */

    private String verifyCode;
    /**
     * 验证码key
     */

    private String verifyCodeKey;
    /**
     * 角色id集合
     */

    private List<String> roleIds;


}
