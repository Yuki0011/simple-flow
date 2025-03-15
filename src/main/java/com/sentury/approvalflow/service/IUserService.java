package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.PageResultDto;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.dto.third.UserQueryDto;
import com.github.yulichang.base.MPJBaseService;
import com.sentury.approvalflow.domain.entity.User;
import com.sentury.approvalflow.domain.vo.UserBizVO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-05
 */
public interface IUserService extends MPJBaseService<User> {


    /**
     * 修改密码
     * @param user
     * @return
     */
    R2 password(User user);

    /**
     * 修改用户状态
     * @param user
     * @return
     */
    R2 status(User user);


    /**
     * 创建用户
     *
     * @param userBizVO
     * @return
     */
    R2 createUser(UserBizVO userBizVO);

    /**
     * 修改用户
     *
     * @param userBizVO
     * @return
     */
    R2 editUser(UserBizVO userBizVO);



    /**
     * 查询本地数据库用户列表
     *
     * @param userListQueryVO 查询参数
     * @return
     */
    PageResultDto<UserDto> queryLocalList(UserQueryDto userListQueryVO);


}
