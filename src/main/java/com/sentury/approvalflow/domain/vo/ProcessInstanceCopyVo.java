package com.sentury.approvalflow.domain.vo;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.sentury.approvalflow.common.dto.TitleDto;
import com.sentury.approvalflow.domain.entity.ProcessInstanceCopy;
import lombok.Data;

import java.util.List;

@Data
public class ProcessInstanceCopyVo extends ProcessInstanceCopy {

    private String startUserName;
    /**
     * 发起人头像
     */
    private String startUserAvatarUrl;
    /**
     *  表单值显示
     */
    private List<Dict> formValueShowList;

    private Integer processInstanceResult;
    private Integer processInstanceStatus;

    /**
     * 流程实例业务编码
     */
    private String processInstanceBizCode;
    /**
     * 正在处理的人员显示
     */

    private String taskAssignShow;
    /**
     * 发起时间显示
     */

    private String startTimeShow;

    private String rootUserCode;

    private List<TitleDto> title;

}
