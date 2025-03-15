package com.sentury.approvalflow.config;

import com.sentury.approvalflow.common.dto.R2;
import com.sentury.approvalflow.common.exception.BusinessException;
import com.sentury.approvalflow.common.exception.LoginExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
//@Component
//@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public R2 businessExceptionHandler(BusinessException e){
        log.error("BusinessException：",e);
        R2 fail = R2.fail(e.getMessage());
//        fail.setTraceId(TLogContext.getTraceId());

        return fail;
    }
    @ExceptionHandler(LoginExpiredException.class)
    public R2 loginExpiredExceptionHandler(LoginExpiredException e){
        R2 fail = R2.fail(e.getMessage());
        fail.setCode(e.getCode());
//        fail.setTraceId(TLogContext.getTraceId());
        return fail;

    }

    @ExceptionHandler(RuntimeException.class)
    public R2 runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException：",e);
        R2 fail = R2.fail(e.getMessage());
//        fail.setTraceId(TLogContext.getTraceId());
        return fail;

    }

    /**
     * 参数校验
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R2 paramCheckExceptionHandler(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException：",e);

        String s = e.getBindingResult().getAllErrors().stream()
                .map(w -> w.getDefaultMessage())
                .collect(Collectors.joining("; "));
        R2 fail = R2.fail(s);
//        fail.setTraceId(TLogContext.getTraceId());
        return fail;

    }
}
