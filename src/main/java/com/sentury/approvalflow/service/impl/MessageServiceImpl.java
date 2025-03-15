package com.sentury.approvalflow.service.impl;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.third.MessageDto;
import com.sentury.approvalflow.common.utils.TenantUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sentury.approvalflow.domain.entity.Message;
import com.sentury.approvalflow.mapper.MessageMapper;
import com.sentury.approvalflow.service.IClearService;
import com.sentury.approvalflow.service.IMessageService;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 通知消息 服务实现类
 * </p>
 *
 * @author xiaoge
 * @since 2023-07-25
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService, IClearService {

    /**
     * 查询未读数量
     *
     * @return
     */
    @Override
    public R2 queryUnreadNum(Long lastId) {
        if(lastId==null){
            lastId=0L;
        }
        String userId = LoginHelper.getUserId()+"";
        Long num = this.lambdaQuery()
                .eq(Message::getReaded,false)
                .eq(Message::getTenantId,TenantUtil.get())
                .eq(Message::getUserId, userId).count();

        Message message = this.lambdaQuery().eq(Message::getTenantId, TenantUtil.get())
                .gt(Message::getId,lastId)
                .eq(Message::getUserId, userId).orderByDesc(Message::getCreateTime)
                .last("limit 1").one();

        Dict set = Dict.create().set("num", num)
                .set("maxId", message == null ? null : message.getId())
                .set("title",message==null?null:message.getTitle())
                .set("content",message==null?null:message.getContent())
                ;

        return R2.success(set);
    }

    /**
     * 保存消息
     *
     * @param messageDto
     * @return
     */
    @Override
    public R2 saveMessage(MessageDto messageDto) {
        Message message = BeanUtil.copyProperties(messageDto, Message.class);
        this.save(message);
        return R2.success();
    }

    /**
     * 查询列表
     *
     * @param pageDto
     * @return
     */
    @Override
    public R2<Page<Message>> queryList(com.sentury.approvalflow.common.dto.MessageDto pageDto) {
        Page<Message> messagePage = this.lambdaQuery()
                .eq(Message::getUserId, LoginHelper.getUserId()+"")
                .eq(Message::getTenantId,TenantUtil.get())
                .eq(pageDto.getReaded()!=null, Message::getReaded,pageDto.getReaded())
                .orderByDesc(Message::getCreateTime)
                .page(new Page<>(pageDto.getPageNum(), pageDto.getPageSize()));
        return R2.success(messagePage);
    }

    /**
     * 删除消息
     *
     * @param messageDto
     * @return
     */
    @Override
    public R2 delete(com.sentury.approvalflow.common.dto.MessageDto messageDto) {
        this.removeById(messageDto.getId());
        return R2.success();
    }

    /**
     * 置为已读
     *
     * @param messageDto
     * @return
     */
    @Override
    public R2 read(com.sentury.approvalflow.common.dto.MessageDto messageDto) {
        String userId = LoginHelper.getUserId()+"";
        this.lambdaUpdate()
                .set(Message::getReaded,true)
                .eq(Message::getUserId,userId)
                .eq(Message::getId,messageDto.getId())
                .eq(Message::getReaded,false)
                .eq(Message::getTenantId,TenantUtil.get())
                .update(new Message());
        return R2.success();
    }

    /**
     * 全部已读
     *
     * @return
     */
    @Override
    public R2 readAll() {
        String userId = LoginHelper.getUserId()+"";

        this.lambdaUpdate().set(Message::getReaded,true)
                .eq(Message::getUserId,userId)

                .eq(Message::getReaded,false)
                .eq(Message::getTenantId,TenantUtil.get())
                .update(new Message());
        return R2.success();
    }

    /**
     * 清理数据
     *
     * @param uniqueId      流程唯一id
     * @param flowIdList    process表 流程id集合
     * @param processIdList process表的注解id集合
     * @param tenantId      租户id
     */
    @Override
    public void clearProcess(String uniqueId, List<String> flowIdList, List<Long> processIdList, String tenantId) {


        this.lambdaUpdate()
                .in(Message::getFlowId, flowIdList)
                .eq(Message::getTenantId, tenantId)
                .remove();

    }
}
