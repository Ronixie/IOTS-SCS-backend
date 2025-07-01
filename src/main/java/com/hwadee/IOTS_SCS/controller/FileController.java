package com.hwadee.IOTS_SCS.controller;

import com.hwadee.IOTS_SCS.common.result.CommonResult;
import com.hwadee.IOTS_SCS.entity.DTO.FileDTO;
import com.hwadee.IOTS_SCS.service.FileService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    @ResponseBody
    public CommonResult<FileDTO> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam("file_type") String fileType,
                                        @RequestParam("related_entity_id") Integer entityId,
                                        HttpServletRequest request) {
        FileDTO info = fileService.upload(file,fileType,entityId,request);
        return CommonResult.success(info);
    }

    @GetMapping("/download/{file_id}")
    public void download(@PathVariable("file_id") String fileId,
                         HttpServletResponse response)
            throws IOException {
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
        File dir = new File("/data/upload/");
        File[] matched = dir.listFiles((d, name) -> name.startsWith(fileId));
        return (matched != null && matched.length > 0) ? matched[0] : new File("");
    }
}
