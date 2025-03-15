package com.sentury.approvalflow.common.dto.third;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    /**
     * 用户id 不能为空
     */
    private String id;
    /**
     * 直属领导id
     */
    private String parentId;

    /**
     * 用户姓名 不能为空
     */
    private String name;
    /**
     * 用户头像 不能为空
     */
    private String avatarUrl;
    /**
     * 用户所属部门id 可以为空
     */
    private List<String> deptIdList;
    /**
     * 用户状态 0禁用 1启用
     */
    private Integer status;
    private String token;

    /**
     * 手机号
     */
    private String phone;
    /**
     * 部门名称
     */
    private String deptName;

    private String leaderCode;


    // 职务
    private String position;
    // 序列
    private String sequence;
    // 职级
    private String postRank;
    // 入职日期
    private LocalDate onBoardingDate;
    // 试用期开始日期
    private LocalDate trialDateStart;
    // 试用期结束日期
    private LocalDate trialDateEnd;
    // 转正日期
    private LocalDate regularizationDate;

    private List<String> flowNames;

}
