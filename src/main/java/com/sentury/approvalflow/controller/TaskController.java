package com.sentury.approvalflow.controller;

import com.sentury.approvalflow.service.ITaskService;
import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.dto.TaskParamDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 任务实例接口
 */
@Tag(name = "任务实例接口", description = "任务实例接口")
@RestController
@RequestMapping(value = "task")
public class TaskController {

    @Resource
    private ITaskService taskService;


    /**
     * 完成任务
     *
     * @param completeParamDto
     * @return
     */
    @Operation(summary = "完成任务", description = "完成任务")
    @SneakyThrows
    @PostMapping("completeTask")
    public R2 completeTask(@RequestBody TaskParamDto completeParamDto) {

        return taskService.completeTask(completeParamDto);

    }


    /**
     * 获取任务信息
     *
     * @param taskId 任务id
     * @return
     */
    @Operation(summary = "获取任务信息", description = "获取任务信息")
    @GetMapping("getTask")
    public R2 getTask(String taskId) {
        return taskService.getTask(taskId);
    }

}
