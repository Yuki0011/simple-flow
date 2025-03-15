package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.Process;
import com.sentury.approvalflow.domain.vo.ProcessVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-25
 */
public interface IProcessService extends IService<Process> {


    /**
     * 获取详细数据
     *
     * @param flowId
     * @return
     */
    R2<ProcessVO> getDetail(String flowId);

    /**
     * 根据流程唯一标识查询流程列表
     * @param uniqueId
     * @return
     */
    R2<ProcessVO> getListByUniqueId(String uniqueId);

    /**
     * 设置主流程
     * @param flowId
     * @return
     */
    R2 setMainProcess(String flowId);

    Process getByFlowId(String flowId);

    /**
     * 查询流程数据 包括已经被删除的
     * 慎用
     * @param flowId
     * @return
     */
    Process getByFlowIdContainDeleted(String flowId);
    Process getByUniqueId(String uniqueId);

    void updateByFlowId(Process process);

    void stop(String flowId);

    /**
     * 创建流程
     *
     * @param processVO
     * @return
     */
    R2 create(ProcessVO processVO);




    /**
     * 编辑表单
     *
     * @param flowId 摸板ID
     * @param type       类型 stop using delete
     * @return 操作结果
     */
    R2 update(String flowId, String type, Long groupId);




    /**
     * 查询所有关联的流程id
     * @param flowIdList
     * @return
     */
    R2<List<String>> getAllRelatedFlowId(List<String> flowIdList);

}
