package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.entity.Dept;
import com.sentury.approvalflow.domain.entity.DeptLeader;
import com.sentury.approvalflow.mapper.DeptMapper;
import com.sentury.approvalflow.service.IDeptLeaderService;
import com.sentury.approvalflow.service.IDeptService;
import com.sentury.approvalflow.biz.utils.DataUtil;
import com.sentury.approvalflow.domain.vo.DeptVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-05
 */
@Service
public class DeptServiceImpl extends MPJBaseServiceImpl<DeptMapper, Dept> implements IDeptService {

    @Resource
    private IDeptLeaderService deptLeaderService;

    /**
     * 创建部门
     *
     * @param deptVO
     * @return
     */
    @Transactional
    @Override
    public R2 create(DeptVO deptVO) {

        this.save(deptVO);
        for (NodeUser nodeUser : deptVO.getLeaderUser()) {
            DeptLeader deptLeader = new DeptLeader();
            deptLeader.setDeptId(String.valueOf(deptVO.getId()));
            deptLeader.setUserId(nodeUser.getId());
            deptLeaderService.save(deptLeader);

        }
        return R2.success();
    }

    @Transactional
    @Override
    public R2 updateDept(DeptVO deptVO) {
        List<DeptDto> allDept = ApiStrategyFactory.getStrategy().loadAllDept(null);

        List<DeptDto> deptList = DataUtil.selectChildrenByDept(String.valueOf(deptVO.getId()), allDept);


        boolean b = deptList.stream().anyMatch(w -> w.getId().equals(deptVO.getParentId()));
        if(b){
            return R2.fail("当前部门的父级部门不能是当前部门或者当前部门的子级部门");
        }

        this.updateById(deptVO);

        deptLeaderService.lambdaUpdate().eq(DeptLeader::getDeptId,deptVO.getId()).remove();
        for (NodeUser nodeUser : deptVO.getLeaderUser()) {
            DeptLeader deptLeader = new DeptLeader();
            deptLeader.setDeptId(String.valueOf(deptVO.getId()));
            deptLeader.setUserId(nodeUser.getId());
            deptLeaderService.save(deptLeader);

        }

        return R2.success("修改成功");
    }

}
