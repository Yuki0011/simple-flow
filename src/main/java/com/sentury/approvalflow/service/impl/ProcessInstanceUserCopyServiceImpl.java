package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.UserDto;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.biz.api.ApiStrategyFactory;
import com.sentury.approvalflow.domain.entity.ProcessInstanceRecord;
import com.sentury.approvalflow.domain.entity.ProcessInstanceUserCopy;
import com.sentury.approvalflow.mapper.ProcessInstanceUserCopyMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IProcessInstanceRecordService;
import com.sentury.approvalflow.service.IProcessInstanceUserCopyService;
import com.sentury.approvalflow.service.IProcessService;
import com.sentury.approvalflow.domain.vo.ProcessDataQueryVO;
import com.sentury.approvalflow.domain.vo.ProcessInstanceCopyVo;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProcessInstanceUserCopyServiceImpl extends ServiceImpl<ProcessInstanceUserCopyMapper,
        ProcessInstanceUserCopy> implements IProcessInstanceUserCopyService, IClearService {

    @Resource
    private IProcessInstanceRecordService processInstanceRecordService;
    @Resource
    private IProcessService processService;
    /**
     * 查询抄送给我的(根据实例id去重)
     *
     * @param pageDto
     * @return
     */
    @Override
    public R2 queryMineCCProcessInstance(ProcessDataQueryVO pageDto) {

        List<String> flowIdList = pageDto.getFlowIdList();
        if(CollUtil.isNotEmpty(flowIdList)){
            flowIdList = processService.getAllRelatedFlowId(flowIdList).getData();
        }


        String userId = LoginHelper.getUserId()+"";

        Page<ProcessInstanceUserCopy> page = this.lambdaQuery()
                .eq(ProcessInstanceUserCopy::getUserId, userId)
                .in(CollUtil.isNotEmpty(flowIdList), ProcessInstanceUserCopy::getFlowId,
                        flowIdList)
                .orderByDesc(ProcessInstanceUserCopy::getCreateTime)
                .page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()));

        List<ProcessInstanceUserCopy> records = page.getRecords();

        List<ProcessInstanceCopyVo> processCopyVoList = BeanUtil.copyToList(records, ProcessInstanceCopyVo.class);

        if (CollUtil.isNotEmpty(records)) {

            //发起人
            Set<String> startUserIdSet = records.stream().map(w -> w.getStartUserId()).collect(Collectors.toSet());

            List<UserDto> startUserList = new ArrayList<>();
            for (String s : startUserIdSet) {
                UserDto user = ApiStrategyFactory.getStrategy().getUser(s);
                startUserList.add(user);
            }
            Set<String> processInstanceIdSet = records.stream().map(w -> w.getProcessInstanceId()).collect(Collectors.toSet());

            //流程实例记录
            List<ProcessInstanceRecord> processInstanceRecordList = processInstanceRecordService.lambdaQuery().in(ProcessInstanceRecord::getProcessInstanceId,
                    processInstanceIdSet).list();

            for (ProcessInstanceCopyVo record : processCopyVoList) {

                ProcessInstanceRecord processInstanceRecord = processInstanceRecordList.stream().filter(w -> StrUtil.equals(w.getProcessInstanceId(),
                        record.getProcessInstanceId())).findFirst().get();


                UserDto startUser = startUserList.stream().filter(w -> w.getId()
                        .equals(record.getStartUserId())).findAny().orElse(null);
                record.setStartUserName(startUser.getName());


                record.setStartTime(processInstanceRecord.getCreateTime());

                record.setProcessInstanceStatus(processInstanceRecord.getStatus());
                record.setProcessInstanceResult(processInstanceRecord.getResult());

            }
        }

        Page p = BeanUtil.copyProperties(page, Page.class);

        p.setRecords(processCopyVoList);

        return R2.success(p);
    }

    /**
     * 清理数据
     *
     * @param uniqueId      流程唯一id
     * @param flowIdList    process表 流程id集合
     * @param processIdList process表的注解id集合
     * @param tenantId      租户id
     */
    @Override
    public void clearProcess(String uniqueId, List<String> flowIdList, List<Long> processIdList, String tenantId) {


        this.lambdaUpdate()
                .in(ProcessInstanceUserCopy::getFlowId, flowIdList)
                .eq(ProcessInstanceUserCopy::getTenantId, tenantId)
                .remove();

    }
}
