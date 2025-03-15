package com.sentury.approvalflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.DeptLeader;

import java.util.List;

public interface IDeptLeaderService extends IService<DeptLeader> {
    /**
     * 查询所有的主管id
     * @param deptId
     * @return
     */
    List<String> queryLeaderIdList(String deptId);

    /**
     * 查询人员所属的部门jid
     * @param userId
     * @return
     */
    List<String> queryDeptIdList(String userId);

}
