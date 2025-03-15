package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户对象
 */

@Schema(description = "用户对象")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBizVO extends User {

    /**
     * 部门名称
     */

    @Schema(description = "部门名称")
    private String deptName;
    /**
     * 直属主管对象集合
     */

    @Schema(description = "直属主管对象集合")
    private List<NodeUser> parentShow;
    /**
     * 部门id集合
     */

    @Schema(description = "部门id集合")
    private List<String> deptIdList;
    /**
     * 角色集合
     */

    @Schema(description = "角色集合")
    private Set<String> roles;
    /**
     * 权限集合
     */

    @Schema(description = "权限集合")
    private Set<String> perms;
    /**
     * 扩展字段
     */

    @Schema(description = "扩展字段")
    private List<UserFieldDataVo> userFieldDataList;
    /**
     * 参数数据
     */

    @Schema(description = "参数数据")
    private Map<String,Object> fieldData;
    /**
     * 验证码
     */

    @Schema(description = "验证码")
    private String verifyCode;
    /**
     * 验证码key
     */

    @Schema(description = "验证码key")
    private String verifyCodeKey;
    /**
     * 角色id集合
     */

    @Schema(description = "角色id集合")
    private List<String> roleIds;


}
