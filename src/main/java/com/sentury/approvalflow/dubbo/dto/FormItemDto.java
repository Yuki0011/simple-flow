package com.sentury.approvalflow.dubbo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class FormItemDto {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String valueFiled;
}
