package com.sentury.approvalflow.common.dto.flow;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sentury.approvalflow.common.constants.FormTypeEnum;
import com.sentury.approvalflow.common.dto.flow.node.EmptyNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表单
 */
@Schema(description = "表单")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormItemVO {
    /**
     * 表单id
     */

    @Schema(description = "表单id")
    private String id;
    /**
     * 表单权限  R E H
     */

    @Schema(description = "表单权限  R E H")
    private String perm;
    /**
     *  标题图标
     */

    @Schema(description = "标题图标")
    private String icon;
    /**
     * 表单名称
     */

    @Schema(description = "表单名称")
    private String name;

    @Schema(description = "取值字段")
    private String valueFiled;
    @Schema(description = "泰语字段")
    private String nameSecond;
    @Schema(description = "宽度")
    private Integer colWidth;
//    @Schema(description = "宽度")
//    private String height;
//    @Schema(description = "是否有添加按钮")
//    private Boolean hasAdd;
    /**
     * 表单类型 {@link FormTypeEnum}
     */

    @Schema(description = "表单类型 {@link FormTypeEnum}")
    private String type;

    /**
     * 是否必须
     */

    @Schema(description = "是否必须")
    private Boolean required;

    /**
     * 表单类型名称
     */

    @Schema(description = "表单类型名称")
    private String typeName;
    /**
     * 显示提示语
     */

    @Schema(description = "显示提示语")
    private String placeholder;
    /**
     * 表单属性
     */

    @Schema(description = "表单属性")
    private Props props;



    /**
     * 动态表单配置
     */
    @Schema(description = "动态表单配置")
    @Data
    public static class DynamicFormConfig{

        /**
         * 表单配置
         */

        @Schema(description = "表单配置")
        private List<FormConfig> list;
        /**
         * 节点条件
         */

        @Schema(description = "节点条件")
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,property = "type",defaultImpl = EmptyNode.class)
        private EmptyNode condition;

        /**
         * 表单配置
         */
        @Schema(description = "表单配置")
        @Data
        public static class FormConfig {
            /**
             * 配置值
             */

            @Schema(description = "配置值")
            private Object value;

            /**
             * 配置条件
             */

            @Schema(description = "配置条件")
            private String contentConfig;
        }


    }

    /**
     * 表单属性
     */
    @Schema(description = "表单属性")
    @Data
    public static class Props{
        /**
         * 是否是私有变量值 如果true 则变量值存储在任务私有变量里
         */

        @Schema(description = "是否是私有变量值 如果true 则变量值存储在任务私有变量里")
        private Boolean privateVal;
        /**
         * 远程配置
         */

        @Schema(description = "远程配置")
        private HttpSetting remoteConfig;

        /**
         * 1手动 2远程
         */

        @Schema(description = "1手动 2远程")
        private Integer dataFrom;
        /**
         * 统计总数
         */

        @Schema(description = "统计总数")
        private Boolean sum;

        /**
         * 表单值
         */

        @Schema(description = "表单值")
        private Object value;
        /**
         * 数据是否是空的
         */

        @Schema(description = "数据是否是空的")
        private Boolean isBlank=false;
        /**
         * 表单选项
         */

        @Schema(description = "表单选项")
        private Object options;
        /**
         * 是否可以选择自己
         */

        @Schema(description = "是否可以选择自己")
        private Boolean self;
        /**
         * 多选单选
         */

        @Schema(description = "多选单选")
        private Boolean multi;
        /**
         * 是否默认值是发起人
         */

        @Schema(description = "是否默认值是发起人")
        private Boolean defaultRoot;
        /**
         * 旧的表单
         */

        @Schema(description = "旧的表单")
        private Object oriForm;
        /**
         * 最小长度
         */

        @Schema(description = "最小长度")
        private Integer minLength;
        /**
         * 最大长度
         */

        @Schema(description = "最大长度")
        private Integer maxLength;
        /**
         * 小数位数
         */

        @Schema(description = "小数位数")
        private Integer radixNum;
        /**
         * 最大尺寸/数量等
         */

        @Schema(description = "最大尺寸/数量等")
        private Integer maxSize;
        /**
         * 是否显示中文大写
         */

        @Schema(description = "是否显示中文大写")
        private Boolean showChinese;
        /**
         * 正则表达式
         */

        @Schema(description = "正则表达式")
        private String regex;
        /**
         * 正则表达式描述
         */

        @Schema(description = "正则表达式描述")
        private String regexDesc;
        /**
         * 单位
         */

        @Schema(description = "单位")
        private String unit;
        /**
         * 是否可以选半个  比如评分表单
         */

        @Schema(description = "是否可以选半个  比如评分表单")
        private Boolean halfSelect;
        /**
         * 表达式集合  公式表达中使用
         */

        @Schema(description = "表达式集合  公式表达中使用")
        private List expList;

        /**
         * 是否使用上次的内容
         * 签名表单
         */

        @Schema(description = "是否使用上次的内容 签名表单")
        private Boolean lastContent;
        /**
         * 是否只读
         */

        @Schema(description = "是否只读")
        private Boolean readonly;
        /**
         * 文件后缀集合
         */

        @Schema(description = "文件后缀集合")
        private Object suffixArray;
        /**
         * 最大值
         */

        @Schema(description = "最大值")
        private Object max;
        /**
         * 最小值
         */

        @Schema(description = "最小值")
        private Object min;

        @Schema(description = "宽度")
        private String height;
        @Schema(description = "是否有添加按钮")
        private Boolean hasAdd;
        @Schema(description = "是否")
        private Boolean hasAssign;



    }

}
