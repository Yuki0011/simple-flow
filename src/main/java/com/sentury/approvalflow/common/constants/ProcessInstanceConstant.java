package com.sentury.approvalflow.common.constants;

import lombok.Data;

public class ProcessInstanceConstant {
    /**
     * 分页最大查询数量
     */
    public static final int PAGE_MAX_COUNT=50;

    /**
     * 空执行人
     */
    public static final String DEFAULT_EMPTY_ASSIGN = "empty_user";
    /**
     * 用户任务没有执行人的情况下如何处理
     * 自动通过
     */
    public static final String USER_TASK_NOBODY_HANDLER_TO_PASS = "TO_PASS";
    /**
     * 转交给管理员
     */
    public static final String USER_TASK_NOBODY_HANDLER_TO_ADMIN = "TO_ADMIN";
    /**
     * 指定人员
     */
    public static final String USER_TASK_NOBODY_HANDLER_TO_USER = "TO_USER";
    /**
     * 自动拒绝
     */
    public static final String USER_TASK_NOBODY_HANDLER_TO_REFUSE = "TO_REFUSE";

    /**
     * 会签
     */
    public static final int MULTIPLE_MODE_ALL_SAME = 1;
    /**
     * 或签
     */
    public static final int MULTIPLE_MODE_ONE = 2;
    /**
     * 顺签
     */
    public static final int MULTIPLE_MODE_ALL_SORT = 3;
    /**
     * 聚合网关标识
     */
    public static final String MERGE_GATEWAY_FLAG = "_merge_gateway";

    /**
     * 流程设置 去重的value
     */
    @Data
    public static class ProcessSettingDistinctValueClass {
        //只要有一次审批通过 就去重
        public static final int ALL = 1;
        //仅在连续出现时，自动去重
        public static final int CONTINUED = 2;
    }


    /**
     * 审批人员类型
     */
    public static class AssignedTypeClass {
        //指定用户
        public static final int USER = 1;
        //发起人自己
        public static final int SELF = 5;


        //表单人员
        public static final int FORM_USER = 8;
        //表单部门
        public static final int FORM_DEPT = 9;


        //指定主管
        public static final int LEADER = 2;
        //连续多级主管
        public static final int LEADER_TOP = 7;

        //角色
        public static final int ROLE = 3;
    }

    /**
     * 审批人是表单部门时  用户类型
     */
    public static class AssignedTypeFormDeptUserTypeClass {
        /**
         * 部门人员
         */
        public static final String ALL_USER = "allUser";
        /**
         * 主管
         */
        public static final String LEADER = "leader";
        /**
         * 角色
         */
        public static final String ROLE = "role";
    }

    /**
     * 变量key
     */
    public static class VariableKey {

        //默认租户id
        public static final String DEFAULT_TENANT_ID="-1";
        //flowuniqueid的redis key
        public static final String REDIS_KEY_OF_FLOW_UNIQUE_ID="flow_unique_id_{}";

        //http租户id的key
        public static final String HTTP_HEADER_TENANT_ID_KEY="Flyflow-Tenant-Id";

        //发起人主部门id的key
        public static final String START_USER_MAIN_DEPTID_KEY="startUserMainDeptId";
        //系统编码
        public static final String SYS_CODE = "flyflow";
        //发起人
        public static final String STARTER_USER = "rootUser";
        //发起人节点
        public static final String START_NODE = "root";
        //流程编号
        public static final String PROCESS_INSTANCE_CODE = "processInstanceCode";
        //结束
        public static final String END = "end";
        //是否撤回
        public static final String CANCEL = "cancel";

        // 支持自动跳过
        public static final String ENABLE_SKIP_EXPRESSION = "_ACTIVITI_SKIP_EXPRESSION_ENABLED";


        //审批结果 boolean
        public static final String APPROVE_RESULT = "approveResult";


        //任务类型
        public static final String TASK_TYPE = "taskType";
        //任务变量
        public static final String TASK_VARIABLES = "taskVariables";

        public static final String APPROVE_NODE_RESULT = "approveNodeResult";


        //流程唯一id
        public static final String FLOW_UNIQUE_ID = "flowUniqueId";

        //节点跳转的key
        public static final String NODE_JUMP_KEY = "{}_parent_id";

        //委派的状态
        public static final String PENDING = "PENDING";
        //自动完成任务
        public static final String AUTO_COMPLETE_TASK = "autoCompleteTask";

    }



    /**
     * 表单权限
     */
    public static class FormPermClass {
        //隐藏
        public static final String HIDE = "H";
        //只读
        public static final String READ = "R";
        //编辑
        public static final String EDIT = "E";
    }



    /**
     * 条件符号
     */
    public static class ConditionSymbol {
        //相等
        public static final String EQUAL = "==";
        //不等
        public static final String NOT_EQUAL = "!=";

        //包含
        public static final String CONTAIN = "contain";

        //不包含
        public static final String NOT_CONTAIN = "notcontain";

        //存在于
        public static final String IN = "in";
        //不存在于
        public static final String NOT_IN = "notin";

        //属于同级及子级
        public static final String IN_CHILD = "in_child";
        //不属于同级及子级
        public static final String NOT_IN_CHILD = "notin_child";
        //重合
        public static final String INTERSECTION = "intersection";
        //范围
        public static final String RANGE = "range";
        //范围
        public static final String RANGE_USER = "rangeUser";
        //范围
        public static final String RANGE_DEPT = "rangeDept";
        //角色
        public static final String ROLE = "role";
        //不为空
        public static final String NOT_EMPTY = "notempty";
        //空
        public static final String EMPTY = "empty";

    }
}
