package com.sentury.approvalflow.service;


import com.sentury.approvalflow.common.dto.PageDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.domain.vo.FormGroupVo;
import com.sentury.approvalflow.domain.vo.ProcessDataQueryVO;

import java.util.List;

/**
 * 聚合接口
 */
public interface ICombinationGroupService {

    /**
     * 根据部门id集合和角色id集合 获取用户列表
     *
     * @param deptIdList 部门id集合
     * @param roleKeyList 角色key集合
     * @return
     */
    R2<List<String>> queryUserListByDeptIdListAndRoleIdList(List<String> deptIdList, List<String> roleKeyList);


    /**
     * 查询表单组包含流程
     *
     * @return 表单组数据
     */
    R2<List<FormGroupVo>> listGroupWithProcess(Boolean hidden);

    /**
     * 查询流程组和主流程
     *
     * @return
     */
    R2<List<FormGroupVo>> listGroupWithProcessMain();

    /**
     * 搜索流程
     * @param word 关键词
     * @return
     */
    R2<List<FormGroupVo.FlowVo>> searchFlowList(String word);

    /**
     * 查询所有我可以发起的表单组
     *
     * @return
     */
    R2<List<FormGroupVo>> listCurrentUserStartGroup();

    /**
     * 删除主流程
     *
     * @param uniqueId
     * @return
     */
    R2 deleteProcessMain(String uniqueId);

    /**
     * 清理所有流程
     *
     * @param uniqueId 流程唯一id
     * @return 成功或者失败
     */
    R2 clear(String uniqueId);

    R2 queryTotalTaskList();

    /**
     * 查询当前登录用户的待办任务
     *
     * @param pageVO
     * @return
     */
    R2 queryTodoTaskList(PageDto pageVO);

    /**
     * 查询已办任务
     *
     * @param pageVO
     * @return
     */
    R2 queryFinishedTaskList(ProcessDataQueryVO pageVO);

    /**
     * 查询我发起的
     *
     * @param pageDto
     * @return
     */
    R2 queryInitiatedTaskList(ProcessDataQueryVO pageDto);

    /**
     * 查询抄送给我的
     *
     * @param pageDto
     * @return
     */
    R2 queryCopiedTaskList(ProcessDataQueryVO pageDto);

}
