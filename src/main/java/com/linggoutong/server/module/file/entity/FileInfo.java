package com.linggoutong.server.module.file.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_info")
public class FileInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String originalName;

    private String fileName;

    private String filePath;

    private String fileUrl;

    private Long fileSize;

    private String fileType;

    private String storageType;

    private Long uploaderId;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
