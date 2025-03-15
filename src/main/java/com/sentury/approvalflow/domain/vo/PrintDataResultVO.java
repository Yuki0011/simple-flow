package com.sentury.approvalflow.domain.vo;



import lombok.Data;

import java.util.List;

/**
 * 打印数据
 * @author Huijun Zhao
 * @description
 * @date 2023-10-12 09:47
 */

@Data
public class PrintDataResultVO {
    /**
     * 自定义模板
     */

    private Boolean printTemplateEnable;
    /**
     * 自定义模板内容
     */

    private String printTemplateContent;

    /**
     * 流程结果
     */

    private Integer processInstanceResult;
    /**
     * 流程状态
     */

    private Integer processStatus;
    /**
     * 流程实例id
     */

    private String processInstanceId;
    /**
     * 流程业务key
     */

    private String processInstanceBizKey;
    /**
     * 流程业务编码
     */

    private String processInstanceBizCode;
    /**
     * 流程状态显示
     */

    private String processStatusShow;
    /**
     * 发起人姓名
     */

    private String starterName;
    /**
     * 发起人部门名称
     */

    private String starterDeptName;
    /**
     * 流程名称
     */

    private String processName;
    /**
     * 发起时间
     */

    private String startTime;
    /**
     * 表单集合
     */

    private List<Form> formList;
    /**
     * 审批集合
     */

    private List<Approve> approveList;

    /**
     * 表单
     */

    @Data
    public static class Form{
        /**
         * 表单名称
         */

        private String formName;
        /**
         * 表单类型
         */

        private String formType;
        /**
         * 表单值
         */

        private Object formValue;
        /**
         * 表单值显示
         */

        private String formValueShow;
    }

    /**
     * 审批对象
     */

    @Data
    public static class Approve{
        /**
         * 节点名称
         */

        private String nodeName;
        /**
         * 操作类型
         */

        private String operType;
        /**
         * 评论
         */

        private String desc;
    }

}
