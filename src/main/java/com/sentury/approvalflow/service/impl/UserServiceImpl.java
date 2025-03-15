package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.PageResultDto;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.dto.third.UserQueryDto;
import com.sentury.approvalflow.common.utils.AvatarUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.sentury.approvalflow.domain.entity.DeptUser;
import com.sentury.approvalflow.domain.entity.User;
import com.sentury.approvalflow.domain.entity.UserRole;
import com.sentury.approvalflow.mapper.UserMapper;
import com.sentury.approvalflow.service.IDeptUserService;
import com.sentury.approvalflow.service.IFileService;
import com.sentury.approvalflow.service.IUserRoleService;
import com.sentury.approvalflow.service.IUserService;
import com.sentury.approvalflow.biz.utils.PinYinUtil;
import com.sentury.approvalflow.domain.vo.UserBizVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-05
 */
@Service
public class UserServiceImpl extends MPJBaseServiceImpl<UserMapper, User> implements IUserService {


    @Resource
    private IUserRoleService userRoleService;

    @Resource
    private IDeptUserService deptUserService;


    @PostConstruct
    public void init() {

        //设置默认头像和密码
        List<User> userList = this.lambdaQuery().and(w -> w.isNull(User::getPassword).or().isNull(User::getAvatarUrl)).list();
        for (User user : userList) {
            if (StrUtil.isBlank(user.getAvatarUrl())) {
                File file = AvatarUtil.generateImg(user.getName());
                IFileService fileService = SpringUtil.getBean(IFileService.class);
                String url = fileService.save(FileUtil.readBytes(file), file.getName()).getData();
                file.delete();
                user.setAvatarUrl(url);
            }
            if (StrUtil.isBlank(user.getPassword())) {
                user.setPassword(SecureUtil.md5("123456"));
            }

            this.updateById(user);

        }
        //设置默认密码

    }

    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    @Override
    public R2 password(User user) {
        String pwd = SecureUtil.md5(user.getPassword());
        this.lambdaUpdate().set(User::getPassword, pwd).eq(User::getId, user.getId()).update(new User());
        return R2.success();
    }

    /**
     * 修改用户状态
     *
     * @param user
     * @return
     */
    @Override
    public R2 status(User user) {
        this.lambdaUpdate().set(User::getStatus, user.getStatus()).eq(User::getId, user.getId()).update(new User());

        return R2.success();
    }


    /**
     * 创建用户
     *
     * @param userBizVO
     * @return
     */
    @Transactional
    @Override
    public R2 createUser(UserBizVO userBizVO) {


        String phone = userBizVO.getPhone();
        Long count = this.lambdaQuery().eq(User::getPhone, phone).count();
        if (count > 0) {
            return R2.fail(StrUtil.format("手机号[{}]已注册", phone));
        }
        List<String> deptIdList = userBizVO.getDeptIdList();
        if (CollUtil.isEmpty(deptIdList)) {
            return R2.fail("请选择部门");
        }
        userBizVO.setPy(PinYinUtil.getFirstPinYin(userBizVO.getName()));
        userBizVO.setPinyin(PinYinUtil.getAllPinyin(userBizVO.getName()));

        String pwd = SecureUtil.md5("123456");
        userBizVO.setPassword(pwd);
        this.save(userBizVO);
        //保存部门

        for (String s : deptIdList) {
            DeptUser deptUser = new DeptUser();
            deptUser.setUserId(String.valueOf(userBizVO.getId()));
            deptUser.setDeptId(s);
            deptUserService.save(deptUser);
        }


        //保存角色
        List<String> roleIds = userBizVO.getRoleIds();
        for (String roleId : roleIds) {
            UserRole entity = new UserRole();

            entity.setUserId(String.valueOf(userBizVO.getId()));
            entity.setRoleId(roleId);

            userRoleService.save(entity);

        }

        return R2.success();
    }

