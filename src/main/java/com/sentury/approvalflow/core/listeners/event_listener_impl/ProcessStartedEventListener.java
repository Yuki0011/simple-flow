package com.sentury.approvalflow.core.listeners.event_listener_impl;

import com.sentury.approvalflow.common.dto.ProcessInstanceRecordParamDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.listeners.EventListenerStrategy;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.ExecutionUtil;
import com.sentury.approvalflow.core.utils.FlowableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableProcessStartedEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static com.sentury.approvalflow.common.constants.ProcessInstanceConstant.VariableKey.*;

/**
 * 流程开始了
 *
 * @author Huijun Zhao
 * @description
 * @date 2023-10-10 10:12
 */
@Slf4j
@Component
public class ProcessStartedEventListener implements EventListenerStrategy, InitializingBean {
    /**
     * 处理数据
     *
     * @param event
     * @return
     */
    @Override
    public void handle(FlowableEvent event) {

        //流程开始了
        FlowableProcessStartedEventImpl flowableProcessStartedEvent = (FlowableProcessStartedEventImpl) event;

        ExecutionEntityImpl entity = (ExecutionEntityImpl) flowableProcessStartedEvent.getEntity();
        DelegateExecution execution = flowableProcessStartedEvent.getExecution();
        String flowId = entity.getProcessDefinitionKey();
        //上级实例id 主要是涉及到子流程的
        String nestedProcessInstanceId = flowableProcessStartedEvent.getNestedProcessInstanceId();
        {
            //设置唯一id

            String s = IdUtil.fastSimpleUUID();
            RedisTemplate redisTemplate = SpringUtil.getBean("redisTemplate", RedisTemplate.class);


            redisTemplate.opsForZSet().add(StrUtil.format(REDIS_KEY_OF_FLOW_UNIQUE_ID, entity.getProcessInstanceId()), StrUtil.format(
                            "{}||||{}", START_NODE, s),
                    System.currentTimeMillis());



        }

        String processInstanceId = flowableProcessStartedEvent.getProcessInstanceId();

        String startUserId = FlowableUtils.getStartUserId(execution);
        log.info("发起人id：{}",startUserId);
        Map<String, Object> variables = execution.getVariables();

        String tenantId = entity.getTenantId();
        {


            //流程编号
            String processInstanceBizCode = StrUtil.format("BIZ{}{}", DateUtil.format(new Date(), "yyyyMMddHHmmss"), RandomUtil.randomStringUpper(6));

            RuntimeService runtimeService = SpringUtil.getBean(RuntimeService.class);
            runtimeService.setVariable(processInstanceId, PROCESS_INSTANCE_CODE, processInstanceBizCode);

            ProcessInstanceRecordParamDto processInstanceRecordParamDto = new ProcessInstanceRecordParamDto();
            processInstanceRecordParamDto.setUserId(startUserId);
            processInstanceRecordParamDto.setMainDeptId(MapUtil.getStr(variables,
                    START_USER_MAIN_DEPTID_KEY));
            processInstanceRecordParamDto.setParentProcessInstanceId(nestedProcessInstanceId);
            processInstanceRecordParamDto.setFlowId(flowId);
            processInstanceRecordParamDto.setTenantId(tenantId);
            processInstanceRecordParamDto.setProcessInstanceId(processInstanceId);
            processInstanceRecordParamDto.setProcessInstanceBizKey(entity.getProcessInstanceBusinessKey());
            processInstanceRecordParamDto.setProcessInstanceBizCode(processInstanceBizCode);
            processInstanceRecordParamDto.setFormData(JsonUtil.toJSONString(variables));
            //判断是子流程启动
            if(StrUtil.isNotBlank(nestedProcessInstanceId)){
                String superExecutionId = execution.getParent().getSuperExecutionId();
                String parentExecutionId = ExecutionUtil.getParentExecutionId(superExecutionId);
                processInstanceRecordParamDto.setParentProcessNodeExecutionId(parentExecutionId);
            }
            BizHttpUtil.processStartEvent(processInstanceRecordParamDto);
        }


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FlowableEngineEventType.PROCESS_STARTED.toString());

    }
}
