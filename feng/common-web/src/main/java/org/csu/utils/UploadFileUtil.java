package org.csu.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件上传工具类
 */
@Slf4j
public class UploadFileUtil {
    /**
     * 保存文件
     *
     * @param file     源文件
     * @param dir      保存目录
     * @param fileName 保存文件名
     * @return 保存结果
     */
    public static boolean saveFile(MultipartFile file, String dir, String fileName) {
        try {
            // 1. 获取static目录路径（仅开发环境有效）
            File staticDirFile = new ClassPathResource("static/").getFile();
            // 2. 用File的构造函数处理路径，自动适配操作系统分隔符
            File targetDir = new File(staticDirFile, dir); // 等价于 staticDir/dir
            File targetFile = new File(targetDir, fileName); // 等价于 staticDir/dir/fileName

            // 3. 确保目标目录存在（多层目录也会创建）
            if (!targetDir.exists()) {
                boolean isDirCreated = targetDir.mkdirs();
                if (!isDirCreated) {
                    log.error("创建目录失败：{}", targetDir.getAbsolutePath());
                    return false;
                }
            }

            // 4. 处理文件已存在的情况（可选：覆盖或重命名）
            if (targetFile.exists()) {
                log.warn("文件已存在，将覆盖：{}", targetFile.getAbsolutePath());
            }

            // 5. 保存文件
            file.transferTo(targetFile);
            log.info("文件保存成功：{}", targetFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            // 6. 打印异常日志，方便排查
            log.error("文件保存失败", e); // 关键：输出完整异常信息
            return false;
        }
    }
}
