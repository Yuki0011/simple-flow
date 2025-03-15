package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.IndexPageStatistics;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.domain.vo.*;

public interface IBaseService {


    /**
     * 修改前端版本号
     * @param webVersionVO
     * @return
     */
    R2 setWebVersion(WebVersionVO webVersionVO);

    /**
     * 获取当前系统前端版本号
     * @return
     */
    R2 getWebVersion();

    /**
     * 首页数据
     *
     * @return
     */
    R2<IndexPageStatistics> index();

    /**
     * 获取所有地区数据
     *
     * @return
     */
    R2 areaList();

    /**
     * 同步数据
     *
     * @return
     */
    R2 loadRemoteData();

    /**
     * 格式化流程显示
     *
     * @param nodeFormatParamVo
     * @return
     */
    R2<NodeFormatResultVo> formatStartNodeShow(NodeFormatParamVo nodeFormatParamVo);

    /**
     * 查询头部显示数据
     *
     * @param nodeFormatParamVo
     * @return
     */
    R2<TaskHeaderShowResultVO> queryHeaderShow(QueryFormListParamVo nodeFormatParamVo);

    /**
     * 获取任务操作列表
     *
     * @param taskId
     * @return
     */
    R2<TaskOperDataResultVO> queryTaskOperData(String taskId);

}
