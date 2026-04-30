package com.linggoutong.server.module.web;

import com.linggoutong.server.common.result.R;
import com.linggoutong.server.module.file.dto.FileDTO;
import com.linggoutong.server.module.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/web/file")
@RequiredArgsConstructor
@Tag(name = "Web端文件", description = "Web端文件接口")
public class WebFileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "Web端上传单个文件")
    public R<FileDTO> upload(@RequestParam("file") MultipartFile file) {
        return R.ok(fileService.upload(file));
    }

    @PostMapping("/upload/batch")
    @Operation(summary = "批量上传文件", description = "Web端批量上传文件")
    public R<List<FileDTO>> uploadBatch(@RequestParam("files") List<MultipartFile> files) {
        return R.ok(fileService.uploadBatch(files));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文件信息", description = "根据文件ID获取文件信息")
    public R<FileDTO> getFile(@PathVariable Long id) {
        var fileInfo = fileService.getFileById(id);
        FileDTO dto = new FileDTO();
        dto.setId(fileInfo.getId());
        dto.setOriginalName(fileInfo.getOriginalName());
        dto.setFileUrl(fileInfo.getFileUrl());
        dto.setFileSize(fileInfo.getFileSize());
        dto.setFileType(fileInfo.getFileType());
        dto.setCreateTime(fileInfo.getCreateTime() != null ? fileInfo.getCreateTime().toString() : null);
        return R.ok(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件", description = "Web端删除文件")
    public R<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return R.ok();
    }
}
