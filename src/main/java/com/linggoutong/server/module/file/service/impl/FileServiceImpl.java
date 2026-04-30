package com.linggoutong.server.module.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linggoutong.server.common.exception.BusinessException;
import com.linggoutong.server.common.result.ResultCode;
import com.linggoutong.server.common.security.SecurityUtils;
import com.linggoutong.server.module.file.dto.FileDTO;
import com.linggoutong.server.module.file.entity.FileInfo;
import com.linggoutong.server.module.file.mapper.FileMapper;
import com.linggoutong.server.module.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements FileService {

    @Value("${file.storage-type}")
    private String storageType;

    @Value("${file.local.base-path}")
    private String basePath;

    @Value("${file.local.url-prefix}")
    private String urlPrefix;

    @Override
    @Transactional
    public FileDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED, "文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String fileName = UUID.randomUUID().toString() + "." + extension;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String filePath = datePath + "/" + fileName;

        try {
            Path targetPath = Paths.get(basePath, filePath);
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();

        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(originalName);
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(filePath);
        fileInfo.setFileUrl(urlPrefix + "/" + filePath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setStorageType(storageType);
        fileInfo.setUploaderId(currentUserId);

        save(fileInfo);
        log.info("文件上传成功: {}", originalName);

        return convertToDTO(fileInfo);
    }

    @Override
    @Transactional
    public List<FileDTO> uploadBatch(List<MultipartFile> files) {
        List<FileDTO> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(upload(file));
        }
        return results;
    }

    @Override
    public FileInfo getFileById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        FileInfo fileInfo = getById(id);
        if (fileInfo == null) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND);
        }

        try {
            Path filePath = Paths.get(basePath, fileInfo.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("删除文件失败: {}", e.getMessage());
        }

        removeById(id);
        log.info("文件删除成功: {}", fileInfo.getOriginalName());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private FileDTO convertToDTO(FileInfo fileInfo) {
        FileDTO dto = new FileDTO();
        dto.setId(fileInfo.getId());
        dto.setOriginalName(fileInfo.getOriginalName());
        dto.setFileUrl(fileInfo.getFileUrl());
        dto.setFileSize(fileInfo.getFileSize());
        dto.setFileType(fileInfo.getFileType());
        dto.setCreateTime(fileInfo.getCreateTime() != null ? fileInfo.getCreateTime().toString() : null);
        return dto;
    }
}
