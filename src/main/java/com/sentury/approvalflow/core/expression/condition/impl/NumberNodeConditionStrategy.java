package com.sentury.approvalflow.core.expression.condition.impl;

import com.sentury.approvalflow.common.constants.FormTypeEnum;
import com.sentury.approvalflow.common.dto.flow.Condition;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.sentury.approvalflow.core.expression.ExpressionHandler;
import com.sentury.approvalflow.core.expression.condition.NodeConditionStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 字符类型处理器
 */
@Component
public class NumberNodeConditionStrategy implements NodeConditionStrategy, InitializingBean {
    /**
     * 抽象方法 处理表达式
     */
    @Override
    public String handleExpression(Condition condition) {


        String compare = condition.getExpression();
        String id = condition.getKey();
        Object value = condition.getValue();

        return StrUtil.format("(expressionHandler.numberHandler(\"{}\",\"{}\",{},execution))", id, compare,
                value);


    }

    /**
     * 处理数据
     *
     * @param condition
     * @param paramMap
     * @param tenantId
     * @return
     */
    @Override
    public boolean handleResult(Condition condition, Map<String, Object> paramMap, String tenantId) {
        String compare = condition.getExpression();
        String id = condition.getKey();
        Object value = condition.getValue();

        ExpressionHandler bean = SpringUtil.getBean(ExpressionHandler.class);

        return bean.numberHandler(id,compare,value,paramMap.get(id));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FormTypeEnum.NUMBER.getType());
    }
}
