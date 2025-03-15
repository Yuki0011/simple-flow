package com.sentury.approvalflow.biz.form.impl;

import com.sentury.approvalflow.common.constants.FormTypeEnum;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.form.FormStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MoneyFormStrategyImpl implements InitializingBean, FormStrategy {
    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FormTypeEnum.MONEY.getType());
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
        String jsonString1 = JsonUtil.toJSONString(v1);
        String jsonString2 = JsonUtil.toJSONString(v2);

        //都为空
        if(StrUtil.isAllBlank(jsonString1,jsonString2)){
            return true;
        }
        //至少有一个不为空
        //如果有一个空的 肯定不一致
        if(StrUtil.isBlank(jsonString1)||StrUtil.isBlank(jsonString2)){
            return false;
        }


        return Convert.toBigDecimal(v1).compareTo(Convert.toBigDecimal(v2))==0;
    }

    /**
     * 处理空值
     *
     * @param value
     * @return
     */
    @Override
    public Object handleBlankValue(Object value) {
        if (StrUtil.isBlankIfStr(value)) {
           return null;
        }
        return value;
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
