package com.sentury.approvalflow.biz.form.impl;

import com.sentury.approvalflow.common.constants.FormTypeEnum;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.form.FormStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SelectDeptFormStrategyImpl implements InitializingBean, FormStrategy {
    @Override
    public void afterPropertiesSet() throws Exception {
        afterPropertiesSet(FormTypeEnum.SELECT_DEPT.getType());
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
        if (StrUtil.isAllBlank(jsonString1, jsonString2)) {
            return true;
        }
        //至少有一个不为空
        //如果有一个空的 肯定不一致
        if (StrUtil.isBlank(jsonString1) || StrUtil.isBlank(jsonString2)) {
            return false;
        }
        List<NodeUser> list1 = JsonUtil.parseArray(jsonString1, NodeUser.class);
        List<NodeUser> list2 = JsonUtil.parseArray(jsonString2, NodeUser.class);
        if (list1.size() != list2.size()) {
            //数量不一致 肯定不匹配
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            //挨个判断每个值
            NodeUser relatedProcessValue1 = list1.get(i);
            NodeUser relatedProcessValue2 = list2.get(i);
            if (!relatedProcessValue1.getId().equals(relatedProcessValue2.getId())) {
                return false;
            }
        }


        return true;
    }


    /**
     * 检查字段格式
     *
     * @param value
     * @return
     */
    @Override
    public R2 checkValue(Object value) {
        try {
            List<NodeUser> nodeUserList = BeanUtil.copyToList(JsonUtil.toList(value), NodeUser.class);
            if(nodeUserList.size()>1){
                return R2.fail("字段格式错误");
            }
            return R2.success();

        } catch (Exception e) {

        }
        return R2.fail("字段格式错误");
    }

    /**
     * 数据的长度
     *
     * @param s
     * @return
     */
    @Override
    public int length(String s) {
        List<NodeUser> nodeUserList = JsonUtil.parseArray(s, NodeUser.class);

        return nodeUserList.size();
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
        if(value==null){
            return true;
        }
        List<NodeUser> nodeUserList = JsonUtil.parseArray(JsonUtil.toJSONString(value), NodeUser.class);

        return CollUtil.isEmpty(nodeUserList);
    }
}
