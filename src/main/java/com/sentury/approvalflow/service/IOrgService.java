package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.domain.entity.Dept;
import com.sentury.approvalflow.domain.entity.User;
import com.sentury.approvalflow.domain.vo.OrgDataVo;
import com.sentury.approvalflow.domain.vo.OrgSelectShowVo;

import java.util.List;


public interface IOrgService {




    /**
     * 查询组织架构树
     *
     * @param deptId    部门id
     * @param type      只查询部门架构
     * @param showLeave 是否显示离职员工
     * @return 组织架构树数据
     */
    R2<OrgSelectShowVo> getOrgTreeData(String deptId, String type, Boolean showLeave);


    /**
     * 查询所有的组织架构 并树形显示
     *
     * @return
     */
    R2 getOrgTreeDataAll(String keywords, Integer status);

    /**
     * 模糊搜索用户
     *
     * @param userName 用户名/拼音/首字母
     * @return 匹配到的用户
     */
    R2<List<OrgDataVo>> getOrgTreeUser(String userName);


    /**
     * 删除部门
     *
     * @param dept
     * @return
     */
    R2 delete(Dept dept);
    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    R2 getUserDetail(String userId);


    /**
     * 用户离职
     * @param user
     * @return
     */
    R2 delete(User user);

}
