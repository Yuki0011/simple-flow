package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.third.DeptDto;
import cn.hutool.core.bean.BeanUtil;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.sentury.approvalflow.domain.entity.Dept;
import com.sentury.approvalflow.domain.entity.DeptUser;
import com.sentury.approvalflow.mapper.DeptUserMapper;
import com.sentury.approvalflow.service.IDeptUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门主管
 */
@Service
@Slf4j
public class DeptUserServiceImpl extends MPJBaseServiceImpl<DeptUserMapper, DeptUser> implements IDeptUserService {
    /**
     * 查询所有的用户id
     *
     * @param deptId
     * @return
     */
    @Override
    public List<String> queryUserIdList(String deptId) {
        return this.lambdaQuery().eq(DeptUser::getDeptId,deptId).list().stream().map(DeptUser::getUserId).collect(Collectors.toList());
    }

    /**
     * 查询人员所属的部门jid
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> queryDeptIdList(String userId) {
        return this.lambdaQuery().eq(DeptUser::getUserId,userId).list().stream().map(DeptUser::getDeptId).collect(Collectors.toList());

    }

    /**
     * 查询部门集合
     *
     * @param userId
     * @return
     */
    @Override
    public List<DeptDto> queryDeptList(String userId) {

        MPJLambdaWrapper<DeptUser> lambdaQueryWrapper = new MPJLambdaWrapper<DeptUser>()
                .selectAll(Dept.class)
                .leftJoin(Dept.class, Dept::getId, DeptUser::getDeptId)
                .eq(DeptUser::getUserId,userId);
        List<Dept> deptList = this.selectJoinList(Dept.class, lambdaQueryWrapper);
        List<DeptDto> deptDtoList = BeanUtil.copyToList(deptList, DeptDto.class);

        return deptDtoList;
    }
}
