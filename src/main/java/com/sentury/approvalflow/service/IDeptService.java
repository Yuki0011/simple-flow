package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.github.yulichang.base.MPJBaseService;
import com.sentury.approvalflow.domain.entity.Dept;
import com.sentury.approvalflow.domain.vo.DeptVO;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-05
 */
public interface IDeptService extends MPJBaseService<Dept> {

    /**
     * 创建部门
     *
     * @param dept
     * @return
     */
    R2 create(DeptVO deptVO);

    /**
     * 修改部门
     *
     * @param deptVO
     * @return
     */
    R2 updateDept(DeptVO deptVO);




}
