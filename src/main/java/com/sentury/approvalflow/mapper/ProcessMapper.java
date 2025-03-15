package com.sentury.approvalflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sentury.approvalflow.domain.entity.Process;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaoge
 * @since 2023-05-25
 */
public interface ProcessMapper extends BaseMapper<Process> {

    @Select("select * from  flyflow_process where flow_id=#{flowId}")
    Process selectByFlowId(@Param("flowId") String flowId);

}
