package com.sentury.approvalflow.biz.form.impl;

import com.sentury.approvalflow.common.constants.FormTypeEnum;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.form.FormStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DescriptionFormStrategyImpl implements InitializingBean, FormStrategy {
    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FormTypeEnum.DESCRIPTION.getType());

    }

    /**
     * 判断两个值是否一致
     *
     * @param v1 第一个值
     * @param v2 第二个值
     * @return true表示一致
     */
    @Override
    public boolean isSameValue(Object v1, Object v2) {
        return true;
    }

    /**
     * 数据的长度
     *
     * @param s
     * @return
     */
    @Override
    public int length(String s) {
        return 1;
    }

    /**
     * 判断是否为空
     *
     * @param value
     * @param formItemVO
     * @param paramMap
     * @return
     */
    @Override
    public boolean isBlank(Object value, FormItemVO formItemVO, Map<String, Object> paramMap) {
        return StrUtil.isBlankIfStr(value);
    }
}
