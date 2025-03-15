package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.MessageDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.domain.entity.Message;

/**
 * <p>
 * 通知消息 服务类
 * </p>
 *
 * @author xiaoge
 * @since 2023-07-25
 */
public interface IMessageService extends IService<Message> {
    /**
     * 查询未读数量
     *
     * @return
     */
    R2 queryUnreadNum(Long lastId);

    /**
     * 保存消息
     *
     * @param messageDto
     * @return
     */
    R2 saveMessage(MessageDto messageDto);

    /**
     * 查询列表
     *
     * @param pageDto
     * @return
     */
    R2<Page<Message>> queryList(com.sentury.approvalflow.common.dto.MessageDto pageDto);

    /**
     * 删除消息
     *
     * @param messageDto
     * @return
     */
    R2 delete(com.sentury.approvalflow.common.dto.MessageDto messageDto);

    /**
     * 置为已读
     *
     * @param messageDto
     * @return
     */
    R2 read(com.sentury.approvalflow.common.dto.MessageDto messageDto);

    /**
     * 全部已读
     *
     * @return
     */
    R2 readAll();

}
