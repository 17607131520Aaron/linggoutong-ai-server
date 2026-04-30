package com.linggoutong.server.module.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linggoutong.server.module.file.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
}