    /**
     * 修改用户
     *
     * @param userBizVO
     * @return
     */
    @Transactional
    @Override
    public R2 editUser(UserBizVO userBizVO) {

        Long userId = userBizVO.getId();
        if (userBizVO.getParentId() != null) {
            if (userBizVO.getParentId().longValue() == userId.longValue()) {
                return R2.fail("直属领导不能是用户自己");
            }
        }
        List<String> deptIdList = userBizVO.getDeptIdList();

        if (CollUtil.isEmpty(deptIdList)) {
            return R2.fail("请选择部门");
        }

        String phone = userBizVO.getPhone();
        Long count = this.lambdaQuery().eq(User::getPhone, phone).ne(User::getId, userId).count();
        if (count > 0) {
            return R2.fail(StrUtil.format("手机号[{}]已注册", phone));
        }

        userBizVO.setPy(PinYinUtil.getFirstPinYin(userBizVO.getName()));
        userBizVO.setPinyin(PinYinUtil.getAllPinyin(userBizVO.getName()));
        this.updateById(userBizVO);
        //处理部门
        deptUserService.lambdaUpdate().eq(DeptUser::getUserId, userId).remove();
        for (String s : deptIdList) {
            DeptUser deptUser = new DeptUser();
            deptUser.setUserId(String.valueOf(userId));
            deptUser.setDeptId(s);
            deptUserService.save(deptUser);
        }


        //先删除用户角色
        LambdaQueryWrapper<UserRole> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleService.remove(objectLambdaQueryWrapper.eq(UserRole::getUserId, userId));
        //保存角色
        List<String> roleIds = userBizVO.getRoleIds();
        for (String roleId : roleIds) {
            UserRole entity = new UserRole();

            entity.setUserId(String.valueOf(userId));
            entity.setRoleId(roleId);

            userRoleService.save(entity);

        }


        return R2.success();
    }


    /**
     * 查询本地数据库用户列表
     *
     * @param userListQueryVO 查询参数
     * @return
     */
    @Override
    public PageResultDto<UserDto> queryLocalList(UserQueryDto userListQueryVO) {
        String queryVODeptId = userListQueryVO.getDeptId();
        List<String> queryVODeptIdList = userListQueryVO.getDeptIdList();
        MPJLambdaWrapper<User> lambdaQueryWrapper = new MPJLambdaWrapper<User>()
                .selectAll(User.class)

                .like(StrUtil.isNotBlank(userListQueryVO.getName()), User::getName, userListQueryVO.getName())
                .eq(userListQueryVO.getStatus() != null, User::getStatus, userListQueryVO.getStatus())
                .and(StrUtil.isNotBlank(userListQueryVO.getKeywords()),
                        k -> k.like(User::getName, userListQueryVO.getKeywords()).or().like(User::getPhone,
                                userListQueryVO.getKeywords())
                )
                .orderByDesc(User::getCreateTime);

        if (StrUtil.isNotBlank(queryVODeptId) || CollUtil.isNotEmpty(queryVODeptIdList)) {
            lambdaQueryWrapper.leftJoin(DeptUser.class, DeptUser::getUserId, User::getId);
            if (StrUtil.isNotBlank(queryVODeptId)) {
                lambdaQueryWrapper.eq(DeptUser::getDeptId, queryVODeptId);
            }
            if (CollUtil.isNotEmpty(queryVODeptIdList)) {
                lambdaQueryWrapper.in(DeptUser::getDeptId, queryVODeptIdList);
            }
        }


        Page<UserBizVO> objectPage = this.selectJoinListPage(new Page<>(userListQueryVO.getPageNum(),
                userListQueryVO.getPageSize()), UserBizVO.class, lambdaQueryWrapper);
        List<UserBizVO> records = objectPage.getRecords();

        List<UserDto> userDtoList = new ArrayList<>();
        for (UserBizVO record : records) {
            UserDto userDto = new UserDto();
            userDto.setId(String.valueOf(record.getId()));
            userDto.setParentId(String.valueOf(record.getParentId()));
            userDto.setName(record.getName());
            userDto.setAvatarUrl(record.getAvatarUrl());
            userDto.setStatus(record.getStatus());
            userDto.setPhone(record.getPhone());

            //获取部门数据
            List<DeptDto> deptDtoList = deptUserService.queryDeptList(userDto.getId());
            List<String> deptIdList = deptDtoList.stream().map(DeptDto::getId).collect(Collectors.toList());
            String deptName = deptDtoList.stream().map(DeptDto::getName).collect(Collectors.joining(","));

            userDto.setDeptName(deptName);
            userDto.setDeptIdList(deptIdList);


            userDtoList.add(userDto);
        }

        PageResultDto<UserDto> pageResultDto = new PageResultDto<>();
        pageResultDto.setTotal(objectPage.getTotal());
        pageResultDto.setRecords(userDtoList);

        return pageResultDto;
    }
}
