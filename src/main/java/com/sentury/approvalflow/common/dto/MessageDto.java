package com.sentury.approvalflow.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-07-25 17:20
 */
@Schema(description = "")
@Data
public class MessageDto extends PageDto {
    /**
     * 是否已读
     */

    @Schema(description = "是否已读")
    private Boolean readed;
    /**
     *  id
     */

    @Schema(description = "id")
    private Long id;

}
