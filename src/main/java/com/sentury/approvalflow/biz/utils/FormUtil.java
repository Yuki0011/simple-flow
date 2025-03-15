package com.sentury.approvalflow.biz.utils;

import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.FormItemVO;
import cn.hutool.core.util.StrUtil;
import com.sentury.approvalflow.biz.form.FormStrategyFactory;

import java.util.Map;

public class FormUtil {


    /**
     * 处理选择框的选项
     *
     * @param formItemVO
     */
    public static void handSelectOptions(FormItemVO formItemVO, Map<String, Object> processParamMap, String flowId,
                                         String processInstanceId) {


    }

    /**
     * 处理空值
     *
     * @param formItemVO
     * @param value
     */
    public static void handDefaultNullValue(FormItemVO formItemVO, Object value) {

        Object o = FormStrategyFactory.getStrategy(formItemVO.getType()).handleBlankValue(value);

        formItemVO.getProps().setValue(o);


    }




    /**
     * 处理标记是否是空值
     *
     * @param formItemVO
     */
    public static void handFormMarkBlank(FormItemVO formItemVO) {
        Object value = formItemVO.getProps().getValue();
        if (ProcessInstanceConstant.FormPermClass.READ.equals(formItemVO.getPerm())) {
            boolean blank = FormStrategyFactory.getStrategy(formItemVO.getType()).isBlank(value, formItemVO, null);
            //非只读的 不处理
            formItemVO.getProps().setIsBlank(blank);

        }
    }

    /**
     * 处理表单值和选项
     *
     * @param formItemVO
     * @param variableMap
     * @param flowId
     * @param processInstanceId
     */
    public static void handFormValueAndOptions(FormItemVO formItemVO, Map<String, Object> variableMap, String flowId,
                                               String processInstanceId) {

        String fid = formItemVO.getId();
       {
            Object value = variableMap.get(fid);
            FormUtil.handDefaultNullValue(formItemVO, value);
            //处理单选多选选项
            FormUtil.handSelectOptions(formItemVO, variableMap, flowId, processInstanceId);

        }

    }

    /**
     * 处理表单权限
     *
     * @param formItemVO
     * @param formPerms
     * @param defaultPerm      空气权限替换的
     * @param editPermReplaced 如果是可编辑的 替换的权限
     */
    public static void handFormPerm(FormItemVO formItemVO, Map<String, String> formPerms, String defaultPerm, String editPermReplaced
    ) {
        String fid = formItemVO.getId();
        String perm = formPerms.get(fid);
        formItemVO.setPerm(StrUtil.isBlankIfStr(perm) ? defaultPerm : perm);
        if (StrUtil.isNotBlank(editPermReplaced) && StrUtil.equals(formItemVO.getPerm(),
                ProcessInstanceConstant.FormPermClass.EDIT)) {
            formItemVO.setPerm(editPermReplaced);
        }


    }

}
