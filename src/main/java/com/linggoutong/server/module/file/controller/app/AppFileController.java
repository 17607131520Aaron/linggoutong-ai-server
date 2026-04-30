package com.linggoutong.server.module.file.controller.app;

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
@RequestMapping("/api/app/file")
@RequiredArgsConstructor
@Tag(name = "App端文件", description = "App端文件接口")
public class AppFileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "App端上传单个文件")
    public R<FileDTO> upload(@RequestParam("file") MultipartFile file) {
        return R.ok(fileService.upload(file));
    }

    @PostMapping("/upload/batch")
    @Operation(summary = "批量上传文件", description = "App端批量上传文件")
    public R<List<FileDTO>> uploadBatch(@RequestParam("files") List<MultipartFile> files) {
        return R.ok(fileService.uploadBatch(files));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件", description = "App端删除文件")
    public R<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return R.ok();
    }
}
