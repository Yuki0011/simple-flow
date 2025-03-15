package com.sentury.approvalflow.biz.form;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FormStrategyFactory {

    private static final Map<String, FormStrategy> STRATEGY_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 提供获取策略的方法
     *
     * @param key
     * @return
     */
    public static FormStrategy getStrategy(String key) {
        FormStrategy formStrategy = STRATEGY_CONCURRENT_HASH_MAP.get(key);
        return formStrategy;
    }

    /**
     * 在Bean属性初始化后执行该方法
     *
     * @param key          表单类型
     * @param formStrategy 表单实现类
     */
    public static void register(String key, FormStrategy formStrategy) {
        STRATEGY_CONCURRENT_HASH_MAP.put(key, formStrategy);
    }

    /**
     * 检查字段格式
     *
     * @param formItemVOList
     * @param paramMap
     * @return
     */
    public static R2 checkValue(List<FormItemVO> formItemVOList, Map<String, Object> paramMap) {
        for (FormItemVO formItemVO : formItemVOList) {
            FormStrategy strategy = FormStrategyFactory.getStrategy(formItemVO.getType());
            if (strategy == null) {
                continue;
            }
            Object o = paramMap.get(formItemVO.getId());
            if (o == null || StrUtil.isBlankIfStr(o)) {
                continue;
            }
            R2 r = strategy.checkValue(o);
            if (!r.isOk()) {
                return R2.fail(StrUtil.format("{}格式校验错误：{}", formItemVO.getName(), r.getMsg()));
            }
        }
        return R2.success();
    }

    /**
     * 数据长度
     *
     * @param s
     * @param formType
     * @return
     */
    public static int length(String s, String formType) {
        if (StrUtil.isBlank(s)) {
            return 0;
        }
        return getStrategy(formType).length(s);
    }


}
