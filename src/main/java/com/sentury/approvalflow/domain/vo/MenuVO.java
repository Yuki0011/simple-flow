package com.sentury.approvalflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sentury.approvalflow.domain.constants.MenuTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class MenuVO {

    private Long id;

    private Long parentId;

    private String name;

    private MenuTypeEnum type;

    private String path;

    private String component;

    private Integer sort;

    private Integer visible;

    private String icon;

    private String redirect;

    private String perm;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<MenuVO> children;

}
