package com.sentury.approvalflow.common.dto.flow.node.parent;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author zhj
 * @version 1.0
 * @description: TODO
 * @date 2024/4/3 16:56
 */
@Schema(description = ": TODO")
@Data
public class SuperUserNode extends SuperUserRootNode {



    /**
     * 部门主管级别
     */

    @Schema(description = "部门主管级别")
    private Integer deptLeaderLevel;

    /**
     * 表单id
     */

    @Schema(description = "表单id")
    private String formUserId;
    /**
     * 表单名称
     */

    @Schema(description = "表单名称")
    private String formUserName;
    /**
     * 节点用户列表
     */

    @Schema(description = "节点用户列表")
    private List<NodeUser> nodeUserList;

    /**
     * 角色列表
     */

    @Schema(description = "角色列表")
    private List<NodeUser> roleList;
    /**
     * 部门组件时  指定人员是主管还是部门下的员工
     * {@link ProcessInstanceConstant.AssignedTypeFormDeptUserTypeClass}
     */

    @Schema(description = "部门组件时  指定人员是主管还是部门下的员工 {@link ProcessInstanceConstant.AssignedTypeFormDeptUserTypeClass}")
    private String deptUserType;
    /**
     * 是否包含子级部门
     */

    @Schema(description = "是否包含子级部门")
    private Boolean containChildrenDept;


    /**
     * 是否是多选
     */

    @Schema(description = "是否是多选")
    private Boolean multiple;
    /**
     * 人员指定类型
     */

    @Schema(description = "人员指定类型")
    private Integer assignedType;
}
