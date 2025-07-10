package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.response.FileInfoDTO;
import com.hwadee.IOTS_SCS.entity.POJO.FileInfo;
import com.hwadee.IOTS_SCS.mapper.FileMapper;
import com.hwadee.IOTS_SCS.service.FileService;

import com.hwadee.IOTS_SCS.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FileMapper fileMapper;

    @PostMapping
    @ResponseBody
    public CommonResult<FileInfoDTO> upload(MultipartFile file, String file_usage,
                                            @RequestHeader("Authorization") String token,
                                            HttpServletRequest request) {
        return CommonResult.success(fileService.upload(file, file_usage, token , request));
    }

    @GetMapping
    public void download(
            @RequestParam(value="file_id", required = true) String fileId,
            @RequestHeader("Authorization") String token,
            HttpServletResponse response)
            throws IOException {
        // 权限判断
        FileInfo info = fileMapper.getFileInfo(fileId);

        if (info.getFileUsage().contains("lesson") && fileService.isDownloadAllowed(fileId)) {
            response.sendError(403,"教师未开放下载权限");
            return;
        }

        // 下载
        File file = findFileById(fileId);
        if(!file.exists()) {
            response.sendError(404, "文件不存在");
            return;
        }

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        // 流式写入响应
        try (InputStream input = new FileInputStream(file);
             OutputStream output = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
        }
    }

    private File findFileById(String fileId) {
        File dir = new File(System.getProperty("user.dir") + "/res/file/");

        File[] matched = dir.listFiles((d, name) -> name.startsWith(fileId));
        return (matched != null && matched.length > 0) ? matched[0] : new File("");
    }
}
