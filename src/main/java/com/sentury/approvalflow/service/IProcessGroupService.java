package com.sentury.approvalflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.domain.entity.ProcessGroup;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Vincent
 * @since 2023-05-25
 */
public interface IProcessGroupService extends IService<ProcessGroup> {
    /**
     * 组列表
     * @return
     */
    R2<List<ProcessGroup>> queryList();

    /**
     * 新增流程分组
     *
     * @param processGroup 分组名
     * @return 添加结果
     */
    R2 create(ProcessGroup processGroup);

    /**
     * 上移
     * @param processGroup
     * @return
     */
    R2 topSort(ProcessGroup processGroup);

    /**
     * 下移
     * @param processGroup
     * @return
     */
    R2 bottomSort(ProcessGroup processGroup);

    /**
     * 修改组
     * @param processGroup
     * @return
     */
    R2 edit(ProcessGroup processGroup);

    /**
     *  删除分组
     * @param id
     * @return
     */
    R2 delete(long id);
}
