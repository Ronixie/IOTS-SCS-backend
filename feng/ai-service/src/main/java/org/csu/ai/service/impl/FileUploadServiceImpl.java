package org.csu.ai.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.ai.service.DocumentService;
import org.csu.ai.service.FileUploadService;
import org.csu.ai.utils.DocumentLoader;
import org.csu.ai.utils.FileUtil;
import org.csu.exception.AIException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    private final DocumentService documentService;
    private final FileUtil fileUtil;

    /**
     * 上传文件
     *
     * @param file   文件
     * @param userId 用户id
     * @return 文件路径
     */
    @Override
    public String uploadFile(MultipartFile file, long userId, String chatId) {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // TODO:后续可使用阿里云OSS存储,返回文件URL
        if (suffixName.equals(".png") || suffixName.equals(".jpg") ||
                suffixName.equals(".jpeg") || suffixName.equals(".gif") || suffixName.equals(".webp")) {
            // 生成目标文件
            String dir = "image/ai";
            fileName = userId + "." + System.currentTimeMillis() + "." + chatId + "." + fileName;
            // 保存文件
            try {
                return fileUtil.compressImage(file, dir, fileName, 500, 500);
            } catch (IOException e) {
                log.error("文件上传失败", e);
                throw new AIException("文件上传失败", e);
            }
        } else if (suffixName.equals(".doc") || suffixName.equals(".docx") || suffixName.equals(".pdf") || suffixName.equals(".txt")) {
            // 生成目标文件
            String dir = "document/ai";
            fileName = userId + "." + System.currentTimeMillis() + "." + chatId + "." + fileName;
            String path = fileUtil.saveFile(file, dir, fileName);
            File tp = new File(path);
            try {
                DocumentLoader dl = new DocumentLoader();
                // 分块处理
                switch (suffixName) {
                    case ".doc" ->
                            FileUtil.extractDocByParagraph(new FileInputStream(tp), 50, takeChunkIndex(chatId, dl));
                    case ".docx" ->
                            FileUtil.extractDocxByParagraph(new FileInputStream(tp), 50, takeChunkIndex(chatId, dl));
                    case ".pdf" -> FileUtil.extractPdfByPage(new FileInputStream(tp), 5, takeChunkIndex(chatId, dl));
                    default -> FileUtil.extractTextByChunk(new FileInputStream(tp), 5000, takeChunkIndex(chatId, dl));
                }
            } catch (Exception e) {
                log.error("文件解析失败", e);
                throw new AIException("文件解析失败", e);
            }
            return "";
        } else {
            log.error("暂不支持该文件格式");
            throw new AIException("暂不支持该文件格式");
        }

    }

    @NotNull
    private Consumer<String> takeChunkIndex(String chatId, DocumentLoader dl) {
        return chunk -> {
            List<String> chunks = dl.splitText(chunk, 1000, 100);
            List<String> buffer = new ArrayList<>();
            for (String text : chunks) {
                buffer.add(text);
                if (buffer.size() == 5) {
                    try {
                        documentService.indexDocuments(buffer, chatId);
                        buffer.clear();
                        Thread.sleep(10);
                    } catch (Exception e) {
                        log.error("文件一些部分解析失败", e);
                    }
                }
            }
            if (!buffer.isEmpty()) {
                try {
                    documentService.indexDocuments(buffer, chatId);
                    buffer.clear();
                } catch (Exception e) {
                    log.error("文件一些部分解析失败", e);
                }
            }
        };
    }
}
