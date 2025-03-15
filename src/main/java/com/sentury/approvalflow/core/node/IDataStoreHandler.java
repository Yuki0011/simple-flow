package com.sentury.approvalflow.core.node;

import com.sentury.approvalflow.common.dto.flow.Node;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.log.StaticLog;

/**
 * 数据处理接口
 */
public interface IDataStoreHandler {
    /**
     * 节点数据存储
     *
     * @param flowId   流程id
     * @param nodeId   节点id
     * @param data     数据
     * @param tenantId
     */
    void saveAll(String flowId, String nodeId, Object data,String tenantId);

    /**
     * 节点数据存储
     * @param flowId
     * @param nodeId
     * @param data
     */
    void save(String flowId,String nodeId,Node data);
    /**
     * 获取节点数据
     *
     * @param flowId   流程id
     * @param nodeId   节点id
     * @param tenantId
     * @return
     */
    String get(String flowId,String nodeId,String tenantId);




    default Node getNode(String flowId, String nodeId,String tenantId){
        try {
            String text = this.get(flowId, nodeId, tenantId);
            StaticLog.info("获取节点数据：{}",text);
            return JsonUtil.parseObject(text,Node.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
