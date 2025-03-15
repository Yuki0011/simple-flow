package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.ProcessNodeDataDto;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.Node;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.ProcessNodeData;

/**
 * <p>
 * 流程节点数据 服务类
 * </p>
 *
 * @author Vincent
 * @since 2023-05-07
 */
public interface IProcessNodeDataService extends IService<ProcessNodeData> {

    /**
     * 保存流程节点数据
     * @param processNodeDataDto
     * @return
     */
    R2 saveNodeData(ProcessNodeDataDto processNodeDataDto);

    /***
     * 获取节点数据
     * @param flowId
     * @param nodeId
     * @return
     */
    R2<String> getNodeData(String flowId, String nodeId);

    R2<Node> getNode(String flowId, String nodeId);

}
