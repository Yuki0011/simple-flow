package com.sentury.approvalflow.domain.vo;

import com.sentury.approvalflow.domain.entity.ProcessInstanceNodeRecord;
import lombok.Data;

import java.util.List;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-10-09 11:15
 */
@Data
public class NextNodeQueryVO {

    private List<ProcessInstanceNodeRecord> processInstanceNodeRecordList;
    /**
     * 是否包含网关
     */
    private Boolean containGateway;
    /**
     * 如果包含网关 则第一个网关id
     */
    private String gatewayId;
    /**
     * 网关类型
     */
    private Integer gatewayType;

}
