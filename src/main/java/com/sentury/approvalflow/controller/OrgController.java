package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.service.IOrgService;
import com.sentury.approvalflow.domain.vo.OrgDataVo;
import com.sentury.approvalflow.domain.vo.OrgSelectShowVo;
import com.sentury.approvalflow.common.dto.R2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 组织架构接口
 */

@Tag(name = "组织架构接口", description = "组织架构接口")
@RestController
@RequestMapping(value = {"org"})
public class OrgController {

    @Autowired
    private IOrgService orgService;









    /**
     * 按层级查询组织架构树
     * @param deptId 部门id
     * @param showLeave 是否显示离职员工
     * @return 组织架构树数据
     */
    @Parameters({
            @Parameter(name = "deptId", description = "部门id", in = ParameterIn.QUERY, required = true),
            @Parameter(name = "showLeave", description = "是否显示离职员工", in = ParameterIn.QUERY, required = true)
    })
    @Operation(summary = "按层级查询组织架构树", description = "按层级查询组织架构树")
    @GetMapping("tree")
    public R2<OrgSelectShowVo> getOrgTreeData(@RequestParam(defaultValue ="100") String deptId,
                                              String type,
                                              @RequestParam(defaultValue = "false") Boolean showLeave){
//        String userCode = LoginHelper.getLoginUser().getUserCode();
//        if(!"S00001".equals(userCode)){
//            List<OrgDataVo> orgTreeUser = orgService.getOrgTreeUser(userCode.trim()).getData();
//            OrgSelectShowVo orgSelectShowVo = new OrgSelectShowVo();
//            orgSelectShowVo.setEmployees(orgTreeUser);
//            return R2.success(orgSelectShowVo);
//        }
        return orgService.getOrgTreeData(deptId, type, showLeave);
    }


    /**
     * 查询所有的组织架构树，不分层
     * @return 组织架构树数据
     */
    @Operation(summary = "查询所有的组织架构树，不分层", description = "查询所有的组织架构树，不分层")
    @GetMapping("treeAll")
    public R2 getOrgTreeDataAll(String keywords, Integer status){
        return orgService.getOrgTreeDataAll(keywords, status);
    }

    /**
     * 模糊搜索用户
     * @param userName 用户名/拼音/首字母
     * @return 匹配到的用户
     */
    @Parameter(name = "userName", description = "用户名/拼音/首字母", in = ParameterIn.QUERY, required = true)
    @Operation(summary = "模糊搜索用户", description = "模糊搜索用户")
    @GetMapping("tree/user/search")
    public R2<List<OrgDataVo>> getOrgTreeUser(@RequestParam String userName){
        return orgService.getOrgTreeUser(userName.trim());
    }


}
