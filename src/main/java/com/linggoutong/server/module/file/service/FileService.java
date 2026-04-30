package com.linggoutong.server.module.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linggoutong.server.module.file.dto.FileDTO;
import com.linggoutong.server.module.file.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService extends IService<FileInfo> {

    FileDTO upload(MultipartFile file);

    List<FileDTO> uploadBatch(List<MultipartFile> files);

    FileInfo getFileById(Long id);

    void deleteFile(Long id);
}
