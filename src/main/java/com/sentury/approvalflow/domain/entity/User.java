package com.sentury.approvalflow.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Vincent
 * @since 2023-07-06
 */
@Getter
@Setter
@Accessors(chain = true)
public class User  extends BaseEntityForWorkFlow {



    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @TableField("`name`")
    private String name;

    /**
     * 拼音  全拼
     */
    @Schema(description = "拼音  全拼")
    @TableField("`pinyin`")
    private String pinyin;

    /**
     * 直属领导id
     */
    @Schema(description = "直属领导id")
    @TableField("`parent_id`")
    private Long parentId;

    /**
     * 拼音, 首字母缩写
     */
    @Schema(description = "拼音, 首字母缩写")
    @TableField("`py`")
    private String py;



    /**
     * 头像url
     */
    @Schema(description = "头像url")
    @TableField("`avatar_url`")
    private String avatarUrl;



    /**
     * 登录密码
     */
    @Schema(description = "登录密码")
    @TableField(value = "`password`",select = false)
    private String password;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @TableField("`phone`")
    private String phone;
    @Schema(hidden = true)
    @TableField("`status`")
    private Integer status;
}
