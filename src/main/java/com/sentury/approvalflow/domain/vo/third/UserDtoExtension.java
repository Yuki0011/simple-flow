package com.sentury.approvalflow.domain.vo.third;

import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.domain.vo.UserFieldDataVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class UserDtoExtension extends UserDto {

    private String deptName;

    private Set<String> roles;
    private Set<String> perms;
    //扩展字段
    private List<UserFieldDataVo> userFieldDataList;

    private Map<String,Object> fieldData;

    private String verifyCode;

    private String verifyCodeKey;

    private List<Long> roleIds;


}
