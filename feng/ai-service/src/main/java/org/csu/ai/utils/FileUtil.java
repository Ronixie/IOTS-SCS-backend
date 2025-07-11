package org.csu.ai.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

@Component
@Slf4j
public class FileUtil {

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 保存文件到外部目录
     *
     * @param file   上传的文件
     * @param subDir 子目录（可选），如 "images"
     * @return 保存后的文件相对路径
     */
    public String saveFile(MultipartFile file, String subDir, String fileName) {
        try {
            if (file.isEmpty()) {
                return null;
            }
            // 确保上传目录存在
            Path uploadDir = Paths.get(uploadPath, subDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // 返回相对路径（用于前端访问）
            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String extractDocxText(InputStream inputStream) throws Exception {
        StringBuilder text = new StringBuilder();
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
        }
        return text.toString();
    }

    public static String extractDocText(InputStream inputStream) throws Exception {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }

    public static String extractPdfText(InputStream inputStream) throws Exception {
        StringBuilder text = new StringBuilder();
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text.append(stripper.getText(document));
        }
        return text.toString();
    }

    public static String extractText(InputStream inputStream) throws Exception {
        byte[] bytes = inputStream.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // 分块读取txt
    public static void extractTextByChunk(InputStream inputStream, int chunkSize, Consumer<String> chunkConsumer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder chunk = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            chunk.append(line).append("\n");
            if (chunk.length() >= chunkSize) {
                chunkConsumer.accept(chunk.toString());
                chunk.setLength(0);
            }
        }
        if (!chunk.isEmpty()) {
            chunkConsumer.accept(chunk.toString());
        }
    }

    // 分块读取pdf
    public static void extractPdfByPage(InputStream inputStream, int pagesPerChunk, Consumer<String> chunkConsumer) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = document.getNumberOfPages();
            for (int start = 1; start <= totalPages; start += pagesPerChunk) {
                int end = Math.min(start + pagesPerChunk - 1, totalPages);
                stripper.setStartPage(start);
                stripper.setEndPage(end);
                String text = stripper.getText(document);
                chunkConsumer.accept(text);
            }
        }
    }

    // 分块读取docx
    public static void extractDocxByParagraph(InputStream inputStream, int paragraphsPerChunk, Consumer<String> chunkConsumer) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            StringBuilder chunk = new StringBuilder();
            int count = 0;
            for (XWPFParagraph paragraph : paragraphs) {
                chunk.append(paragraph.getText()).append("\n");
                count++;
                if (count >= paragraphsPerChunk) {
                    chunkConsumer.accept(chunk.toString());
                    chunk.setLength(0);
                    count = 0;
                }
            }
            if (!chunk.isEmpty()) {
                chunkConsumer.accept(chunk.toString());
            }
        }
    }

    // 分块读取doc
    public static void extractDocByParagraph(InputStream inputStream, int paragraphsPerChunk, Consumer<String> chunkConsumer) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            String[] paragraphs = extractor.getParagraphText();
            StringBuilder chunk = new StringBuilder();
            int count = 0;
            for (String paragraph : paragraphs) {
                chunk.append(paragraph).append("\n");
                count++;
                if (count >= paragraphsPerChunk) {
                    chunkConsumer.accept(chunk.toString());
                    chunk.setLength(0);
                    count = 0;
                }
            }
            if (!chunk.isEmpty()) {
                chunkConsumer.accept(chunk.toString());
            }
        }
    }

    /**
     * 压缩上传的图片并保存到指定路径。
     *
     * @param file         需要压缩的图片文件（MultipartFile类型）
     * @param subDir 目标保存路径的相对路径目录
     * @param fileName     目标文件名
     * @param targetWidth  压缩后的目标宽度（像素）
     * @param targetHeight 压缩后的目标高度（像素）
     * @throws IOException 文件读写或图片处理异常
     */
    public String compressImage(MultipartFile file, String subDir, String fileName, int targetWidth, int targetHeight) throws IOException {
        /* 读取原始图片 */
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        /* 计算压缩比例 */
        double scaleWidth = (double) targetWidth / originalWidth;
        double scaleHeight = (double) targetHeight / originalHeight;
        double scale = Math.min(scaleWidth, scaleHeight);

        /* 计算新的尺寸 */
        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        /* 创建新的 BufferedImage 并设置渲染参数 */
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

// 确保上传目录存在
        Path uploadDir = Paths.get(uploadPath, subDir);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 保存文件
        Path filePath = uploadDir.resolve(fileName);
        String path = filePath.toAbsolutePath().toString();

        // 创建输出目录
        File outputFile = new File(path);
        /* 保存图片（保存为 PNG 格式） */
        ImageIO.write(resizedImage, "png", outputFile);
        return path;
    }
}