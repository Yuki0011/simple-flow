package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.domain.entity.Dept;
import lombok.Data;

import java.util.List;

/**
 * 部门vo
 */

@Data
public class DeptVO extends Dept {
    /**
     * 部门主管
     */

    private List<NodeUser> leaderUser;

}
