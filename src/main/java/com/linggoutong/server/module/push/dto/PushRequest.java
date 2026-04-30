package com.linggoutong.server.module.push.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "推送请求")
public class PushRequest {

    @NotBlank(message = "推送标题不能为空")
    @Schema(description = "推送标题", example = "订单通知")
    private String title;

    @NotBlank(message = "推送内容不能为空")
    @Schema(description = "推送内容", example = "您的订单已发货")
    private String content;

    @Schema(description = "推送类型", example = "ORDER_STATUS")
    private String type;

    @Schema(description = "目标平台", example = "ALL", allowableValues = {"ALL", "iOS", "Android"})
    private String platform;

    @Schema(description = "扩展数据")
    private Map<String, Object> extra;
}
