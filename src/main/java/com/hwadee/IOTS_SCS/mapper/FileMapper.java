package com.hwadee.IOTS_SCS.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwadee.IOTS_SCS.entity.POJO.FileInfo;

public interface FileMapper extends BaseMapper<FileInfo> {
    int insertFileInfo(FileInfo fileInfo);
    String isDownloadAllowed(String fileId);
    FileInfo getFileInfo(String fileId);
}
