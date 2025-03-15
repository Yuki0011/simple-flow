package com.sentury.approvalflow.biz.utils;

import com.sentury.approvalflow.common.dto.third.DeptDto;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

public class DeptUtil {

    public static List<String> queryRootIdList(String deptId, List<DeptDto> deptList){
        if(StrUtil.isBlank(deptId)||StrUtil.equals(deptId,"0")){
            return new ArrayList<>();
        }
        List<String> list=new ArrayList<>();
        DeptDto oaDepartments = deptList.stream().filter(w -> w.getId().equals(deptId)).findFirst().get();
        String parentId = oaDepartments.getParentId();
        list.add(deptId);
        List<String> integers = queryRootIdList(parentId, deptList);
        list.addAll(integers);
        return list;
    }

}
