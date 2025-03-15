package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.domain.entity.User;
import com.sentury.approvalflow.service.IOrgService;
import com.sentury.approvalflow.service.IUserBizService;
import com.sentury.approvalflow.service.IUserService;
import com.sentury.approvalflow.domain.vo.UserBizVO;
import com.sentury.approvalflow.domain.vo.UserListQueryVO;
import com.sentury.approvalflow.common.dto.R2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户接口
 */
@Tag(name = "用户接口", description = "用户接口")
@RestController
@RequestMapping(value = {"user"})
public class UserController {

    @Resource
    private IUserService userService;
    @Resource
    private IUserBizService userBizService;
    @Resource
    private IOrgService orgService;


    /**
     * 创建用户
     * @param user
     * @return
     */
    @Operation(summary = "创建用户", description = "创建用户")
    @PostMapping("create")
    public R2 create(@RequestBody UserBizVO user){
        return userService.createUser(user);
    }
    /**
     * 修改用户
     * @param user
     * @return
     */
    @Operation(summary = "修改用户", description = "修改用户")
    @PutMapping("edit")
    public R2 editUser(@RequestBody UserBizVO user){
        return userService.editUser(user);
    }



    /**
     * 修改密码
     * @param user
     * @return
     */
    @Operation(summary = "修改密码", description = "修改密码")
    @PostMapping("password")
    public R2 password(@RequestBody User user){
        return userService.password(user);
    }
    /**
     * 修改状态
     * @param user
     * @return
     */
    @Operation(summary = "修改状态", description = "修改状态")
    @PostMapping("status")
    public R2 status(@RequestBody User user){
        return userService.status(user);
    }

    /**
     * 获取当前用户详细信息
     *

     * @return
     */
    @Operation(summary = "获取当前用户详细信息", description = "获取当前用户详细信息")
    @SneakyThrows
    @GetMapping("getCurrentUserDetail")
    public R2 getCurrentUserDetail() {

        return userBizService.getCurrentUserDetail();

    }

    /**
     * 获取用户详细信息
     * @param user
     * @return
     */
    @Operation(summary = "获取用户详细信息", description = "获取用户详细信息")
    @GetMapping("getUserDetail")
    public R2 getUserDetail(String userId){
        return orgService.getUserDetail(userId);
    }


    /**
     * 查询用户列表
     * @param userListQueryVO
     * @return
     */
    @Operation(summary = "查询用户列表", description = "查询用户列表")
    @PostMapping("queryList")
    public R2 queryList(@RequestBody UserListQueryVO userListQueryVO){
        return userBizService.queryList(userListQueryVO);
    }
    /**
     * 删除用户
     * @param userListQueryVO
     * @return
     */
    @Operation(summary = "删除用户", description = "删除用户")
    @DeleteMapping("delete")
    public R2 delete(@RequestBody User userListQueryVO){
        return orgService.delete(userListQueryVO);
    }
}
