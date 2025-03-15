package com.sentury.approvalflow.common.dto.flow.node;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sentury.approvalflow.common.dto.flow.Node;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhj
 * @version 1.0
 * @description: TODO
 * @date 2024/4/3 16:56
 */
@Schema(description = ": TODO")
@JsonTypeName("7")
@Data
public class DelayNode extends Node {




    @Schema(hidden = true)
    private Object value;
    /**
     * true:延迟时间  false：表示具体时间
     */

    @Schema(description = "true:延迟时间  false：表示具体时间")
    private Boolean mode;
    /**
     * 延迟单位
     */

    @Schema(description = "延迟单位")
    private String delayUnit;
}
