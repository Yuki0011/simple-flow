package com.sentury.approvalflow.domain.vo;

import cn.hutool.core.lang.Dict;
import com.sentury.approvalflow.common.dto.TitleDto;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-07-28 15:23
 */

@Schema(description = "")
@Data
public class ProcessInstanceRecordVO extends ProcessInstanceRecord {

    /**
     *  表单值显示
     */

    @Schema(description = "表单值显示")
    private List<Dict> formValueShowList;

    /**
     * 发起人名字
     */

    @Schema(description = "发起人名字")
    private String rootUserName;
    /**
     * 发起人头像
     */

    @Schema(description = "发起人头像")
    private String rootUserAvatarUrl;
    /**
     * 是否允许撤销
     */

    @Schema(description = "是否允许撤销")
    private Boolean cancelEnable;
    /**
     * 正在处理的人员显示
     */

    @Schema(description = "正在处理的人员显示")
    private String taskAssignShow;
    /**
     * 发起时间显示
     */

    @Schema(description = "发起时间显示")
    private String startTimeShow;
    /**
     * 流程唯一id
     */

    @Schema(description = "流程唯一id")
    private String flowUniqueId;

    private List<TitleDto> title;
}
