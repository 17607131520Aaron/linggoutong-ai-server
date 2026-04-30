package com.linggoutong.server.module.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件信息")
public class FileDTO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "上传时间")
    private String createTime;
}
