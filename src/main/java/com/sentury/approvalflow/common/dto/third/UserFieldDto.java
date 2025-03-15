package com.sentury.approvalflow.common.dto.third;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户字段
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */
@Schema(description = "<p> 用户字段 </p>")
@Getter
@Setter
@Accessors(chain = true)
public class UserFieldDto  {


    /**
     * 用户名
     */

    @Schema(description = "用户名")
    private String name;


    /**
     * 字段类型
     */

    @Schema(description = "字段类型")
    private String type;

    /**
     * 是否必填
     */

    @Schema(description = "是否必填")
    private Boolean required;

    /**
     * 配置json字符串
     */

    @Schema(description = "配置json字符串")
    private String props;

    /**
     * 字段
     */

    @Schema(description = "字段")
    private String key;
}
