package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.ProcessInstanceUserCopy;
import com.sentury.approvalflow.domain.vo.ProcessDataQueryVO;

public interface IProcessInstanceUserCopyService extends IService<ProcessInstanceUserCopy> {


    /**
     * 查询抄送给我的(根据实例id去重)
     * @param pageDto
     * @return
     */
    R2 queryMineCCProcessInstance(ProcessDataQueryVO pageDto);

}
